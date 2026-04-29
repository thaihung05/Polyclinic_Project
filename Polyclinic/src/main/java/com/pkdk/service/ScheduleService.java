/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.DoctorSchedules;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface ScheduleService {
    List<DoctorSchedules> getByDoctorId(int doctorId);
    DoctorSchedules getById(int id);
    void save(DoctorSchedules schedule);
    void reactivate(DoctorSchedules schedules);
    void deactivate(DoctorSchedules schedule);
    void delete(int id);
    DoctorSchedules getByDoctorAndStartTime(int doctorId, Date startTime);
}
