/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.DoctorSchedules;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface ScheduleRepository {
    List<DoctorSchedules> getDoctorById(int doctorId);
    DoctorSchedules getById(int id);
    void save(DoctorSchedules schedule);
    void delete(int id);
}
