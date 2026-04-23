/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Notifications;
import com.pkdk.repository.NotificationRepository;
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
public class NotificationRepositoryImpl implements NotificationRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public void save(Notifications n) {
        Session s = this.factory.getObject().getCurrentSession();
        if (n.getId() == null) {
            s.persist(n);
        } else {
            s.merge(n);
        }

    }

    @Override
    public List<Notifications> getByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Notifications n WHERE n.userId.id = :uid", Notifications.class);
        q.setParameter("uid", userId);
        List<Notifications> list = q.getResultList();
        return list.isEmpty() ? null : list;
    }

    @Override
    public Notifications getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Notifications.class, id);
    }

}
