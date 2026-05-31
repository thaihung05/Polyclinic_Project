/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.repository.StatsRepository;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public class StatsRepositoryImpl implements StatsRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    private Date parseDateEndOfDay(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 23:59:59");
        } catch (ParseException ex) {
            return null;
        }
    }

    private boolean hasDate(String date) {
        return date != null && !date.isEmpty();
    }
    
    private String getAgeGroup(Date date){
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTime(date);
        int age = currentYear - cal.get(Calendar.YEAR);
        if (age < 18) return "Dưới 18";
        if (age <= 30) return "18 - 30";
        if (age <= 45) return "31 - 45";
        if (age <= 60) return "46 - 60";
        return "Trên 60";
    }

    @Override
    public List<Object[]> patientStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT a.patientId.dateOfBirth, a.patientId.gender, "
                + "a.doctorId.specialtyId.name, COUNT(a.id) "
                + "FROM Appointments a WHERE a.patientId.dateOfBirth IS NOT NULL ";

        if (hasDate(fromDate)) hql += "AND a.scheduledAt >= :fromDate ";
        if (hasDate(toDate)) hql += "AND a.scheduledAt <= :toDate ";
        hql += "GROUP BY a.patientId.dateOfBirth, a.patientId.gender, a.doctorId.specialtyId.name";
        
        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate)) q.setParameter("toDate", parseDateEndOfDay(toDate));
        
        Map<String, Object[]> map = new LinkedHashMap<>();
        for (Object[] row : q.getResultList()) {
            String ageGroup = getAgeGroup((Date) row[0]);
            String key = ageGroup + "|" + row[1] + "|" + row[2];
            if (!map.containsKey(key)) {
                map.put(key, new Object[]{ageGroup, row[1], row[2], 0L});
            }
            map.get(key)[3] = (Long) map.get(key)[3] + (Long) row[3];
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public List<Object[]> patientByAgeStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT a.patientId.dateOfBirth, COUNT(DISTINCT a.patientId.id) "
                + "FROM Appointments a WHERE a.patientId.dateOfBirth IS NOT NULL ";
        if (hasDate(fromDate)) hql += "AND a.scheduledAt >= :fromDate ";
        if (hasDate(toDate)) hql += "AND a.scheduledAt <= :toDate ";
        hql += "GROUP BY a.patientId.dateOfBirth";
        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate)) q.setParameter("toDate", parseDateEndOfDay(toDate));

        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : q.getResultList()) {
            String group = getAgeGroup((Date) row[0]);
            if (!map.containsKey(group)) {
                map.put(group, 0L);
            }
            map.put(group, map.get(group) + (Long) row[1]);
        }
        List<Object[]> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            result.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return result;
    }

    @Override
    public List<Object[]> patientByGenderStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT a.patientId.gender, COUNT(DISTINCT a.patientId.id) "
                + "FROM Appointments a WHERE a.patientId.gender IS NOT NULL ";
        if (hasDate(fromDate)) hql += "AND a.scheduledAt >= :fromDate ";
        if (hasDate(toDate))   hql += "AND a.scheduledAt <= :toDate ";
        hql += "GROUP BY a.patientId.gender ORDER BY COUNT(DISTINCT a.patientId.id) DESC";
        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate))   q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getResultList();
    }

    @Override
    public List<Object[]> patientBySpecialtyStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT a.doctorId.specialtyId.name, COUNT(DISTINCT a.patientId.id) "
                + "FROM Appointments a WHERE 1=1 ";
        if (hasDate(fromDate)) hql += "AND a.scheduledAt >= :fromDate ";
        if (hasDate(toDate))   hql += "AND a.scheduledAt <= :toDate ";
        hql += "GROUP BY a.doctorId.specialtyId.name ORDER BY COUNT(DISTINCT a.patientId.id) DESC";
        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate))   q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getResultList();
    }

    @Override
    public List<Object[]> serviceUsageStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT a.doctorId.specialtyId.name, COUNT(a.id) "
                + "FROM Appointments a WHERE 1=1 ";

        if (hasDate(fromDate)) hql += "AND a.scheduledAt >= :fromDate ";
        if (hasDate(toDate)) hql += "AND a.scheduledAt <= :toDate ";

        hql += "GROUP BY a.doctorId.specialtyId.name ORDER BY COUNT(a.id) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate)) q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getResultList();
    }

    @Override
    public List<Object[]> commonDiseaseStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT m.diagnosis, COUNT(m.id) "
                + "FROM MedicalRecords m "
                + "WHERE m.diagnosis IS NOT NULL AND m.diagnosis <> '' ";

        if (hasDate(fromDate)) hql += "AND m.appointmentId.scheduledAt >= :fromDate ";
        if (hasDate(toDate)) hql += "AND m.appointmentId.scheduledAt <= :toDate ";

        hql += "GROUP BY m.diagnosis ORDER BY COUNT(m.id) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate)) q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getResultList();
    }

    @Override
    public List<Object[]> revenueSummaryStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT p.method, COUNT(p.id), SUM(p.amount) "
                + "FROM Payments p "
                + "WHERE p.status = 'COMPLETED' ";

        if (hasDate(fromDate)) hql += "AND p.ngayTao >= :fromDate ";
        if (hasDate(toDate)) hql += "AND p.ngayTao <= :toDate ";

        hql += "GROUP BY p.method ORDER BY SUM(p.amount) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate)) q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getResultList();
    }

    @Override
    public List<Object[]> revenueDetailStats(String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();

         String hql = "SELECT a.paymentId.id, a.paymentId.ngayTao, a.patientId.userId.name, a.doctorId.userId.name, "
                + "a.doctorId.specialtyId.name, a.paymentId.method, a.paymentId.amount "
                + "FROM Appointments a "
                + "WHERE a.paymentId IS NOT NULL AND a.paymentId.status = 'COMPLETED' ";

        if (hasDate(fromDate)) hql += "AND a.paymentId.ngayTao >= :fromDate ";
        if (hasDate(toDate)) hql += "AND a.paymentId.ngayTao <= :toDate ";

        hql += "ORDER BY a.paymentId.ngayTao DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);
        if (hasDate(fromDate)) q.setParameter("fromDate", parseDate(fromDate));
        if (hasDate(toDate)) q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getResultList();
    }

    @Override
    public long countAppointments() {
        Session s = this.factory.getObject().getCurrentSession();
        Query<Long> q = s.createQuery("SELECT COUNT(a.id) FROM Appointments a", Long.class);
        return q.getSingleResult();
    }

    @Override
    public BigDecimal totalRevenue() {
        Session s = this.factory.getObject().getCurrentSession();
        Query<BigDecimal> q = s.createQuery(
                "SELECT SUM(p.amount) FROM Payments p WHERE p.status = 'COMPLETED'",
                BigDecimal.class);
        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
