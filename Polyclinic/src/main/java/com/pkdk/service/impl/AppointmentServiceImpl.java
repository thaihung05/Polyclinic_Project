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
    public List<Appointments> getByDoctorId(int doctorId) {
        return this.appointmentRepo.getByDoctorId(doctorId);
    }

    @Override
    public List<Appointments> getByPatientId(int patientId) {
        return this.appointmentRepo.getByPatientId(patientId);
    }

    @Override
    public Appointments getById(int id) {
        return this.appointmentRepo.getById(id);
    }

    @Override
    public void save(Appointments appointment) {
        this.appointmentRepo.save(appointment);
    }

    @Override
    public Appointments book(int doctorId, int scheduleId, int patientId, String symptoms) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
