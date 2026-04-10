/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import services.SpecialtiesService;

/**
 *
 * @author Admin
 */
@Controller
@ControllerAdvice
public class HomeController {
    
    @Autowired
    private SpecialtiesService SpecService;
    
    @ModelAttribute
    public void commonResponses(Model model) {
        model.addAttribute("specialties", this.SpecService.getSpecs());
    }
    
    @RequestMapping("/")
    public String index(){
        return "index";
    }
}
