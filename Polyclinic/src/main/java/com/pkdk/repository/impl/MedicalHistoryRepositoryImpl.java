/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.LabResults;
import com.pkdk.pojo.MedicalRecords;
import com.pkdk.repository.MedicalHistoryRepository;
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
public class MedicalHistoryRepositoryImpl implements MedicalHistoryRepository {
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<MedicalRecords> getMedicalRecordsByPatientId(int patientId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM MedicalRecords m WHERE m.appointmentId.patientId.id = :pid ORDER BY m.appointmentId.scheduledAt DESC", MedicalRecords.class);
        q.setParameter("pid", patientId);
        try{
            return q.getResultList();
        }
        catch(NoResultException ex){
            return null;
        }
    }
    
    @Override
    public List<LabResults> getLabResultsByPatientId(int patientId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM LabResults l WHERE l.appointmentId.patientId.id = :pid ORDER BY l.ngayTao DESC", LabResults.class);
        q.setParameter("pid", patientId);
        try{
            return q.getResultList();
        }
        catch(NoResultException ex){
            return null;
        }
    }
    
}
