import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class MedicalServiceImplTest {

    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
    PatientInfo patientInfo = Mockito.mock(PatientInfo.class);
    HealthInfo healthInfo = Mockito.mock(HealthInfo.class);
    MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    public static Stream<Arguments> sourceAdd() {
        return Stream.of(Arguments.of("1111", new BigDecimal(36), new BloodPressure(120, 60)),
                Arguments.of("2222", new BigDecimal(40), new BloodPressure(110, 60)));
    }

    @ParameterizedTest
    @MethodSource("sourceAdd")
    public void testCheckTemperature(String id, BigDecimal normalTemperature) {
        String expectedMassage = "Warning, patient with id: " + id + ", need help";
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);
        Mockito.when(patientInfo.getHealthInfo()).thenReturn(healthInfo);
        Mockito.when(healthInfo.getNormalTemperature()).thenReturn(normalTemperature);
        BigDecimal extremalTemperature = new BigDecimal(38);
        Mockito.when(patientInfo.getId()).thenReturn(id);

        medicalService.checkTemperature(id, extremalTemperature);
        if (id.equals("1111")) {
            Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
        }

        if (id.equals("2222")) {
            Mockito.verify(sendAlertService).send(argumentCaptor.capture());
            Assertions.assertEquals(expectedMassage, argumentCaptor.getValue());
        }
    }

    @ParameterizedTest
    @MethodSource("sourceAdd")
    public void testCheckBloodPressure(String id, BigDecimal bigDecimal, BloodPressure bloodPressure) {
        String expectedMassage = "Warning, patient with id: " + id + ", need help";
        BloodPressure etalonBloodPressure = new BloodPressure(120, 60);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);
        Mockito.when(patientInfo.getHealthInfo()).thenReturn(healthInfo);
        Mockito.when(healthInfo.getBloodPressure()).thenReturn(bloodPressure);
        Mockito.when(patientInfo.getId()).thenReturn(id);

        medicalService.checkBloodPressure(id, etalonBloodPressure);

        if (bloodPressure.equals(etalonBloodPressure)) {
            Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
        }
        if (!bloodPressure.equals(etalonBloodPressure)) {
            Mockito.verify(sendAlertService).send(argumentCaptor.capture());
            Assertions.assertEquals(expectedMassage, argumentCaptor.getValue());
        }
    }
}