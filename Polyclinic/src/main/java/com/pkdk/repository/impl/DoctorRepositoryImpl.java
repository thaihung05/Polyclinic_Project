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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class DoctorRepositoryImpl implements DoctorRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Autowired
    private Environment env;

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

    @Override
    public List<Doctors> getAll(String kw, int page) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "FROM Doctors d WHERE 1=1 ";
        if (kw != null && !kw.trim().isEmpty())
            hql += "AND (LOWER(d.userId.name) LIKE :kw OR LOWER(d.specialtyId.name) LIKE :kw) ";
        hql += "ORDER BY d.userId.name ASC";
        
        Query q = s.createQuery(hql, Doctors.class);
        if (kw != null && !kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");
        
        int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
        q.setMaxResults(pageSize);
        q.setFirstResult((page-1) * pageSize);
        
        return q.getResultList();
    }

    @Override
    public List<Doctors> getAllDoctors() {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createQuery("FROM Doctors d ORDER BY d.rating DESC", Doctors.class).getResultList();
    }

    @Override
    public long countAll(String kw) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(d) FROM Doctors d WHERE 1=1 ";
        if (kw != null && !kw.trim().isEmpty())
            hql += "AND (LOWER(d.userId.name) LIKE :kw OR LOWER(d.specialtyId.name) LIKE :kw) ";

        Query<Long> q = s.createQuery(hql, Long.class);
        if (kw != null && !kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");
        return q.getSingleResult();
    }

}
