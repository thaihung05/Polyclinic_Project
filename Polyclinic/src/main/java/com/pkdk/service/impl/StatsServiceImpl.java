/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.repository.StatsRepository;
import com.pkdk.service.StatsService;
import java.math.BigDecimal;
import java.util.List;
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
    public List<Object[]> patientStats(String fromDate, String toDate) {
        return this.statsRepository.patientStats(fromDate, toDate);
    }

    @Override
    public List<Object[]> serviceUsageStats(String fromDate, String toDate) {
        return this.statsRepository.serviceUsageStats(fromDate, toDate);
    }

    @Override
    public List<Object[]> commonDiseaseStats(String fromDate, String toDate) {
        return this.statsRepository.commonDiseaseStats(fromDate, toDate);
    }

    @Override
    public List<Object[]> revenueSummaryStats(String fromDate, String toDate) {
        return this.statsRepository.revenueSummaryStats(fromDate, toDate);
    }

    @Override
    public List<Object[]> revenueDetailStats(String fromDate, String toDate) {
        return this.statsRepository.revenueDetailStats(fromDate, toDate);
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
