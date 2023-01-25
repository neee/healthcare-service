import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

public class MedicalServiceImplTest {

    public static Stream<Arguments> sourceAdd() {
        PatientInfo patientInfo1 = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36"), new BloodPressure(120, 60)));

        PatientInfo patientInfo2 = new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("40"), new BloodPressure(125, 78)));
        return Stream.of(Arguments.of("1", patientInfo1), Arguments.of("2", patientInfo2));
    }

    //todo:
    // 1.   Этот тест работает нормально, на мой взгляд. Но при вызове метода checkTemperature()
    // на 32 строчке класса MedicalServiceImpl происходит обращение к объектам дргугих классов.
    // Из-за этого данный тест является по сути интеграционным, так как зависит от разных классов.
    // Чтобы исключить все зависимости от дргих классов, можно сделать заглушки для каждого класса в этой цепочке.
    // Есть ли более оптимальный способ? Чтобы на найти более оптимальный способ, я попробовал сделать
    // заглушку для вызва Mockito.when(patientInfo.getHealthInfo().getNormalTemperature()).thenReturn(new BigDecimal(currentTemperature));
    // Но так не работает (тест с этим кодом ниже закоментирован)
    // Аналогичная проблема и в testCheckBloodPressure.
    // Как можно сделать данный тест модульным на 100% ?

    //todo:
    // 2.   В консоли появляется следующее сообщение:
    // OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
    // Наверное, это связано с некорректным подключением плагина в maven-compiler-plugin.
    // Как это устранить и для чего этот плагин нужен? Раньше мы его не использовали.




    @ParameterizedTest
    @MethodSource("sourceAdd")
    public void testCheckTemperature(String id, PatientInfo patientInfo) {
        BigDecimal extremalTemperature = new BigDecimal(38);
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        medicalService.checkTemperature(id, extremalTemperature);

        if (id.equals("1")) {
            Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
        }
        if (id.equals("2")) {
            Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
        }
    }

//Этот вариант теста не работает
//    @ParameterizedTest
//    @ValueSource(ints = {36, 40})
//    public void testCheckTemperature(int currentTemperature) {
//        BigDecimal temperature = new BigDecimal(38);
//        PatientInfo patientInfo = Mockito.mock(PatientInfo.class);
//        Mockito.when(patientInfo.getHealthInfo().getNormalTemperature()).thenReturn(new BigDecimal(currentTemperature));
//        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
//        Mockito.when(patientInfoRepository.getById(Mockito.anyString())).thenReturn(patientInfo);
//        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
//        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
//
//        medicalService.checkTemperature(Mockito.anyString(), temperature);
//
//        if (currentTemperature == 36) {
//            Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
//        }
//        if (currentTemperature == 40) {
//            Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
//        }
//    }

    @ParameterizedTest
    @MethodSource("sourceAdd")
    public void testCheckBloodPressure(String id, PatientInfo patientInfo) {
        BloodPressure etalonBloodPressure = new BloodPressure(120, 60);
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        medicalService.checkBloodPressure(id, etalonBloodPressure);

        if (id.equals("1")) {
            Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
        }
        if (id.equals("2")) {
            Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
        }
    }
}
