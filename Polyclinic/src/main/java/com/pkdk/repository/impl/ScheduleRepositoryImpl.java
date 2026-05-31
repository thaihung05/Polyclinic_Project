/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository.impl;

import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.repository.ScheduleRepository;
import java.util.Date;
import java.util.List;
import org.hibernate.LockMode;
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
public class ScheduleRepositoryImpl implements ScheduleRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<DoctorSchedules> getDoctorById(int doctorId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM DoctorSchedules d Where d.doctorId.id = :doctorId", DoctorSchedules.class)
                .setParameter("doctorId", doctorId);
        return q.getResultList();
    }

    @Override
    public DoctorSchedules getById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(DoctorSchedules.class, id);
    }

    @Override
    public void save(DoctorSchedules schedule) {
        Session s = this.factory.getObject().getCurrentSession();
        if (schedule.getId() == null) {
            s.persist(schedule);
        } else {
            s.merge(schedule);
        }
    }

    @Override
    public void delete(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        DoctorSchedules schedule = s.get(DoctorSchedules.class, id);
        if (schedule != null) {
            s.remove(schedule);
        }
    }

    @Override
    public boolean isOverlap(int doctorId, Date start, Date end, Integer excludeId) {
        Session s = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(d) FROM DoctorSchedules d "
                + "WHERE d.doctorId.id = :doctorId "
                + "AND d.startTime < :end AND d.endTime > :start"
                + (excludeId != null ? " AND d.id <> :excludeId" : "");
        Query q = s.createQuery(hql, Long.class)
                .setParameter("doctorId", doctorId)
                .setParameter("start", start)
                .setParameter("end", end);
        if (excludeId != null) {
            q.setParameter("excludeId", excludeId);
        }
        Long count = (Long) q.getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public DoctorSchedules getByDoctorAndStartTime(int doctorId, Date startTime) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery(
                "FROM DoctorSchedules d WHERE d.doctorId.id = :doctorId AND d.startTime = :startTime",
                DoctorSchedules.class)
                .setParameter("doctorId", doctorId)
                .setParameter("startTime", startTime)
                .setMaxResults(1);
        List<DoctorSchedules> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public DoctorSchedules getByIdWithLock(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(DoctorSchedules.class, id, LockMode.PESSIMISTIC_WRITE);
    }
    
    

}
