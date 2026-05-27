/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Payments;
import com.pkdk.pojo.PrescriptionItems;
import com.pkdk.pojo.Prescriptions;
import com.pkdk.repository.PrescriptionRepository;
import com.pkdk.service.MedicineService;
import com.pkdk.service.PaymentService;
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

    @Autowired
    private PaymentService paymentService;
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
    }

    @Override
    public List<Prescriptions> getByPatientId(int id) {
        return this.prescriptionRepo.getByPatientId(id);
    }

    @Override
    public void confirmPayment(int prescriptionId, int paymentId) {
        Prescriptions prescription = this.prescriptionRepo.getById(prescriptionId);
        if (prescription==null){
            throw new RuntimeException("Không tìm thấy đơn thuốc");
        }
        Payments payment = this.paymentService.getById(paymentId);
        if (payment==null){
            throw new RuntimeException("Không tìm thấy thông tin thanh toán");
        }
        prescription.setIsPaid(true);
        prescription.setPaymentId(payment);
        this.prescriptionRepo.save(prescription);
    }

    @Override
    public void deductStock(int prescriptionId) {
        Prescriptions prescription = this.prescriptionRepo.getById(prescriptionId);
        if (prescription==null){
            throw new RuntimeException("Không tìm thấy đơn thuốc");
        }
        for(PrescriptionItems item: prescription.getPrescriptionItemsCollection()){
            this.medicineService.deductStock(item.getMedicineId().getId(), item.getQuantity());
        }
    }
    
}
