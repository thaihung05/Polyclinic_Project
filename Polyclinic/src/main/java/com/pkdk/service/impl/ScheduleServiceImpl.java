/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.repository.ScheduleRepository;
import com.pkdk.service.ScheduleService;
import java.util.Date;
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
        
        if (schedule.getStartTime().after(schedule.getEndTime()))
            throw new RuntimeException("Giờ không hợp lệ!");
        
        int doctorId = schedule.getDoctorId().getId();
        if (this.scheduleRepo.isOverlap(doctorId, schedule.getStartTime(), schedule.getEndTime(), schedule.getId()))
            throw new RuntimeException("Lịch làm việc trùng với lịch đã có!");
        
        this.scheduleRepo.save(schedule);
    }

    @Override
    public void delete(int id) {
        this.scheduleRepo.delete(id);
    }

    @Override
    public void deactivate(DoctorSchedules schedule) {
        schedule.setIsActive(false);
        this.scheduleRepo.save(schedule);
    }

    @Override
    public DoctorSchedules getByDoctorAndStartTime(int doctorId, Date startTime) {
        return this.scheduleRepo.getByDoctorAndStartTime(doctorId, startTime);
    }

    @Override
    public void reactivate(DoctorSchedules schedules) {
        schedules.setIsActive(true);
        this.scheduleRepo.save(schedules);
    }

    @Override
    public DoctorSchedules getByIdWithLock(int id) {
        return this.scheduleRepo.getByIdWithLock(id);
    }
    
}
