import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MedicalServiceImplTest {
    private final PatientInfoRepository patientInfoMock = Mockito.mock(PatientInfoRepository.class);
    private final SendAlertService alertServiceMock = Mockito.mock(SendAlertService.class);
    private final MedicalService medicalServiceTest = new MedicalServiceImpl(patientInfoMock, alertServiceMock);
    private final String patientId = "patient#1";
    private final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    private final String exected = String.format("Warning, patient with id: %s, need help", null);
    private final PatientInfo patientTest = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));

    @BeforeEach
    void mockitoWhenThen() {
        Mockito.when(patientInfoMock.getById(patientId)).thenReturn(patientTest);
    }

    @Test
    void testCheckBloodPressure() {
        medicalServiceTest.checkBloodPressure(patientId, new BloodPressure(190, 100));
        Mockito.verify(alertServiceMock).send(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), exected);
    }

    @Test
    void testCheckTemperature() {
        medicalServiceTest.checkTemperature(patientId, new BigDecimal("32.1"));
        Mockito.verify(alertServiceMock).send(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), exected);
    }

    @Test
    void testNormalTemperature() {
        medicalServiceTest.checkTemperature(patientId, patientTest.getHealthInfo().getNormalTemperature());
        Mockito.verify(alertServiceMock, Mockito.never()).send(exected);
    }
    @Test
    void testNormalBloodPressure() {
        medicalServiceTest.checkBloodPressure(patientId, patientTest.getHealthInfo().getBloodPressure());
        Mockito.verify(alertServiceMock, Mockito.never()).send(exected);
    }

}
