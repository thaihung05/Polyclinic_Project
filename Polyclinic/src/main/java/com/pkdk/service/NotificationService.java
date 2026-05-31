/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Notifications;
import com.pkdk.pojo.Users;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author vanlong
 */
public interface NotificationService {
    void save(Notifications n);
    List<Notifications> getByUserId(int userId);
    Notifications getById(int id);
    Notifications createAppointmentNotification(Users user, String doctorName, String date);
    Notifications createPaySuccessNotification(Users user, String transactionId, BigDecimal amount);
    Notifications createFollowUpNotification(Users user, String doctorName, String scheduledAt);
    Notifications createNewBookingNotificationForDoctor(Users doctorUser, String patientName, String scheduledAt);
    Notifications createCancelNotificationForPatient(Users user, String scheduledAt, String cancelledBy);
    Notifications createCancelNotificationForDoctor(Users doctorUser, String patientName, String scheduledAt);
    void markAsRead(Notifications n);
    Notifications createPrescriptionDispensedNotification(Users user, String doctorName);
    Notifications createPrescriptionExpiredNotification(Users user, java.util.Date expiresAt);
    Notifications createPrescriptionCreatedNotification(Users user, String doctorName, java.util.Date expiresAt);
}
