/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Patients;
import com.pkdk.repository.PatientRepository;
import com.pkdk.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PatientServiceImpl implements PatientService{
    
    @Autowired
    private PatientRepository patientRepo;

    @Override
    public Patients getPatientByUserId(int userId) {
        return this.patientRepo.getPatientByUserId(userId);
    }

    @Override
    public void addOrUpdate(Patients p) {
        this.patientRepo.addOrUpdate(p);
    }

    @Override
    public void deleteByUserId(int userId) {
        this.patientRepo.deleteByUserId(userId);
    }
    
}
