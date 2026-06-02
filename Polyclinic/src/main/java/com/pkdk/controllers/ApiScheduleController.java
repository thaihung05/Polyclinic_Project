/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.enums.UserRole;
import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Users;
import com.pkdk.service.DoctorService;
import com.pkdk.service.ScheduleService;
import com.pkdk.service.UserService;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
public class ApiScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/api/doctors/{doctorId}/schedules")
    public ResponseEntity<?> getSchedules(@PathVariable("doctorId") int doctorId){
        Doctors d = this.doctorService.getDoctorById(doctorId);
        if (d == null)
            return new ResponseEntity<>("Không tìm thấy bác sĩ hợp lệ", HttpStatus.NOT_FOUND);
        List<DoctorSchedules> s = this.scheduleService.getByDoctorId(doctorId);
        
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/doctors/{doctorId}/schedules")
    public ResponseEntity<?> addSchedule(@PathVariable("doctorId") int doctorId, 
            @RequestBody DoctorSchedules s, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        
        if (caller == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền thêm lịch làm việc",HttpStatus.FORBIDDEN);
        
        Doctors callerDoctor = this.doctorService.getDoctorByUserId(caller.getId());
        if (callerDoctor == null || callerDoctor.getId()!=doctorId)
            return new ResponseEntity<>("Bác sĩ chỉ có quyền thêm lịch của chính mình",HttpStatus.FORBIDDEN);
        
        Doctors d = this.doctorService.getDoctorById(doctorId);
        if (d == null)
            return new ResponseEntity<>("Không tìm thấy bác sĩ hợp lệ", HttpStatus.NOT_FOUND);
        s.setId(null);
        s.setDoctorId(d);
        s.setIsActive(true);
        try {
            this.scheduleService.save(s);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(s, HttpStatus.CREATED);
    }
    
    @PutMapping("/api/secure/doctors/{doctorId}/schedules/{scheduleId}")
    public ResponseEntity<?> updateSchedules(@PathVariable("doctorId") int doctorId,
            @PathVariable("scheduleId") int scheduleId, @RequestBody DoctorSchedules s, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (caller == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền sửa lịch làm việc", HttpStatus.FORBIDDEN);
        
        Doctors callerDoctor = this.doctorService.getDoctorByUserId(caller.getId());
        if (callerDoctor == null || callerDoctor.getId()!=doctorId)
            return new ResponseEntity<>("Bác sĩ chỉ có quyền sửa lịch của chính mình",HttpStatus.FORBIDDEN);
        
        DoctorSchedules schedules = this.scheduleService.getById(scheduleId);
        if (schedules == null)
            return new ResponseEntity<>("Không tìm thấy lịch làm việc hợp lệ", HttpStatus.NOT_FOUND);
        if (schedules.getDoctorId().getId()!=doctorId)
            return new ResponseEntity<>("Lịch làm việc không thuộc bác sĩ này",HttpStatus.FORBIDDEN);
        
        boolean timeChanged = !s.getStartTime().equals(schedules.getStartTime()) || !s.getEndTime().equals(schedules.getEndTime());
        
        if (timeChanged && !schedules.getIsActive())
            return new ResponseEntity<>("Không thể thay đổi giờ của lịch đã được bệnh nhân đặt", HttpStatus.BAD_REQUEST);
        
        schedules.setStartTime(s.getStartTime());
        schedules.setEndTime(s.getEndTime());
        schedules.setIsActive(s.getIsActive());
        
        this.scheduleService.save(schedules);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }
    
    @DeleteMapping("/api/secure/doctors/{doctorId}/schedules/{scheduleId}")
    public ResponseEntity<?> deleteSchedules(@PathVariable("doctorId") int doctorId,
            @PathVariable("scheduleId") int scheduleId, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (caller == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền xóa lịch làm việc", HttpStatus.FORBIDDEN);
        
        Doctors callerDoctor = this.doctorService.getDoctorByUserId(caller.getId());
        if (callerDoctor == null || callerDoctor.getId()!=doctorId)
            return new ResponseEntity<>("Bác sĩ chỉ có quyền xóa lịch của chính mình",HttpStatus.FORBIDDEN);
        
        DoctorSchedules schedules = this.scheduleService.getById(scheduleId);
        if (schedules == null)
            return new ResponseEntity<>("Không tìm thấy lịch làm việc hợp lệ",HttpStatus.NOT_FOUND);
        
        if (schedules.getDoctorId().getId() != doctorId)
            return new ResponseEntity<>("Lịch làm việc không thuộc về bác sĩ này", HttpStatus.FORBIDDEN);
        
        if (!schedules.getIsActive())
            return new ResponseEntity<>("Không thể xóa lịch đã được bệnh nhân đặt", HttpStatus.BAD_REQUEST);

        this.scheduleService.delete(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }
    
}
