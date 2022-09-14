package service.medical;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MedicalServiceImplTests {
    static PatientInfo patientInfo;

    @BeforeAll
    public static void NewPatientInfo() {
        patientInfo = new PatientInfo("1", "Ivan", "Petrov", LocalDate.of(1997, 7, 31),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 70)));
    }

    @ParameterizedTest
    @MethodSource ("sourcePressure")
    public void testCheckBloodPressure(BloodPressure bloodPressure, int times) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);


        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure("1", bloodPressure);

        Mockito.verify(sendAlertService, Mockito.times(times)).send(Mockito.any());
    }

    static Stream<Arguments> sourcePressure() {
        return Stream.of(
                arguments(new BloodPressure(130, 80), 1),
                arguments(new BloodPressure(140, 70), 1),
                arguments(new BloodPressure(120, 70), 0)
        );
    }

    @ParameterizedTest
    @MethodSource ("sourceTemperature")
    public void testCheckTemperature(BigDecimal temperature, int times) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);


        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkTemperature("1", temperature);

        Mockito.verify(sendAlertService, Mockito.times(times)).send(Mockito.any());
    }

    static Stream<Arguments> sourceTemperature() {
        return Stream.of(
                arguments(new BigDecimal("37.6"), 1),
                arguments(new BigDecimal("40.6"), 1),
                arguments(new BigDecimal("34.3"), 1),
                arguments(new BigDecimal("36.6"), 0)
        );
    }

}
