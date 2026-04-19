/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Specialties;
import com.pkdk.repository.SpecialtyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pkdk.service.SpecialtyService;

/**
 *
 * @author Admin
 */
@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepo;

    @Override
    public List<Specialties> getSpecs() {
        return this.specialtyRepo.getSpecs();
    }

    @Override
    public Specialties getSpecialtyById(int id) {
        return this.specialtyRepo.getSpecialtyById(id);
    }

    @Override
    public void addOrUpdate(Specialties s) {
        this.specialtyRepo.addOrUpdate(s);
    }

    @Override
    public void deleteSpecialty(int id) {
        this.specialtyRepo.deleteSpecialty(id);
    }

}
