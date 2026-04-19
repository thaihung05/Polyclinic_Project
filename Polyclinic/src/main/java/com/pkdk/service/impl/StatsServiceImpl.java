/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.repository.StatsRepository;
import com.pkdk.service.StatsService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsRepository statsRepository;

    @Override
    public List<Object[]> patientStats(Map<String, String> params) {
        return this.statsRepository.patientStats(params);
    }

    @Override
    public List<Object[]> serviceUsageStats(Map<String, String> params) {
        return this.statsRepository.serviceUsageStats(params);
    }

    @Override
    public List<Object[]> commonDiseaseStats(Map<String, String> params) {
        return this.statsRepository.commonDiseaseStats(params);
    }

    @Override
    public List<Object[]> revenueSummaryStats(Map<String, String> params) {
        return this.statsRepository.revenueSummaryStats(params);
    }

    @Override
    public List<Object[]> revenueDetailStats(Map<String, String> params) {
        return this.statsRepository.revenueDetailStats(params);
    }

    @Override
    public long countAppointments() {
        return this.statsRepository.countAppointments();
    }

    @Override
    public BigDecimal totalRevenue() {
        return this.statsRepository.totalRevenue();
    }

}
