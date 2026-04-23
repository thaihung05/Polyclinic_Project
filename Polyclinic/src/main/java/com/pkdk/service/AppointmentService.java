/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Appointments;
import java.util.List;

/**
 *
 * @author vanlong
 */
public interface AppointmentService {
    Appointments book(int doctorId, int scheduleId, int patientId, String symptoms);
    List<Appointments> getByPatientId(int patientId);
    Appointments getById(int id);
}
