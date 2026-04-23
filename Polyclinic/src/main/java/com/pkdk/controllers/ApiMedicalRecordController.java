/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.enums.UserRole;
import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.MedicalRecords;
import com.pkdk.pojo.Users;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.MedicalRecordService;
import com.pkdk.service.UserService;
import java.security.Principal;
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
public class ApiMedicalRecordController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/api/appointments/{appointmentId}/medical-record")
    public ResponseEntity<?> getByAppointment(@PathVariable("appointmentId") int appointmentId, Principal principal){
        Appointments a = this.appointmentService.getById(appointmentId);
        if (a==null)
            return new ResponseEntity<>("Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND);
        
        MedicalRecords m = this.medicalRecordService.getByAppointmentId(appointmentId);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy hồ sơ bệnh án", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/appointments/{appointmentId}/medical-record")
    public ResponseEntity<?> createRecord(@PathVariable("appointmentId") int appointmentId
            ,@RequestBody MedicalRecords record, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền tạo hồ sơ bệnh án",HttpStatus.FORBIDDEN);
        
        Appointments a = this.appointmentService.getById(appointmentId);
        if (a==null)
            return new ResponseEntity<>("Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND);
        
        MedicalRecords m = this.medicalRecordService.getByAppointmentId(appointmentId);
        if (m!=null)
            return new ResponseEntity<>("Lịch hẹn này đã tồn tại hồ sơ bệnh án", HttpStatus.BAD_REQUEST);
        record.setId(null);
        record.setAppointmentId(a);
        this.medicalRecordService.save(record);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }
    
    @PutMapping("/api/secure/medical-records/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable("id")int id,
            @RequestBody MedicalRecords record, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền sửa hồ sơ bệnh án",HttpStatus.FORBIDDEN);
        
        MedicalRecords m = this.medicalRecordService.getById(id);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy hồ sơ bệnh án",HttpStatus.BAD_REQUEST);
        
        m.setChiefComplaint(record.getChiefComplaint());
        m.setDiagnosis(record.getDiagnosis());
        m.setNotes(record.getNotes());
        m.setFollowUpDate(record.getFollowUpDate());
        m.setTreatmentPlan(record.getTreatmentPlan());
        
        this.medicalRecordService.save(m);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }
}
