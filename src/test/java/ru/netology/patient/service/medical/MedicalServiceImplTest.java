package ru.netology.patient.service.medical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class MedicalServiceImplTest {
    private MedicalServiceImpl medicalService;
    private PatientInfoRepository patientInfoRepository;
    private SendAlertService sendAlertService;

    private final String id = "1";
    private final BigDecimal objTemperature = new BigDecimal("36.65");
    private final BloodPressure objBloodPressure = new BloodPressure(120, 80);
    private final PatientInfo patientInfo = new PatientInfo(null, null, null,
            new HealthInfo(objTemperature, objBloodPressure));

    @BeforeEach
    void init() {
        patientInfoRepository = mock(PatientInfoRepository.class);
        sendAlertService = mock(SendAlertService.class);
        medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        given(patientInfoRepository.getById(id)).willReturn(patientInfo);

    }

    @Test
    void shouldWorkWithoutDisplayingInformationWhenCheckingThePressure() {
        BloodPressure objectBloodPressure = objBloodPressure;

        medicalService.checkBloodPressure(id, objectBloodPressure);

        verify(sendAlertService, never()).send(anyString());
    }

    @Test
    void shouldDisplayMessageWhenCheckingPressure() {
        BloodPressure currentBloodPressure = new BloodPressure(100, 100);

        medicalService.checkBloodPressure(id, currentBloodPressure);
        verify(sendAlertService, times(1)).send(anyString());
    }

    @Test
    void shouldWorkWithoutDisplayingInformationWhenCheckingTheTemperature() {
        BigDecimal currentTemperature = objTemperature;

        medicalService.checkTemperature(id, currentTemperature);
        verify(sendAlertService, never()).send(anyString());
    }

    @Test
    void shouldDisplayMessageWhenCheckingTheTemperature() {
        BigDecimal currentTemperature = new BigDecimal("32");

        medicalService.checkTemperature(id, currentTemperature);
        verify(sendAlertService, times(1)).send(anyString());
    }

}