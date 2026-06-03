/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.Users;
import com.pkdk.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@PropertySource("classpath:configs.properties")
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        String kw = params != null ? params.getOrDefault("kw", ""):"";
        
        String hql = "FROM Users u WHERE 1=1 ";
        
        if (kw != null && !kw.trim().isEmpty())
            hql += "AND (LOWER(u.name) LIKE :kw OR LOWER(u.username) LIKE :kw) ";
        hql += "ORDER BY u.id ASC";
        
        Query q = s.createQuery(hql, Users.class);
        if (!kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");

        if (params != null) {
            int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            q.setMaxResults(pageSize);
            q.setFirstResult((page-1)*pageSize);
        }

        return q.getResultList();
    }

    @Override
    public Users getUserById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Users.class, id);
    }

    @Override
    public Users getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        List<Users> users = s.createQuery(
                "FROM Users u WHERE u.username = :username", Users.class)
                .setParameter("username", username)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public void saveOrUpdate(Users u) {
        Session s = this.factory.getObject().getCurrentSession();
        if (u.getId() == null) {
            s.persist(u);
        } else {
            s.merge(u);
        }
    }

    @Override
    public void deleteUser(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Users u = s.get(Users.class, id);
        if (u != null) {
            s.remove(u);
        }
    }
    
    @Override
    public boolean authenticate(String username, String password) {
        Users u = this.getUserByUsername(username);
        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public long countUsers(String kw) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(u.id) FROM Users u WHERE 1=1 ";
        if (kw != null && !kw.trim().isEmpty())
            hql += "AND (LOWER(u.name) LIKE :kw OR LOWER(u.username) LIKE :kw)";
        Query<Long> q = s.createQuery(hql, Long.class);
        if (kw != null && !kw.trim().isEmpty())
            q.setParameter("kw", "%" + kw.trim().toLowerCase() + "%");
        return q.getSingleResult();
    }

    @Override
    public Users getUserByEmail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        List<Users> users = s.createQuery("FROM Users u WHERE u.email = :email" ,Users.class)
                .setParameter("email", email).getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<Users> getUsersByRole(String role) {
        Session s = this.factory.getObject().getCurrentSession();
        List<Users> users = s.createQuery("FROM Users u WHERE u.role = :role AND u.isActive = true" ,Users.class)
                .setParameter("role", role).getResultList();
        
        return users;
    }
}
