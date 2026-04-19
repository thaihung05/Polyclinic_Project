/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;
import com.pkdk.repository.StatsRepository;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Override
    public List<Object[]> patientStats(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT p.gender, d.specialtyId.name, COUNT(a.id) "
                + "FROM Appointments a "
                + "JOIN a.patientId p "
                + "JOIN a.doctorId d "
                + "WHERE 1=1 ";

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                hql += "AND a.scheduledAt >= :fromDate ";
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                hql += "AND a.scheduledAt <= :toDate ";
            }
        }

        hql += "GROUP BY p.gender, d.specialtyId.name "
             + "ORDER BY COUNT(a.id) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                q.setParameter("fromDate", parseDate(params.get("fromDate")));
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                q.setParameter("toDate", parseDate(params.get("toDate")));
            }
        }

        return q.getResultList();
    }

    @Override
    public List<Object[]> serviceUsageStats(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT d.specialtyId.name, COUNT(a.id) "
                + "FROM Appointments a "
                + "JOIN a.doctorId d "
                + "WHERE 1=1 ";

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                hql += "AND a.scheduledAt >= :fromDate ";
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                hql += "AND a.scheduledAt <= :toDate ";
            }
        }

        hql += "GROUP BY d.specialtyId.name "
             + "ORDER BY COUNT(a.id) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                q.setParameter("fromDate", parseDate(params.get("fromDate")));
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                q.setParameter("toDate", parseDate(params.get("toDate")));
            }
        }

        return q.getResultList();
    }

    @Override
    public List<Object[]> commonDiseaseStats(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT m.diagnosis, COUNT(m.id) "
                + "FROM MedicalRecords m "
                + "JOIN m.appointmentId a "
                + "WHERE m.diagnosis IS NOT NULL "
                + "AND m.diagnosis <> '' ";

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                hql += "AND a.scheduledAt >= :fromDate ";
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                hql += "AND a.scheduledAt <= :toDate ";
            }
        }

        hql += "GROUP BY m.diagnosis "
             + "ORDER BY COUNT(m.id) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                q.setParameter("fromDate", parseDate(params.get("fromDate")));
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                q.setParameter("toDate", parseDate(params.get("toDate")));
            }
        }

        return q.getResultList();
    }

    @Override
    public List<Object[]> revenueSummaryStats(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT p.method, COUNT(p.id), SUM(p.amount) "
                + "FROM Payments p "
                + "WHERE p.status = 'COMPLETED' ";

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                hql += "AND p.ngayTao >= :fromDate ";
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                hql += "AND p.ngayTao <= :toDate ";
            }
        }

        hql += "GROUP BY p.method "
             + "ORDER BY SUM(p.amount) DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                q.setParameter("fromDate", parseDate(params.get("fromDate")));
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                q.setParameter("toDate", parseDate(params.get("toDate")));
            }
        }

        return q.getResultList();
    }

    @Override
    public List<Object[]> revenueDetailStats(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        String hql = "SELECT p.id, p.ngayTao, pa.userId.name, d.userId.name, "
                + "d.specialtyId.name, p.method, p.amount "
                + "FROM Appointments a "
                + "JOIN a.paymentId p "
                + "JOIN a.patientId pa "
                + "JOIN a.doctorId d "
                + "WHERE p.status = 'COMPLETED' ";

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                hql += "AND p.ngayTao >= :fromDate ";
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                hql += "AND p.ngayTao <= :toDate ";
            }
        }

        hql += "ORDER BY p.ngayTao DESC";

        Query<Object[]> q = s.createQuery(hql, Object[].class);

        if (params != null) {
            if (params.get("fromDate") != null && !params.get("fromDate").isEmpty()) {
                q.setParameter("fromDate", parseDate(params.get("fromDate")));
            }
            if (params.get("toDate") != null && !params.get("toDate").isEmpty()) {
                q.setParameter("toDate", parseDate(params.get("toDate")));
            }
        }

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
                BigDecimal.class
        );

        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
