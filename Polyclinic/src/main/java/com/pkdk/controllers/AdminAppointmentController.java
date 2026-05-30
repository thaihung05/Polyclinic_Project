/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Appointments;
import com.pkdk.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/admin/appointments")
@PropertySource("classpath:configs.properties")
public class AdminAppointmentController {
    
    @Autowired
    private Environment env;
    
    @Autowired
    private AppointmentService appontmenService;
    
    @GetMapping
    public String list(Model model,
            @RequestParam(value="kw", defaultValue = "") String kw,
            @RequestParam(value="status", defaultValue = "") String status,
            @RequestParam(value="fromDate", defaultValue = "") String fromDate,
            @RequestParam(value="toDate", defaultValue = "") String toDate,
            @RequestParam(value="page", defaultValue = "1") int page){
        
        int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
        long total = this.appontmenService.countAll(kw, status, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) total/pageSize);
        
        model.addAttribute("appointments", this.appontmenService.getAll(kw, status, fromDate, toDate, page));
        model.addAttribute("kw", kw);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "appointments");
        
        return "appointment-list";
    }
    
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") int id,
            @RequestParam("status") String status){
        Appointments a = appontmenService.getById(id);
        if (a!=null){
            a.setStatus(status);
            appontmenService.save(a);
        }
        return "redirect:/admin/appointments";
    }
    
}
