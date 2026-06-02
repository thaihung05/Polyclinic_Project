/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.MedicalRecords;
import com.pkdk.repository.MedicalRecordRepository;
import java.util.List;
import org.hibernate.Session;
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
public class MedicalRecordRepositoryImpl implements MedicalRecordRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public MedicalRecords getByAppointmentId(int appointmentId) {
        Session s = this.factory.getObject().getCurrentSession();
        List<MedicalRecords> list = s.createQuery("FROM MedicalRecords m WHERE m.appointmentId.id = :appointmentId",
                MedicalRecords.class).setParameter("appointmentId", appointmentId).getResultList();
        return list.isEmpty()?null:list.get(0);
    }

    @Override
    public MedicalRecords getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(MedicalRecords.class, id);
    }

    @Override
    public void save(MedicalRecords record) {
        Session s = this.factory.getObject().getCurrentSession();
        if (record.getId()==null)
            s.persist(record);
        else
            s.merge(record);
    }

    @Override
    public List<MedicalRecords> getByPatientId(int patientId) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createQuery(
                "FROM MedicalRecords m WHERE m.appointmentId.patientId.id = :id ORDER BY m.appointmentId.scheduledAt DESC",
                MedicalRecords.class)
                .setParameter("id", patientId)
                .getResultList();
    }
}
