/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Notifications;
import com.pkdk.pojo.Users;
import com.pkdk.repository.NotificationRepository;
import com.pkdk.service.EmailService;
import com.pkdk.service.NotificationService;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    public Notifications createAppointmentNotification(Users user, String doctorName, String date) {
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

    @Override
    public Notifications createFollowUpNotification(Users user, String doctorName, String scheduledAt) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Lịch tái khám mới!");
        n.setMessage(String.format(
                "Bác sĩ %s đã đặt lịch tái khám cho bạn vào lúc %s. Vui lòng chuẩn bị kết quả xét nghiệm.",
                doctorName, scheduledAt));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
    }

    @Override
    public Notifications createNewBookingNotificationForDoctor(Users doctorUser, String patientName, String scheduledAt) {
        Notifications n = new Notifications();
        n.setUserId(doctorUser);
        n.setTitle("Lịch hẹn mới");
        n.setMessage(String.format(
                "Bệnh nhân %s vừa đặt lịch khám với bạn vào lúc %s.",
                patientName, scheduledAt));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(doctorUser, n);
        return n;
    }

    @Override
    public Notifications createCancelNotificationForPatient(Users user, String scheduledAt, String cancelledBy) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Lịch hẹn đã bị hủy");
        n.setMessage(String.format("Lịch khám vào lúc %s của bạn đã bị hủy bởi %s.",scheduledAt, cancelledBy));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
        
    }

    @Override
    public Notifications createCancelNotificationForDoctor(Users doctorUser, String patientName, String scheduledAt) {
        Notifications n = new Notifications();
        n.setUserId(doctorUser);
        n.setTitle("Lịch hẹn đã bị hủy");
        n.setMessage(String.format(
                "Bệnh nhân %s đã hủy lịch khám vào lúc %s.",
                patientName, scheduledAt));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(doctorUser, n);
        return n;
    }

    @Override
    public Notifications createPrescriptionDispensedNotification(Users user, String doctorName) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Đơn thuốc đã sẵn sàng!");
        n.setMessage(String.format("Đơn thuốc của bạn do bác sĩ %s kê, đã được dược sĩ cấp nhận cấp phát. Vui lòng đến quầy thuốc để nhận thuốc.", doctorName));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
    }

    @Override
    public Notifications createPrescriptionCreatedNotification(Users user, String doctorName, Date expiresAt) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Bác sĩ vừa kê đơn thuốc cho bạn!");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String deadline = sdf.format(expiresAt);
        n.setMessage(String.format(
            "Bác sĩ %s vừa kê đơn thuốc cho bạn. Vui lòng vào mục Lịch sử khám bệnh để xem và thanh toán trước %s để tránh bị hủy đơn.",
            doctorName, deadline));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
    }

    @Override
    public Notifications createPrescriptionExpiredNotification(Users user, Date expiresAt) {
        Notifications n = new Notifications();
        n.setUserId(user);
        n.setTitle("Đơn thuốc đã hết hạn thanh toán");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String expiredTime = sdf.format(expiresAt);
        n.setMessage(String.format(
            "Đơn thuốc của bạn đã hết thời hạn thanh toán lúc %s và bị hủy. Vui lòng liên hệ bác sĩ để được kê đơn lại.",
            expiredTime));
        n.setNgayTao(new Date());
        this.save(n);
        this.sendEmailNoti(user, n);
        return n;
    }

}
