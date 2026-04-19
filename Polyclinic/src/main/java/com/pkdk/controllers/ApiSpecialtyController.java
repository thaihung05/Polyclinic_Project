/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Specialties;
import com.pkdk.service.SpecialtyService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@CrossOrigin
@RestController
public class ApiSpecialtyController {

     @Autowired
    private SpecialtyService specialtyService;

    @GetMapping("/api/specialties")
    public ResponseEntity<List<Specialties>> getSpecialties() {
        return new ResponseEntity<>(this.specialtyService.getSpecs(), HttpStatus.OK);
    }
}
