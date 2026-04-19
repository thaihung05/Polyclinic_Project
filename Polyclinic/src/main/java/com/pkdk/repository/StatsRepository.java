/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface StatsRepository {
    List<Object[]> patientStats(Map<String, String> params);
    List<Object[]> serviceUsageStats(Map<String, String> params);
    List<Object[]> commonDiseaseStats(Map<String, String> params);
    List<Object[]> revenueSummaryStats(Map<String, String> params);
    List<Object[]> revenueDetailStats(Map<String, String> params);

    long countAppointments();
    BigDecimal totalRevenue();
}
