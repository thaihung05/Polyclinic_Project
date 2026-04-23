/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.MedicalRecords;
import com.pkdk.pojo.PrescriptionItems;
import com.pkdk.pojo.Prescriptions;
import com.pkdk.service.MedicalRecordService;
import com.pkdk.service.PrescriptionService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
public class ApiPrescriptionController {
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @GetMapping("/api/medical-records/{recordId}/prescriptions")
    public ResponseEntity<?> getPrescriptions(@PathVariable("recordId") int recordId){
        MedicalRecords m = this.medicalRecordService.getById(recordId);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy hồ sơ bệnh án",HttpStatus.NOT_FOUND);
        List<Prescriptions> list = this.prescriptionService.getByMedicalRecordId(recordId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping("/api/prescriptions/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id")int id){
        Prescriptions p = this.prescriptionService.getById(id);
        if (p==null)
            return new ResponseEntity<>("Không tìm thấy đơn thuốc",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/medical-records/{recordId}/prescriptions")
    public ResponseEntity<?> createPrescription(@PathVariable("recordId") int recordId,
            @RequestBody Prescriptions prescription ){
        
        MedicalRecords m = this.medicalRecordService.getById(recordId);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy hồ sơ bệnh án",HttpStatus.NOT_FOUND);
        
        prescription.setId(null);
        prescription.setMedicalRecordId(m);
        prescription.setNgayTao(new Date());
        
        if (prescription.getPrescriptionItemsCollection()!=null)
            for (PrescriptionItems item : prescription.getPrescriptionItemsCollection())
                item.setPrescriptionId(prescription);
        
        this.prescriptionService.save(prescription);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
}
