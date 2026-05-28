/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Prescriptions;
import com.pkdk.repository.PrescriptionRepository;
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
public class PrescriptionRepositoryImpl implements PrescriptionRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Prescriptions> getByMedicalRecordId(int medicalRecordId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Prescriptions p Where p.medicalRecordId.id = :medicalRecordId",Prescriptions.class)
                .setParameter("medicalRecordId", medicalRecordId);
        return q.getResultList();
    }

    @Override
    public Prescriptions getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Prescriptions.class, id);
    }

    @Override
    public void save(Prescriptions prescriptions) {
        Session s = this.factory.getObject().getCurrentSession();
        if (prescriptions.getId()==null)
            s.persist(prescriptions);
        else
            s.merge(prescriptions);
    }  

    @Override
    public List<Prescriptions> getByPatientId(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Prescriptions p WHERE p.medicalRecordId.appointmentId.patientId.id = :patientId", Prescriptions.class)
                .setParameter("patientId", id);
        return q.getResultList();
    }
}
