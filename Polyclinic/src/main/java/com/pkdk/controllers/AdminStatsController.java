/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/admin/stats")
public class AdminStatsController {
    @Autowired
    private StatsService statsService;

    @GetMapping
    public String statsView(Model model, 
            @RequestParam( value = "fromDate", defaultValue = "") String fromDate,
            @RequestParam( value = "toDate", defaultValue = "") String toDate) {
        
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        
        model.addAttribute("patientStats", this.statsService.patientStats(fromDate, toDate));
        model.addAttribute("patientByAgeStats", this.statsService.patientByAgeStats(fromDate, toDate));
        model.addAttribute("patientByGenderStats", this.statsService.patientByGenderStats(fromDate, toDate));
        model.addAttribute("patientBySpecialtyStats", this.statsService.patientBySpecialtyStats(fromDate, toDate));
        model.addAttribute("revenueDetailStats", this.statsService.revenueDetailStats(fromDate, toDate));
        model.addAttribute("serviceUsageStats", this.statsService.serviceUsageStats(fromDate, toDate));
        model.addAttribute("revenueSummaryStats", this.statsService.revenueSummaryStats(fromDate, toDate));
        model.addAttribute("commonDiseaseStats", this.statsService.commonDiseaseStats(fromDate, toDate));
        
        model.addAttribute("activePage", "stats");
        return "admin-stats";
    }
}
