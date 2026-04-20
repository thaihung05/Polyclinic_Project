/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.pojo.Doctors;
import com.pkdk.service.DoctorService;
import com.pkdk.service.ScheduleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api/doctors/{doctorId}/schedules")
public class ApiScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<?> getSchedules(@PathVariable("doctorId") int doctorId){
        Doctors d = this.doctorService.getDoctorById(doctorId);
        if (d == null)
            return new ResponseEntity<>("Không tìm thấy bác sĩ hợp lệ", HttpStatus.NOT_FOUND);
        List<DoctorSchedules> s = this.scheduleService.getByDoctorId(doctorId);
        
        return new ResponseEntity<>(s,HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> addSchedule(@PathVariable("doctorId") int doctorId, @RequestBody DoctorSchedules s){
        Doctors d = this.doctorService.getDoctorById(doctorId);
        if (d == null)
            return new ResponseEntity<>("Không tìm thấy bác sĩ hợp lệ", HttpStatus.NOT_FOUND);
        s.setId(null);
        s.setDoctorId(d);
        this.scheduleService.save(s);
        return new ResponseEntity<>(s, HttpStatus.CREATED);
    }
    
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedules(@PathVariable("doctorId") int doctorId,
            @PathVariable("scheduleId") int scheduleId, @RequestBody DoctorSchedules s){
        DoctorSchedules schedules = this.scheduleService.getById(scheduleId);
        if (schedules == null)
            return new ResponseEntity<>("Không tìm thấy lịch làm việc hợp lệ", HttpStatus.NOT_FOUND);
        schedules.setStartTime(s.getStartTime());
        schedules.setEndTime(s.getEndTime());
        schedules.setIsActive(s.getIsActive());
        
        this.scheduleService.save(schedules);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }
    
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedules(@PathVariable("doctorId") int doctorId,
            @PathVariable("scheduleId") int scheduleId){
        DoctorSchedules schedules = this.scheduleService.getById(scheduleId);
        if (schedules==null)
            return new ResponseEntity<>("Không tìm thấy lịch làm việc hợp lệ",HttpStatus.NOT_FOUND);
        this.scheduleService.delete(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }
    
}
