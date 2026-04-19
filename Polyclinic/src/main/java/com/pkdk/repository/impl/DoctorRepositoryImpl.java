/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Doctors;
import com.pkdk.repository.DoctorRepository;
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
 * @author Admin
 */
@Repository
@Transactional
public class DoctorRepositoryImpl implements DoctorRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Doctors getDoctorByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Doctors d WHERE d.userId.id = :uid", Doctors.class);
        q.setParameter("uid", userId);
        try {
            return (Doctors) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void addOrUpdate(Doctors d) {
        Session s = this.factory.getObject().getCurrentSession();
        if (d.getId() == null) {
            s.persist(d);
        } else {
            s.merge(d);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        Doctors d = this.getDoctorByUserId(userId);
        if (d != null) {
            s.remove(d);
        }

    }

    @Override
    public List<Doctors> getDoctorsBySpecialtyId(int specialtyId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery(
                "FROM Doctors d WHERE d.specialtyId.id = :specialtyId",
                Doctors.class
        );
        query.setParameter("specialtyId", specialtyId);
        return query.getResultList();
    }

    @Override
    public Doctors getDoctorById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Doctors.class, id);
    }

}
