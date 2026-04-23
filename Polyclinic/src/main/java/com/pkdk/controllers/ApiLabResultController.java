/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.enums.UserRole;
import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.LabResults;
import com.pkdk.pojo.Users;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.LabResultService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
public class ApiLabResultController {
    
    @Autowired
    private LabResultService labResultService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/api/appointments/{appointmentId}/lab-results")
    public ResponseEntity<?> getByAppointment(@PathVariable("appointmentId") int appointmentId){
        Appointments a = this.appointmentService.getById(appointmentId);
        if (a==null)
            return new ResponseEntity<>("Không tìm thấy lịch hẹn phù hợp", HttpStatus.NOT_FOUND);
        List<LabResults> list = this.labResultService.getByAppointmentId(appointmentId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/appointments/{appointmentId}/lab-results")
    public ResponseEntity<?> createLabResult(@PathVariable("appointmentId") int appointmentId,
            @RequestBody LabResults labResult, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (caller == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền tạo kết quả xét nghiệm",HttpStatus.FORBIDDEN);
        
        Appointments a = this.appointmentService.getById(appointmentId);
        if (a==null)
            return new ResponseEntity<>("Không tìm thấy lịch hẹn phù hợp", HttpStatus.NOT_FOUND);
        
        labResult.setId(null);
        labResult.setAppointmentId(a);
        labResult.setNgayTao(new Date());
        this.labResultService.save(labResult);
        return new ResponseEntity<>(labResult, HttpStatus.CREATED);
    }
    
    @PutMapping("/api/secure/lab-results/{id}")
    public ResponseEntity<?> updateLabResult(@PathVariable("id") int id,
            @RequestBody LabResults labResult, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (caller == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền sửa kết quả xét nghiệm", HttpStatus.FORBIDDEN);
        
        LabResults l = this.labResultService.getById(id);
        if (l == null)
            return new ResponseEntity<>("Không tìm thấy kết quả xét nghiệm hợp lệ",HttpStatus.NOT_FOUND);
        
        l.setTestName(labResult.getTestName());
        l.setTestCode(labResult.getTestCode());
        l.setResult(labResult.getResult());
        l.setUnit(labResult.getUnit());
        l.setIsAbnormal(labResult.getIsAbnormal());
        l.setPerformedAt(labResult.getPerformedAt());
        this.labResultService.save(l);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }
    
}
