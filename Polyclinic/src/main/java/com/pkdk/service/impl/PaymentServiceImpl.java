/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Payments;
import com.pkdk.pojo.Users;
import com.pkdk.repository.AppointmentRepository;
import com.pkdk.repository.PaymentRepository;
import com.pkdk.service.NotificationService;
import com.pkdk.service.PaymentService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vanlong
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private NotificationService notificationService;

    @Override
    public Payments createPending(int appointmentId, String method) {
        Appointments appointment = this.appointmentRepo.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("Không tìm thấy lịch hẹn");
        }
        if (appointment.getPaymentId() != null) {
            throw new RuntimeException("Lịch hẹn này đã được tạo phiếu thanh toán");
        }

        BigDecimal amount = appointment.getDoctorId().getConsultationFee();

        Payments payment = new Payments();
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus("PENDING");
        payment.setTransactionId("MOMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setDescription("Thanh toán khám bệnh - Lịch hẹn #" + appointmentId);
        payment.setNgayTao(new Date());
        this.paymentRepo.save(payment);

        appointment.setPaymentId(payment);
        this.appointmentRepo.save(appointment);

        return payment;
    }

    @Override
    public Payments confirm(int appointmentId) {
        Appointments appointment = appointmentRepo.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("Không tìm thấy lịch hẹn");
        }

        Payments payment = appointment.getPaymentId();
        if (payment == null) {
            throw new RuntimeException("Chưa khởi tạo thanh toán");
        }

        if ("COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("Lịch hẹn này đã thanh toán rồi");
        }

        payment.setStatus("COMPLETED");
        paymentRepo.save(payment);

        // Cập nhật status appointment
        appointment.setStatus("CONFIRMED");
        appointmentRepo.save(appointment);

        Users u = appointment.getPatientId().getUserId();
        this.notificationService.createPaySuccessNotification(u, payment.getTransactionId(), payment.getAmount());

        return payment;
    }

}
