package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

public class MedicalServiceTest {

    @ParameterizedTest
    @MethodSource("providesForCheckBloodPressure")
    public void shouldCheckBloodPressure(String id, BloodPressure bloodPressure) {

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(id))
                .thenReturn(new PatientInfo("123", "Ivan", "Ivanov",
                        LocalDate.of(1970, 1, 1),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))));

        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        medicalService.checkBloodPressure(id, bloodPressure);

        Mockito.verify(sendAlertService, Mockito.times(1)).send(Mockito.anyString());
    }

    public static Stream<Arguments> providesForCheckBloodPressure() {
        return Stream.of(Arguments.of("123", new BloodPressure(120, 80)),
                Arguments.of("123", new BloodPressure(130, 90))
        );
    }

    @ParameterizedTest
    @MethodSource("providesForCheckTemperature")
    public void shouldCheckTemperature(String id, BigDecimal temperature) {

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(id))
                .thenReturn(new PatientInfo("123", "Ivan", "Ivanov",
                        LocalDate.of(1970, 1, 1),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))));

        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        medicalService.checkTemperature(id, temperature);

        Mockito.verify(sendAlertService, Mockito.times(1)).send(Mockito.anyString());
    }

    public static Stream<Arguments> providesForCheckTemperature() {
        return Stream.of(Arguments.of("123", new BigDecimal("36.6")),
                Arguments.of("123", new BigDecimal("35.0")),
                Arguments.of("123", new BigDecimal("38.2"))
        );
    }

    @ParameterizedTest
    @MethodSource("providesForCaptureMessageCheckTemperature")
    public void shouldCaptureMessageCheckTemperature(String id, BigDecimal temperature) {

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(id))
                .thenReturn(new PatientInfo("123", "Ivan", "Ivanov",
                        LocalDate.of(1970, 1, 1),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))));

        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        medicalService.checkTemperature(id, temperature);

        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 123, need help", argumentCaptor.getValue());
    }

    public static Stream<Arguments> providesForCaptureMessageCheckTemperature() {
        return Stream.of(Arguments.of("123", new BigDecimal("36.6")),
                Arguments.of("123", new BigDecimal("35.0")),
                Arguments.of("123", new BigDecimal("38.2")),
                Arguments.of("123", new BigDecimal("40.2"))
        );
    }
}
