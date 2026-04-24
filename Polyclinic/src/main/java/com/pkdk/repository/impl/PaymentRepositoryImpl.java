/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Payments;
import com.pkdk.repository.PaymentRepository;
import org.hibernate.Session;
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
public class PaymentRepositoryImpl implements PaymentRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public void save(Payments payment) {
        Session s = this.factory.getObject().getCurrentSession();
         if (payment.getId() == null)
            s.persist(payment);
        else
            s.merge(payment);
    }

    @Override
    public Payments getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Payments.class, id);
    }
    
}
