/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface StatsRepository {
    List<Object[]> patientStats(String fromDate, String toDate);
    List<Object[]> patientByAgeStats(String fromDate, String toDate);
    List<Object[]> patientByGenderStats(String fromDate, String toDate);
    List<Object[]> patientBySpecialtyStats(String fromDate, String toDate);
    List<Object[]> serviceUsageStats(String fromDate, String toDate);
    List<Object[]> commonDiseaseStats(String fromDate, String toDate);
    List<Object[]> revenueSummaryStats(String fromDate, String toDate);
    List<Object[]> revenueDetailStats(String fromDate, String toDate);

    long countAppointments();
    BigDecimal totalRevenue();
}
