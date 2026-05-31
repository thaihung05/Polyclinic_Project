/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Medicines;
import com.pkdk.repository.MedicineRepository;
import java.util.Calendar;
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
@Repository
@Transactional
public class MedicineRepositoryImpl implements MedicineRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Medicines> getAll() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Medicines m ORDER BY m.isActive DESC, m.name ASC", Medicines.class);
        return q.getResultList();
    }

    @Override
    public List<Medicines> getAll(String kw, String status) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "FROM Medicines m WHERE 1=1";
        if (kw != null && !kw.trim().isEmpty())
            hql += " AND (LOWER(m.name) LIKE :kw OR LOWER(m.code) LIKE :kw)";
        if ("active".equals(status))
            hql += " AND m.isActive = true";
        else if ("inactive".equals(status))
            hql += " AND m.isActive = false";
        hql += " ORDER BY m.isActive DESC, m.name ASC";

        Query q = s.createQuery(hql, Medicines.class);
        if (kw != null && !kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    public Medicines getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Medicines.class, id);
    }

    @Override
    public void save(Medicines medicine) {
        Session s = this.factory.getObject().getCurrentSession();
        if (medicine.getId()==null)
            s.persist(medicine);
        else
            s.merge(medicine);
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Medicines m = s.get(Medicines.class, id);
        if (m!=null){
            m.setIsActive(false);
            s.merge(m);
        }
    }

    @Override
    public List<Medicines> getLowStock(int threshold) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Medicines m WHERE m.isActive=true AND m.stockQuantity <= :threshold", Medicines.class)
                .setParameter("threshold", threshold);
        return q.getResultList();
    }

    @Override
    public List<Medicines> getNearExiry(int days) {
        Session s = this.factory.getObject().getCurrentSession();
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date dl = cal.getTime();
        Query q = s.createQuery("FROM Medicines m WHERE m.isActive=true AND m.expiryDate is not null AND m.expiryDate BETWEEN :now and :dl",Medicines.class)
                .setParameter("now", now)
                .setParameter("dl", dl);
        return q.getResultList();
    }

    @Override
    public void deductStock(int medicineId, int quantity) {
        Session s = this.factory.getObject().getCurrentSession();
        Medicines m = s.get(Medicines.class, medicineId);
        if (m != null) {
            m.setStockQuantity(m.getStockQuantity() - quantity);
            s.merge(m);
        }
    }

    @Override
    public void restoreStock(int medicineId, int quantity) {
        Session s = this.factory.getObject().getCurrentSession();
        Medicines m = s.get(Medicines.class, medicineId);
        if (m != null){
            m.setStockQuantity(m.getStockQuantity() + quantity);
            s.merge(m);
        }
    }
    
}
