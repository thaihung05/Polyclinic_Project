/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Users;
import com.pkdk.service.DoctorService;
import com.pkdk.service.SpecialtyService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ApiDoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/doctors")
    public ResponseEntity<?> getDoctors(
            @RequestParam(name = "specialtyId", required = false) Integer specialtyId) {

        if (specialtyId == null) {
            return new ResponseEntity<>(this.doctorService.getAllDoctors(), HttpStatus.OK);
        }

        if (this.specialtyService.getSpecialtyById(specialtyId) == null) {
            return new ResponseEntity<>("Chuyên khoa không tồn tại", HttpStatus.NOT_FOUND);
        }

        List<Doctors> doctors = this.doctorService.getDoctorsBySpecialtyId(specialtyId);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/api/doctors/{doctorId}")
    public ResponseEntity<?> getDoctorDetail(@PathVariable("doctorId") int doctorId) {
        Doctors d = this.doctorService.getDoctorById(doctorId);

        if (d == null) {
            return new ResponseEntity<>("Không tìm thấy bác sĩ", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @GetMapping("/api/secure/doctors/me")
    public ResponseEntity<?> getMyDoctorId(Principal principal) {
        Users u = this.userService.getUserByUserName(principal.getName());
        Doctors d = this.doctorService.getDoctorByUserId(u.getId());
        if (d == null) {
            return new ResponseEntity<>("Không tìm thấy bác sĩ", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(Collections.singletonMap("doctorId", d.getId()), HttpStatus.OK);
    }
}
