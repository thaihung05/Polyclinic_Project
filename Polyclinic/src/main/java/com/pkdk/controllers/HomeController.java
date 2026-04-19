/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Admin
 */
@Controller
@ControllerAdvice
public class HomeController {

    @Autowired
    private SpecialtyService specialtyService;

    @ModelAttribute
    public void commonAttr(Model model) {
        model.addAttribute("specialties", this.specialtyService.getSpecs());
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
