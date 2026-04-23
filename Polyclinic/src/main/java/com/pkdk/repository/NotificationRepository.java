/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.Notifications;
import java.util.List;

/**
 *
 * @author vanlong
 */
public interface NotificationRepository {
    void save(Notifications n);
    List<Notifications> getByUserId(int userId);
    Notifications getById(int id);
}
