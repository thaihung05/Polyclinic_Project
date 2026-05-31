/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Appointments;
import com.pkdk.repository.AppointmentRepository;

import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class AppointmentRepositoryImpl implements AppointmentRepository{
   
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Autowired
    private Environment env;

    @Override
    public List<Appointments> getByDoctorId(int doctorId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Appointments a Where a.doctorId.id = :doctorId", Appointments.class)
                .setParameter("doctorId", doctorId);
        return q.getResultList();
    }

    @Override
    public List<Appointments> getByPatientId(int patientId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("From Appointments a Where a.patientId.id = :patientId", Appointments.class)
                .setParameter("patientId", patientId);
        return q.getResultList();
    }

    @Override
    public Appointments getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Appointments.class, id);
    }

    @Override
    public void save(Appointments appointment) {
        Session s = this.factory.getObject().getCurrentSession();
        if (appointment.getId() == null) {
            s.persist(appointment);
        } else {
            s.merge(appointment);
        }
    }

    @Override
    public boolean existsByPatientAndTime(int patientId, Date scheduledAt) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery(
                "SELECT COUNT(a) FROM Appointments a "
                + "WHERE a.patientId.id = :patientId "
                + "AND a.scheduledAt = :scheduledAt "
                + "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')", // ← lịch đã hủy thì không tính
                Long.class);
    @Override
    public boolean existsByPatientAndTime(int patientId, Date scheduledAt) {
        Session s = this.factory.getObject().getCurrentSession();
        Query<Long> q = s.createQuery(
            "SELECT COUNT(a) FROM Appointments a " +
            "WHERE a.patientId.id = :patientId " +
            "AND a.scheduledAt = :scheduledAt " +
            "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')",
            Long.class);
        q.setParameter("patientId", patientId);
        q.setParameter("scheduledAt", scheduledAt);
        return q.getSingleResult() > 0;
    }
    
    private Date parseDate(String date){
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException ex){
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

    @Override
    public List<Appointments> getAll(String kw, String status, String fromDate, String toDate, int page) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "FROM Appointments a WHERE 1=1 ";
        if (kw!=null && !kw.trim().isEmpty())
            hql += "AND (LOWER(a.patientId.userId.name) LIKE :kw OR LOWER(a.doctorId.userId.name) LIKE :kw) ";
        if (status!=null && !status.trim().isEmpty())
            hql += "AND a.status = :status ";
        if (fromDate != null && !fromDate.trim().isEmpty())
            hql += "AND a.scheduledAt >= :fromDate ";
        if (toDate != null && !toDate.trim().isEmpty())
            hql += "AND a.scheduledAt <= :toDate ";
        hql += "ORDER BY a.scheduledAt DESC";
        
        Query q = s.createQuery(hql, Appointments.class);
        if (kw!=null && !kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");
        if (status!=null && !status.trim().isEmpty())
            q.setParameter("status", status);
        if (fromDate != null && !fromDate.trim().isEmpty())
            q.setParameter("fromDate", parseDate(fromDate));
        if (toDate != null && !toDate.trim().isEmpty())
            q.setParameter("toDate", parseDateEndOfDay(toDate));
        
        int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
        q.setMaxResults(pageSize);
        q.setFirstResult((page-1)*pageSize);
        return q.getResultList();
    }

    @Override
    public long countAll(String kw, String status, String fromDate, String toDate) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(a) FROM Appointments a WHERE 1=1 ";
        if (kw!=null && !kw.trim().isEmpty())
            hql += "AND (LOWER(a.patientId.userId.name) LIKE :kw OR LOWER(a.doctorId.userId.name) LIKE :kw) ";
        if (status!=null && !status.trim().isEmpty())
            hql += "AND a.status = :status ";
        if (fromDate != null && !fromDate.trim().isEmpty())
            hql += "AND a.scheduledAt >= :fromDate ";
        if (toDate != null && !toDate.trim().isEmpty())
            hql += "AND a.scheduledAt <= :toDate ";

        Query<Long> q = s.createQuery(hql, Long.class);
        if (kw!=null && !kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");
        if (status!=null && !status.trim().isEmpty())
            q.setParameter("status", status);
        if (fromDate != null && !fromDate.trim().isEmpty())
            q.setParameter("fromDate", parseDate(fromDate));
        if (toDate != null && !toDate.trim().isEmpty())
            q.setParameter("toDate", parseDateEndOfDay(toDate));
        return q.getSingleResult();
    }

    @Override
    public boolean existsByPatientDoctorAndDate(int patientId, int doctorId, Date date) {
        Session s = this.factory.getObject().getCurrentSession();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endOfDay = cal.getTime();

        Query q = s.createQuery("SELECT COUNT(a) FROM Appointments a WHERE a.patientId.id = :patientId "
                + "AND a.doctorId.id = :doctorId "
                + "AND a.scheduledAt >= :startOfDay "
                + "AND a.scheduledAt <= :endOfDay "
                + "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')", Long.class);

        q.setParameter("patientId", patientId);
        q.setParameter("doctorId", doctorId);
        q.setParameter("startOfDay", startOfDay);
        q.setParameter("endOfDay", endOfDay);

        return ((Long) q.getSingleResult() > 0);

    }

}
