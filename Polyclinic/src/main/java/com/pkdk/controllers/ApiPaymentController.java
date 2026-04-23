/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Payments;
import com.pkdk.pojo.Users;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.PatientService;
import com.pkdk.service.PaymentService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
public class ApiPaymentController {
    
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PatientService patientService;
    
    @PostMapping("/secure/payment/create")
    public ResponseEntity<?> createPayment(Principal principal, @RequestBody Map<String, Object> body){
        if(!checkIsOwner(principal, body.get("appointmentId"))){
            return new ResponseEntity<>("Không có quyền thanh toán lịch hẹn này", HttpStatus.FORBIDDEN);
        }
        
        int appointmentId = (Integer)body.get("appointmentId");
        String method = "MOMO";
        try{
            Payments payment = this.paymentService.createPending(appointmentId, method);
            return new ResponseEntity<>(payment,HttpStatus.OK);
        }
        catch(RuntimeException ex){
            return new ResponseEntity<>("Lỗi: "+ ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
        
    }
    
    
    @PostMapping("/secure/payment/confirm")
    public ResponseEntity<?> confirm(Principal principal, @RequestBody Map<String, Object> body){
        if(!checkIsOwner(principal, body.get("appointmentId"))){
            return new ResponseEntity<>("Không có quyền xác nhận lịch hẹn này", HttpStatus.FORBIDDEN);
        }
        int appointmentId = (Integer)body.get("appointmentId");
        
        try{
            Payments payment = this.paymentService.confirm(appointmentId);
            return new ResponseEntity<>(payment,HttpStatus.OK);
        }
        catch(RuntimeException ex){
            return new ResponseEntity<>("Lỗi: "+ ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }
    
    private boolean checkIsOwner(Principal principal, Object appointmentIdObj){
        if (appointmentIdObj == null) return false;
        int appointmentId= (Integer)appointmentIdObj;
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null) return false;
        
        Patients p = this.patientService.getPatientByUserId(u.getId());
        if (p==null) return false;
        
        Appointments a = this.appointmentService.getById(appointmentId);
        if(a==null) return false;
        
        return a.getPatientId().getId().equals(p.getId());
    }
}
