/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.LabResults;
import com.pkdk.pojo.MedicalRecords;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Users;
import com.pkdk.service.MedicalHistoryService;
import com.pkdk.service.PatientService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vanlong
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiMedicalHistoryController {

    @Autowired
    private UserService userService;
    @Autowired
    private MedicalHistoryService medicalHistoryService;
    @Autowired
    private PatientService patientService;
    
    @GetMapping("/secure/medical-records")
    public ResponseEntity<?> getMedicalRecords(Principal principal){
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null){
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        }
        Patients patient = this.patientService.getPatientByUserId(u.getId());
        if (patient==null){
            return new ResponseEntity<>("Không tìm thấy bệnh nhân", HttpStatus.UNAUTHORIZED);
        }
        
        List<MedicalRecords> list = this.medicalHistoryService.getMedicalRecordsByPatientId(patient.getId());
        
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping("/secure/lab-results")
    public ResponseEntity<?> getLabResults(Principal principal){
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null){
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        }
        Patients patient = this.patientService.getPatientByUserId(u.getId());
        if (patient==null){
            return new ResponseEntity<>("Không tìm thấy bệnh nhân", HttpStatus.UNAUTHORIZED);
        }
        
        List<LabResults> list=this.medicalHistoryService.getLabResultsByPatientId(patient.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
        
    }
}
