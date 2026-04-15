/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.services.SpecialtyService;
import com.pkdk.services.UserService;
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
    
    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("pageDescription", "Tổng quan hệ thống phòng khám");
        model.addAttribute("activePage", "dashboard");

        model.addAttribute("totalUsers", this.userService.getUsers(null).size());
        model.addAttribute("totalAppointments", 0);
        model.addAttribute("totalSpecialties", this.specialtyService.getSpecs().size());
        model.addAttribute("totalRevenue", 0);
        
        return "admin";
    }
}
