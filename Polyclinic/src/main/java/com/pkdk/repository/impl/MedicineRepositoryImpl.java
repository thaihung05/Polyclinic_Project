/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Medicines;
import com.pkdk.repository.MedicineRepository;
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
public class MedicineRepositoryImpl implements MedicineRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Medicines> getAll() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Medicines m WHERE m.isActive == True",Medicines.class);
        return q.getResultList();
    }

    @Override
    public Medicines getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Medicines.class, id);
    }
    
}
