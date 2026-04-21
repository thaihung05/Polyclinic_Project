/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Medicines;
import com.pkdk.repository.MedicineRepository;
import com.pkdk.service.MedicineService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class MedicineServiceImpl implements MedicineService{
    
    @Autowired
    private MedicineRepository medicineRepo;

    @Override
    public List<Medicines> getAll() {
        return this.medicineRepo.getAll();
    }

    @Override
    public Medicines getById(int id) {
        return this.medicineRepo.getById(id);
    }
    
}
