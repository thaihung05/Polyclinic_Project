
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Doctors;
import com.pkdk.repository.DoctorRepository;
import com.pkdk.service.DoctorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author Admin
 */
@Service
public class DoctorServiceImpl implements DoctorService{
    @Autowired
    private DoctorRepository doctorRepo;
    
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
        return this.doctorRepo.getDoctorById(id);
    }

    @Override
    public List<Doctors> getAll(String kw, int page) {
        return this.doctorRepo.getAll(kw, page);
    }

    @Override
    public long countAll(String kw) {
        return this.doctorRepo.countAll(kw);
    }
}
