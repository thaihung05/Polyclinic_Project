/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.Appointments;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface AppointmentRepository {
    List<Appointments> getAll(String kw, String status, String fromDate, String toDate, int page);
    long countAll(String kw, String status, String fromDate, String toDate);
    List<Appointments> getByDoctorId(int doctorId);
    List<Appointments> getByPatientId(int patientId);
    Appointments getById(int id);
    void save(Appointments appointment);
    boolean existsByPatientAndTime(int patientId, java.util.Date scheduledAt);
}

