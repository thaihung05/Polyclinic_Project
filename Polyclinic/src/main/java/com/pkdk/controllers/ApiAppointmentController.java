/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Users;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.PatientService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vanlong
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiAppointmentController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    
    @PostMapping("/secure/appointments")
    public ResponseEntity<?> bookAppointment(
            Principal principal,
            @RequestBody Map<String, Object> body) {
        
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null){
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
        }
        
        Patients p = this.patientService.getPatientByUserId(u.getId());
        if(p==null){
            return new ResponseEntity<>("Không tìm thấy thông tin bệnh nhân", HttpStatus.BAD_REQUEST);
        }
        
        Integer doctorId = (Integer) body.get("doctorId");
        Integer scheduleId = (Integer) body.get("scheduleId");
        String symptoms = (String) body.get("symptoms");
        
        symptoms=symptoms!=null?symptoms.trim():null;
        
        if (doctorId == null)
            return new ResponseEntity<>("Thiếu thông tin doctorId", HttpStatus.BAD_REQUEST);
        if(scheduleId == null)
            return new ResponseEntity<>("Thiếu thông tin scheduleId", HttpStatus.BAD_REQUEST);
 
        try {
            Appointments appt = appointmentService.book(doctorId, scheduleId, p.getId(), symptoms);
            return new ResponseEntity<>(appt, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>("Lỗi tạo lịch hẹn: "+ ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
    
    @GetMapping("/secure/appointments")
    public ResponseEntity<?> getMyAppointments(Principal principal) {
        Users u = userService.getUserByUserName(principal.getName());
        if (u == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
 
        Patients patient = patientService.getPatientByUserId(u.getId());
        if (patient == null)
            return new ResponseEntity<>("Tài khoản không phải bệnh nhân", HttpStatus.BAD_REQUEST);
 
        List<Appointments> list = appointmentService.getByPatientId(patient.getId());
        return ResponseEntity.ok(list);
    }
}
