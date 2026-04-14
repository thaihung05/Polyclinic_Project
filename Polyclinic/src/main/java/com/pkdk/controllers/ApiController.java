/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Specialties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import services.SpecialtiesService;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiController {
    
    @Autowired
    private SpecialtiesService specialtiesService;
    
    @GetMapping("/specialties")
    @ResponseBody
    public List<Map<String, Object>> getSpecialties() {
        List<Specialties> specialties = this.specialtiesService.getSpecs();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Specialties s : specialties) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", s.getId());
            item.put("name", s.getName());
            item.put("description", s.getDescription());
            result.add(item);
        }

        return result;
    }
}
