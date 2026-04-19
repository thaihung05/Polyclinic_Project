/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.service.StatsService;
import java.util.Map;
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
    public String statsView(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("activePage", "stats");

        model.addAttribute("fromDate", params.getOrDefault("fromDate", ""));
        model.addAttribute("toDate", params.getOrDefault("toDate", ""));

        model.addAttribute("patientStats", this.statsService.patientStats(params));
        model.addAttribute("serviceUsageStats", this.statsService.serviceUsageStats(params));
        model.addAttribute("commonDiseaseStats", this.statsService.commonDiseaseStats(params));
        model.addAttribute("revenueSummaryStats", this.statsService.revenueSummaryStats(params));
        model.addAttribute("revenueDetailStats", this.statsService.revenueDetailStats(params));

        return "admin-stats";
    }
}
