/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.MedicalRecords;
import com.pkdk.repository.MedicalRecordRepository;
import com.pkdk.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class MedicalRecordServiceImpl implements MedicalRecordService{
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepo;

    @Override
    public MedicalRecords getByAppointmentId(int appointmentId) {
        return this.medicalRecordRepo.getByAppointmentId(appointmentId);
    }

    @Override
    public MedicalRecords getById(int id) {
        return this.medicalRecordRepo.getById(id);
    }

    @Override
    public void save(MedicalRecords record) {
        this.medicalRecordRepo.save(record);
    }
    
}
