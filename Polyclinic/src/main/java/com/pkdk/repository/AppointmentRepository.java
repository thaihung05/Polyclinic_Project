/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.Appointments;
import java.util.List;

/**
 *
 * @author vanlong
 */
public interface AppointmentRepository {
    List<Appointments> getByPatientId(int patientId);
    void save(Appointments appointment);
    void delete(int id);
    Appointments getById(int id);
}
