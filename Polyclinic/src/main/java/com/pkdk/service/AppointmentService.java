/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Appointments;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface AppointmentService {
    List<Appointments> getByDoctorId(int doctorId);
    List<Appointments> getByPatientId(int patientId);
    Appointments getById(int id);
    void save(Appointments appointment);
    Appointments book(int doctorId, int scheduleId, int patientId, String symptoms);
}
