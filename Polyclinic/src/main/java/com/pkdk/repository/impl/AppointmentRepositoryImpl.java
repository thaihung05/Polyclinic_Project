/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Appointments;
import com.pkdk.repository.AppointmentRepository;
import jakarta.persistence.NoResultException;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vanlong
 */
@Repository
@Transactional
public class AppointmentRepositoryImpl implements AppointmentRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

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
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Appointments appointment = s.get(Appointments.class, id);
        if (appointment != null) {
            s.remove(appointment);
        }
    }

    @Override
    public List<Appointments> getByPatientId(int patientId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Appointments a WHERE a.patientId.id = :pid ORDER BY a.scheduledAt DESC",Appointments.class);
        q.setParameter("pid", patientId);
        try{
            return q.getResultList();
        }
        catch(NoResultException ex){
            return null;
        }
    }

    @Override
    public Appointments getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Appointments.class, id);
        
    }

}
