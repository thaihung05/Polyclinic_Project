/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.repository.ScheduleRepository;
import com.pkdk.service.ScheduleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class ScheduleServiceImpl implements ScheduleService{
    
    @Autowired
    private ScheduleRepository scheduleRepo;

    @Override
    public List<DoctorSchedules> getByDoctorId(int doctorId) {
       return this.scheduleRepo.getDoctorById(doctorId);
    }

    @Override
    public DoctorSchedules getById(int id) {
        return this.scheduleRepo.getById(id);
    }

    @Override
    public void save(DoctorSchedules schedule) {
        this.scheduleRepo.save(schedule);
    }

    @Override
    public void delete(int id) {
        this.scheduleRepo.delete(id);
    }
    
}
