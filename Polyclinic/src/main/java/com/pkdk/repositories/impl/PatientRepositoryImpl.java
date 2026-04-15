/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repositories.impl;

import com.pkdk.pojo.Patients;
import com.pkdk.repositories.PatientRepository;
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
public class PatientRepositoryImpl implements PatientRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Patients getPatientByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();

        List<Patients> patients = s.createQuery(
                "FROM Patients p WHERE p.userId.id = :uid", Patients.class)
                .setParameter("uid", userId)
                .getResultList();

        return patients.isEmpty() ? null : patients.get(0);
    }

    @Override
    public void addOrUpdate(Patients p) {
        Session s = this.factory.getObject().getCurrentSession();

        if (p.getId() == null) {
            s.persist(p);
        } else {
            s.merge(p);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();

        s.createQuery("DELETE FROM Patients p WHERE p.userId.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }
}
