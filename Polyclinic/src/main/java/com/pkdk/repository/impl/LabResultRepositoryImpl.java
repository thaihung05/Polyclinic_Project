/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.LabResults;
import com.pkdk.repository.LabResultRepository;
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
public class LabResultRepositoryImpl implements LabResultRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<LabResults> getByAppointmentId(int appointmentId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM LabResults r WHERE r.appointmentId.id = :appointmentId",LabResults.class)
                .setParameter("appointmentId", appointmentId);
        return q.getResultList();
    }

    @Override
    public LabResults getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(LabResults.class, id);
    }

    @Override
    public void save(LabResults labResult) {
        Session s = this.factory.getObject().getCurrentSession();
        if (labResult.getId() == null)
            s.persist(labResult);
        else
            s.merge(labResult);
    }
    
}
