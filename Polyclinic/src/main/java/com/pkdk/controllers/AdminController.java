/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.service.SpecialtyService;
import com.pkdk.service.StatsService;
import com.pkdk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SpecialtyService specialtyService;
    
    @Autowired
    private StatsService statsService;
    
    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("activePage", "dashboard");

        model.addAttribute("totalUsers", this.userService.countUsers(""));
        model.addAttribute("totalAppointments", this.statsService.countAppointments());
        model.addAttribute("totalSpecialties", this.specialtyService.getSpecs().size());
        model.addAttribute("totalRevenue", this.statsService.totalRevenue());
        
        return "admin";
    }
}
