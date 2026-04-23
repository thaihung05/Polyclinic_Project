/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.LabResults;
import com.pkdk.pojo.MedicalRecords;
import com.pkdk.repository.MedicalHistoryRepository;
import com.pkdk.service.MedicalHistoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vanlong
 */
@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService{

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepo;
    
    @Override
    public List<MedicalRecords> getMedicalRecordsByPatientId(int patientId) {
        return this.medicalHistoryRepo.getMedicalRecordsByPatientId(patientId);
    }

    @Override
    public List<LabResults> getLabResultsByPatientId(int patientId) {
        return this.medicalHistoryRepo.getLabResultsByPatientId(patientId);
    }
    
}
