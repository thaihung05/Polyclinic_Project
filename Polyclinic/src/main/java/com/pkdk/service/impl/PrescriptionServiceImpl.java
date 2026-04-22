/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.PrescriptionItems;
import com.pkdk.pojo.Prescriptions;
import com.pkdk.repository.PrescriptionRepository;
import com.pkdk.service.MedicineService;
import com.pkdk.service.PrescriptionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PrescriptionServiceImpl implements PrescriptionService{
    
    @Autowired
    private PrescriptionRepository prescriptionRepo;
    
    @Autowired
    private MedicineService medicineService;

    @Override
    public List<Prescriptions> getByMedicalRecordId(int medicalRecordId) {
        return this.prescriptionRepo.getByMedicalRecordId(medicalRecordId);
    }

    @Override
    public Prescriptions getById(int id) {
        return this.prescriptionRepo.getById(id);
    }

    @Override
    public void save(Prescriptions prescription) {
        this.prescriptionRepo.save(prescription);
        for (PrescriptionItems item : prescription.getPrescriptionItemsCollection()){
            this.medicineService.deductStock(item.getMedicineId().getId(), item.getQuantity());
        };
    }
    
}
