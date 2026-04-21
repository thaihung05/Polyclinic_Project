/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Appointments;
import com.pkdk.repository.AppointmentRepository;
import com.pkdk.service.AppointmentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class AppointmentServiceImpl implements AppointmentService{
    
    @Autowired
    private AppointmentRepository appointmentRepo;

    @Override
    public List<Appointments> getDoctorId(int doctorId) {
        return this.appointmentRepo.getDoctorId(doctorId);
    }

    @Override
    public List<Appointments> getPatientId(int patientId) {
        return this.appointmentRepo.getPatientId(patientId);
    }

    @Override
    public Appointments getById(int id) {
        return this.appointmentRepo.getById(id);
    }

    @Override
    public void save(Appointments appointment) {
        this.appointmentRepo.save(appointment);
    }
    
}
