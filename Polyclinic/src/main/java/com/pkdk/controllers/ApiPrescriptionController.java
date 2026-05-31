/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.enums.PayMethodEnum;
import com.pkdk.enums.PaymentStatus;
import com.pkdk.enums.UserRole;
import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.MedicalRecords;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Payments;
import com.pkdk.pojo.PrescriptionItems;
import com.pkdk.pojo.PrescriptionReservations;
import com.pkdk.pojo.Prescriptions;
import com.pkdk.pojo.Users;
import com.pkdk.service.MedicalRecordService;
import com.pkdk.service.NotificationService;
import com.pkdk.service.PatientService;
import com.pkdk.service.PaymentService;
import com.pkdk.service.PrescriptionReservationService;
import com.pkdk.service.PrescriptionService;
import com.pkdk.service.QrService;
import com.pkdk.service.UserService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
public class ApiPrescriptionController {
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private QrService qrService;
    
    @Autowired
    private PrescriptionReservationService prescriptionReservationService; 
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/api/medical-records/{recordId}/prescriptions")
    public ResponseEntity<?> getPrescriptions(@PathVariable("recordId") int recordId){
        MedicalRecords m = this.medicalRecordService.getById(recordId);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy hồ sơ bệnh án",HttpStatus.NOT_FOUND);
        List<Prescriptions> list = this.prescriptionService.getByMedicalRecordId(recordId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping("/api/prescriptions/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id")int id){
        Prescriptions p = this.prescriptionService.getById(id);
        if (p==null)
            return new ResponseEntity<>("Không tìm thấy đơn thuốc",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/medical-records/{recordId}/prescriptions")
    public ResponseEntity<?> createPrescription(@PathVariable("recordId") int recordId,
            @RequestBody Prescriptions prescription, Principal principal){
        
        Users caller = this.userService.getUserByUserName(principal.getName());
        if (caller == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        if (!UserRole.ROLE_DOCTOR.name().equals(caller.getRole()))
            return new ResponseEntity<>("Chỉ có bác sĩ mới có quyền tạo đơn thuốc mới",HttpStatus.FORBIDDEN);
        
        MedicalRecords m = this.medicalRecordService.getById(recordId);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy hồ sơ bệnh án",HttpStatus.NOT_FOUND);
        
        prescription.setId(null);
        prescription.setMedicalRecordId(m);
        prescription.setNgayTao(new Date());
        
        if (prescription.getPrescriptionItemsCollection()!=null)
            for (PrescriptionItems item : prescription.getPrescriptionItemsCollection())
                item.setPrescriptionId(prescription);
        
        try {
            this.prescriptionService.save(prescription);
            this.prescriptionService.deductStock(prescription.getId());
            Appointments a = m.getAppointmentId();
            this.prescriptionReservationService.create(prescription, a.getPatientId(), a.getDoctorId());
            PrescriptionReservations reservation = this.prescriptionReservationService.getByPrescriptionId(prescription.getId());
            Users patientUser = a.getPatientId().getUserId();
            this.notificationService.createPrescriptionCreatedNotification(patientUser, caller.getName(), reservation.getExpiresAt());
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    @GetMapping("/api/secure/patient/prescriptions")
    public ResponseEntity<?> getPatientPrescriptions(Principal principal){
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        
        Patients patient = this.patientService.getPatientByUserId(u.getId());
        if (patient == null)
            return new ResponseEntity<>("Không tìm thấy bệnh nhân", HttpStatus.NOT_FOUND);
        
        List<Prescriptions> list = this.prescriptionService.getByPatientId(patient.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/prescriptions/{prescriptionId}/payment/create")
    public ResponseEntity<?> createPrescriptionPayment(Principal principal, @PathVariable("prescriptionId") int prescriptionId){
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        
        Prescriptions prescription = this.prescriptionService.getById(prescriptionId);
        if (prescription == null){
            return new ResponseEntity<>("Không tìm thấy đơn thuốc", HttpStatus.NOT_FOUND);
        }
        
        if (prescription.getIsPaid()==true)
            return new ResponseEntity<>("Đơn thuốc này đã được thanh toán", HttpStatus.BAD_REQUEST);
        
        if (prescription.getPaymentId() != null){
            return new ResponseEntity<>(createPaymentResponse(prescription.getPaymentId(), prescription), HttpStatus.OK);
        }
        BigDecimal totalAmount = prescription.getPrescriptionItemsCollection()
                .stream().map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String transactionId = "MEDICINE-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String description = "Thanh toán đơn thuốc #" + prescriptionId;
        String qrUrl = this.qrService.generateBankingQR(transactionId, totalAmount.toPlainString(), description);
        
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
        this.prescriptionService.save(prescription);
        
        return new ResponseEntity<>(createPaymentResponse(payment, prescription), HttpStatus.CREATED);             
    }
    
    @PostMapping("/api/secure/prescriptions/{prescriptionId}/payment/confirm")
    public ResponseEntity<?> confirmPrescriptionPayment(@PathVariable("prescriptionId") int prescriptionId, Principal principal){
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        
        Prescriptions prescription = this.prescriptionService.getById(prescriptionId);
        if (prescription == null){
            return new ResponseEntity<>("Không tìm thấy đơn thuốc", HttpStatus.NOT_FOUND);
        }
        
        if (prescription.getIsPaid()==true)
            return new ResponseEntity<>("Đơn thuốc này đã được thanh toán", HttpStatus.BAD_REQUEST);
        
        PrescriptionReservations r = this.prescriptionReservationService.getByPrescriptionId(prescriptionId);
        if (r != null && Boolean.TRUE.equals(r.getIsExpired()))
            return new ResponseEntity<>("Đơn thuốc đã hết hạn thanh toán. Vui lòng liên hệ bác sĩ kê đơn mới.", HttpStatus.BAD_REQUEST);
        
        Payments payment = prescription.getPaymentId();
        
        if(payment == null)
            return new ResponseEntity<>("Chưa khởi tạo thanh toán cho đơn thuốc này", HttpStatus.BAD_REQUEST);
        
        try{
            payment.setStatus(PaymentStatus.COMPLETED.name());
            this.paymentService.save(payment);
            this.prescriptionService.confirmPayment(prescriptionId, payment.getId());
            this.prescriptionReservationService.confirmPaid(prescriptionId);
            
        }
        catch(RuntimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    @GetMapping("/api/secure/pharmacist/prescriptions")
    public ResponseEntity<?> getPharmacistPrescriptions(Principal principal){
        
        Users u = this.userService.getUserByUserName(principal.getName());
        
        if (u == null) 
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        try {
            List<PrescriptionReservations> list = this.prescriptionReservationService.getPaidAndNotDispensed();
            return new ResponseEntity<>(list,HttpStatus.OK);
        } catch (RuntimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/api/secure/pharmacist/prescriptions/{id}/dispense")
    public ResponseEntity<?> dispensePrescription(@PathVariable("id") int id,
            Principal principal){
        
        Users u = this.userService.getUserByUserName(principal.getName());
        
        if (u == null)
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        
        PrescriptionReservations p = this.prescriptionReservationService.getByPrescriptionId(id);
        if (p == null)
            return new ResponseEntity<>("Không tìm thấy đơn thuốc", HttpStatus.NOT_FOUND);
        if (!Boolean.TRUE.equals(p.getIsPaid()))
            return new ResponseEntity<>("Đơn thuốc chưa được thanh toán", HttpStatus.BAD_REQUEST);
        if (Boolean.TRUE.equals(p.getIsDispensed()))
            return new ResponseEntity<>("Đơn thuốc đã được cấp phát trước đó", HttpStatus.BAD_REQUEST);
        
        try{
            this.prescriptionReservationService.confirmDispensed(id);
            Prescriptions pre = this.prescriptionService.getById(id);
            pre.setIsDispensed(true);
            this.prescriptionService.save(pre);
            Users patientUser = p.getPatientId().getUserId();
            String doctorName = p.getDoctorId().getUserId().getName();
            this.notificationService.createPrescriptionDispensedNotification(patientUser, doctorName);
            
            
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    private Map<String,Object> createPaymentResponse(Payments payment, Prescriptions prescription){
        Map<String,Object> res = new HashMap<>();
        res.put("paymentId", payment.getId());
        res.put("qrUrl", payment.getQrUrl());
        res.put("totalAmount", payment.getAmount());
        res.put("transactionId", payment.getTransactionId());
        res.put("prescriptionId", prescription.getId());
        return res;
        
    }
    
}
