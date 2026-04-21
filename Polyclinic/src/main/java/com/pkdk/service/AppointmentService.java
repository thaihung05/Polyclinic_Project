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
    List<Appointments> getDoctorId(int doctorId);
    List<Appointments> getPatientId(int patientId);
    Appointments getById(int id);
    void save(Appointments appointment);
}
