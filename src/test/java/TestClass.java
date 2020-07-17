import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestClass {
//    Проверить вывод сообщения во время проверки давления checkBloodPressure
//    Проверить вывод сообщения во время проверки температуры checkTemperature
//    Проверить, что сообщения не выводятся, когда показатели в норме.

    PatientInfoFileRepository repository = Mockito.mock(PatientInfoFileRepository.class);
    SendAlertService service = Mockito.mock(SendAlertService.class);
    MedicalService medicalService = new MedicalServiceImpl(repository, service);
    BloodPressure currentPressure = new BloodPressure(60, 120);
    BigDecimal currentTemperature = new BigDecimal("38.2");

    @Test
    @DisplayName("Проверка вывода сообщения во время проверки давления checkBloodPressure")
    public void test_checkBloodPressure(){
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(repository.getById(Mockito.anyString())).thenReturn(new PatientInfo("id1","Иван", "Петров",
                LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))));

        medicalService.checkBloodPressure("id1", currentPressure);

        Mockito.verify(service, Mockito.times(1)).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Проверка вывода сообщения во время проверки температуры checkTemperature")
    public void test_checkTemperature(){
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(repository.getById(Mockito.anyString())).thenReturn(new PatientInfo("id1","Иван", "Петров",
                LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))));

        medicalService.checkTemperature("id1", currentTemperature);

        Mockito.verify(service, Mockito.times(1)).send(argumentCaptor.capture());

        Assertions.assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());
    }


    @Test
    @DisplayName("Проверка отсутствия сообщений при нормальных показателях")
    public void test_NoMessages(){
        BloodPressure currentPressure1 = new BloodPressure(120, 80);
        BigDecimal currentTemperature1 = new BigDecimal("36.6");

        Mockito.when(repository.getById(Mockito.anyString())).thenReturn(new PatientInfo("id1","Иван", "Петров",
                LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))));

        medicalService.checkBloodPressure("id1", currentPressure1);
        medicalService.checkTemperature("id1", currentTemperature1);

        Mockito.verify(service, Mockito.times(0)).send(Mockito.anyString());
    }
}
