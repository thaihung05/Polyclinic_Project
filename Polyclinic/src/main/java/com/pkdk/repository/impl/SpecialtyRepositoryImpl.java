/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Specialties;
import java.util.List;
import com.pkdk.repository.SpecialtyRepository;
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
public class SpecialtyRepositoryImpl implements SpecialtyRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Specialties> getSpecs() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Specialties", Specialties.class);
        return q.getResultList();
    }

    @Override
    public Specialties getSpecialtyById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Specialties.class, id);
    }

    @Override
    public void addOrUpdate(Specialties s) {
        Session session = this.factory.getObject().getCurrentSession();

        if (s.getId() == null) {
            session.persist(s);
        } else {
            session.merge(s);
        }
    }

    @Override
    public void deleteSpecialty(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        Specialties s = this.getSpecialtyById(id);
        if (s != null) {
            session.remove(s);
        }
    }

}
