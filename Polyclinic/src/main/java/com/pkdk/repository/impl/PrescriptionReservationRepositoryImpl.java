/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.PrescriptionReservations;
import com.pkdk.repository.PrescriptionReservationRepository;
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
@Transactional
@Repository
public class PrescriptionReservationRepositoryImpl implements PrescriptionReservationRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public void save(PrescriptionReservations r) {
        Session s = this.factory.getObject().getCurrentSession();
        if (r.getId() == null){
            s.persist(r);
        } else {
            s.merge(r);
        }
    }

    @Override
    public PrescriptionReservations getByPrescriptionId(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM PrescriptionReservations r WHERE r.prescriptionId.id = :id",PrescriptionReservations.class)
                .setParameter("id", id);
        List<PrescriptionReservations> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<PrescriptionReservations> findExpired() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM PrescriptionReservations r WHERE r.expiresAt < :now AND r.isPaid = false And r.isExpired = false",PrescriptionReservations.class)
                .setParameter("now", new Date());
        return q.getResultList();
        
    }

    @Override
    public List<PrescriptionReservations> findPaidAndNotDispensed() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM PrescriptionReservations r WHERE r.isPaid = true AND r.isDispensed = false AND r.isExpired = false",PrescriptionReservations.class);
        return q.getResultList();
    }
    
}
