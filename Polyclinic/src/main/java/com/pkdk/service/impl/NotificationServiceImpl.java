/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Notifications;
import com.pkdk.pojo.Users;
import com.pkdk.repository.NotificationRepository;
import com.pkdk.service.EmailService;
import com.pkdk.service.NotificationService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vanlong
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    private EmailService emailService;

    @Override
    public void save(Notifications n) {
        this.notificationRepo.save(n);
    }

    @Override
    public List<Notifications> getByUserId(int userId) {
        return this.notificationRepo.getByUserId(userId);
    }

    @Override
    public Notifications getById(int id) {
        return this.notificationRepo.getById(id);
    }

    @Override
    public Notifications createAppointmentNotification(Users user, int doctorName, String date) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Nhắc lịch hẹn khám bệnh");
        n.setMessage(String.format(
                "Bạn có lịch khám với bác sĩ %s vào lúc %s. Vui lòng có mặt đúng giờ.",
                doctorName, date));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
    }

    @Override
    public Notifications createPaySuccessNotification(Users user, String transactionId, BigDecimal amount) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Thanh toán thành công");
        n.setMessage(String.format(
                "Bạn đã thanh toán thành công %s VND. Mã giao dịch: %s.",
                amount, transactionId));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
    }

    @Override
    public void markAsRead(Notifications n) {
        n.setIsRead(true);
        this.save(n);
    }

    private void sendEmailNoti(Users u, Notifications n) {
        try {
            if (u.getEmail() != null && !u.getEmail().isEmpty()) {
                this.emailService.sendEmail(
                        u.getEmail(),
                        "[Phòng khám TH&VL] " + n.getTitle(),
                        n.getMessage()
                );
            }
        } catch (Exception e) {
            System.err.println("Email thất bại: " + e.getMessage());
        }
    }

}
