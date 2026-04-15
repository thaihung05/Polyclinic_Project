/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.services.impl;

import com.pkdk.pojo.Doctors;
import com.pkdk.repositories.DoctorRepository;
import com.pkdk.services.DoctorService;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Admin
 */
@Service
@Transactional
public class DoctorServiceImpl implements DoctorService{
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private LocalSessionFactoryBean factory;
    @Override
    public Doctors getDoctorByUserId(int userId) {
        return this.doctorRepo.getDoctorByUserId(userId);
    }
    @Override
    public void addOrUpdate(Doctors d) {
        this.doctorRepo.addOrUpdate(d);
    }
    @Override
    public void deleteByUserId(int userId) {
        this.doctorRepo.deleteByUserId(userId);
    }
    @Override
    public List<Doctors> getDoctorsBySpecialtyId(int specialtyId) {
        return this.doctorRepo.getDoctorsBySpecialtyId(specialtyId);
    }
    @Override
    public Doctors getDoctorById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Doctors.class, id);
    }
}
