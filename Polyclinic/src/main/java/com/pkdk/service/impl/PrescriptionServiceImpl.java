/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.enums.PayMethodEnum;
import com.pkdk.enums.PaymentStatus;
import com.pkdk.pojo.Medicines;
import com.pkdk.pojo.Payments;
import com.pkdk.pojo.PrescriptionItems;
import com.pkdk.pojo.Prescriptions;
import com.pkdk.repository.PrescriptionRepository;
import com.pkdk.service.MedicineService;
import com.pkdk.service.PaymentService;
import com.pkdk.service.PrescriptionService;
import com.pkdk.service.QrService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    @Autowired
    private QrService qrService;
    
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
        for (PrescriptionItems item : prescription.getPrescriptionItemsCollection()) {
            Medicines med = this.medicineService.getById(item.getMedicineId().getId());
            if (med == null)
                throw new IllegalArgumentException("Không tìm thấy thuốc id=" + item.getMedicineId().getId());
            if (item.getQuantity() > med.getStockQuantity())
                throw new IllegalArgumentException(
                    "Thuốc \"" + med.getName() + "\" chỉ còn " + med.getStockQuantity() + " " + med.getUnit() +
                    ", không thể kê " + item.getQuantity()
                );
        }
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

    @Override
    public Payments createPayment(int prescriptionId) {
        Prescriptions prescription = this.prescriptionRepo.getById(prescriptionId);
        if (prescription==null){
            throw new RuntimeException("Không tìm thấy đơn thuốc");
        }
        
        BigDecimal totalAmount = prescription.getPrescriptionItemsCollection()
                .stream().map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String transactionId = "MEDICINE-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String description = "Thanh toán đơn thuốc #" + prescriptionId;
        String qrUrl = this.qrService.generateBankingQR(totalAmount.toPlainString(), description);
        
        Payments payment = new Payments();
        payment.setAmount(totalAmount);
        payment.setMethod(PayMethodEnum.BANKING.toString());
        payment.setStatus(PaymentStatus.PENDING.toString());
        payment.setTransactionId(transactionId);
        payment.setDescription(description);
        payment.setNgayTao(new Date());
        payment.setQrUrl(qrUrl);
        
        this.paymentService.save(payment);
        prescription.setPaymentId(payment);
        this.prescriptionRepo.save(prescription);
        return payment;
    }
    
}
