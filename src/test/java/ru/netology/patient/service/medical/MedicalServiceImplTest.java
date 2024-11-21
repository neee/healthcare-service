package ru.netology.patient.service.medical;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MedicalServiceImplTest {
    private PatientInfoRepository patientInfoRepository;
    private SendAlertServiceImpl alertService;
    private MedicalServiceImpl medicalService;
    ArgumentCaptor<String> argument;


    @Before
    public void setUp() {
        this.patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        when(patientInfoRepository.getById("82642464-0f0a-4efc-b304-30259e6d78a2"))
                .thenReturn(new PatientInfo("82642464-0f0a-4efc-b304-30259e6d78a2", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(BigDecimal.valueOf(36.65), new BloodPressure(80, 120))));

        this.alertService = Mockito.mock(SendAlertServiceImpl.class);
        this.medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        this.argument = ArgumentCaptor.forClass(String.class);
    }


    @Test
    public void checkBloodPressure_needHelp() { //Проверить вывод сообщения во время проверки давления

        BloodPressure currentPressure = new BloodPressure(60, 120);
        medicalService.checkBloodPressure("82642464-0f0a-4efc-b304-30259e6d78a2", currentPressure);
        verify(alertService).send(argument.capture());
        System.out.println();
    }

    @Test
    public void checkTemperature_needHelp() {

        BigDecimal currentTemperature = new BigDecimal("30.0");
        medicalService.checkTemperature("82642464-0f0a-4efc-b304-30259e6d78a2", currentTemperature);
        verify(alertService).send(argument.capture());
    }

    @Test
    public void checkBloodPressure_noMessage() { //Проверить вывод сообщения во время проверки давления

        BloodPressure currentPressure = new BloodPressure(80, 120);
        medicalService.checkBloodPressure("82642464-0f0a-4efc-b304-30259e6d78a2", currentPressure);
        verify(alertService, Mockito.never()).send(argument.capture());
    }

    @Test
    public void checkTemperature_noMessage() {

        BigDecimal currentTemperature = new BigDecimal("36.6");
        medicalService.checkTemperature("82642464-0f0a-4efc-b304-30259e6d78a2", currentTemperature);
        verify(alertService, Mockito.never()).send(argument.capture());
    }


}
