/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.enums.AppointmentStatus;
import com.pkdk.enums.PayMethodEnum;
import com.pkdk.enums.PaymentStatus;
import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Payments;
import com.pkdk.pojo.Users;
import com.pkdk.repository.AppointmentRepository;
import com.pkdk.repository.PaymentRepository;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.NotificationService;
import com.pkdk.service.PaymentService;
import com.pkdk.service.QrService;
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
    private AppointmentService appointmentService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private QrService qrService;

    @Override
    public Payments createPending(int appointmentId, String method) {
        Appointments appointment = this.appointmentService.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("Không tìm thấy lịch hẹn");
        }
        if (appointment.getPaymentId() != null) {
            throw new RuntimeException("Lịch hẹn này đã được tạo phiếu thanh toán");
        }
        if (!method.equals(PayMethodEnum.MOMO.toString()) && !method.equals(PayMethodEnum.BANKING.toString())) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ");
        }

        BigDecimal amount = appointment.getDoctorId().getConsultationFee();
        String transactionId = method + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String description = "Thanh toán khám bệnh - Lịch hẹn #" + appointmentId;

        String qrUrl = "";
        if (method.equals(PayMethodEnum.MOMO.toString())) {
            qrUrl = this.qrService.generateMomoQR(transactionId, amount.toPlainString(), description);
        } else {
            qrUrl = this.qrService.generateBankingQR(transactionId, amount.toPlainString(), description);
        }

        Payments payment = new Payments();
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING.toString());
        payment.setTransactionId(transactionId);
        payment.setDescription(description);
        payment.setNgayTao(new Date());
        payment.setQrUrl(qrUrl);
        this.save(payment);

        appointment.setPaymentId(payment);
        this.appointmentService.save(appointment);

        return payment;
    }

    @Override
    public Payments confirm(int appointmentId) {
        Appointments appointment = this.appointmentService.getById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("Không tìm thấy lịch hẹn");
        }

        Payments payment = appointment.getPaymentId();
        if (payment == null) {
            throw new RuntimeException("Chưa khởi tạo thanh toán");
        }

        if (AppointmentStatus.COMPLETED.toString().equals(payment.getStatus())) {
            throw new RuntimeException("Lịch hẹn này đã thanh toán rồi");
        }

        payment.setStatus(PaymentStatus.COMPLETED.toString());
        this.save(payment);

        appointment.setStatus(AppointmentStatus.CONFIRMED.toString());
        this.appointmentService.save(appointment);

        Users u = appointment.getPatientId().getUserId();
        this.notificationService.createPaySuccessNotification(u, payment.getTransactionId(), payment.getAmount());

        return payment;
    }

    @Override
    public void save(Payments payment) {
        this.paymentRepo.save(payment);
    }

    @Override
    public Payments getById(int id) {
        return this.paymentRepo.getById(id);
    }

}
