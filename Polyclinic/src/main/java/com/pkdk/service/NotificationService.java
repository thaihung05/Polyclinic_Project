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
    Notifications createAppointmentNotification(Users user, int doctorName, String date);
    Notifications createPaySuccessNotification(Users user, String transactionId, BigDecimal amount);
    void markAsRead(Notifications n);
}
