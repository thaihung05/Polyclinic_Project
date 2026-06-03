/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Appointments;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface AppointmentService {
    List<Appointments> getAll(String kw, String status, String fromDate, String toDate, int page);
    long countAll(String kw, String status, String fromDate, String toDate);
    List<Appointments> getByDoctorId(int doctorId);
    List<Appointments> getByPatientId(int patientId);
    Appointments getById(int id);
    void save(Appointments appointment);
    Appointments book(int doctorId, int scheduleId, int patientId, String symptoms);
    boolean existsByPatientAndTime(int patientId, java.util.Date scheduledAt);
    boolean existsByPatientDoctorAndDate(int patientId, int doctorId, Date date);
    Appointments cancelAppointments(int appointmentId, String cancelReason, String cancelBy);
    Appointments finishAppointments(int appointmentId, String newStatus);
    Appointments addMeetingUrl(int appointmentId, String meetingUrl);
}