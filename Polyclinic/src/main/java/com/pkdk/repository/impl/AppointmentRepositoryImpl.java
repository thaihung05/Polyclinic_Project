/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Appointments;
import com.pkdk.repository.AppointmentRepository;
import java.util.Date;
import java.util.List;
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
public class AppointmentRepositoryImpl implements AppointmentRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

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
        if (appointment.getId()==null)
            s.persist(appointment);
        else
            s.merge(appointment);
    }
  
//    @Override
//    public void delete(int id) {
//        Session s = this.factory.getObject().getCurrentSession();
//        Appointments appointment = s.get(Appointments.class, id);
//        if (appointment != null) {
//            s.remove(appointment);
//        }
//    }

    @Override
    public boolean existsByPatientAndTime(int patientId, Date scheduledAt) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery(
            "SELECT COUNT(a) FROM Appointments a " +
            "WHERE a.patientId.id = :patientId " +
            "AND a.scheduledAt = :scheduledAt " +
            "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')",  // ← lịch đã hủy thì không tính
            Long.class);
        q.setParameter("patientId", patientId);
        q.setParameter("scheduledAt", scheduledAt);
        boolean check = ((Long) q.getSingleResult()) > 0;
        return check;
    }
    
}