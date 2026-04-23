/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.LabResults;
import com.pkdk.repository.LabResultRepository;
import com.pkdk.service.LabResultService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class LabResultServiceImpl implements LabResultService{
    
    @Autowired
    private LabResultRepository labResultRepo;

    @Override
    public List<LabResults> getByAppointmentId(int appointmentId) {
        return this.labResultRepo.getByAppointmentId(appointmentId);
    }

    @Override
    public LabResults getById(int id) {
        return this.labResultRepo.getById(id);
    }

    @Override
    public void save(LabResults labResult) {
        this.labResultRepo.save(labResult);
    }
    
}
