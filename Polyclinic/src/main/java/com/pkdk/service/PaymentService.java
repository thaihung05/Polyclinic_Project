/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Payments;

/**
 *
 * @author vanlong
 */
public interface PaymentService {
    void save(Payments payment);
    Payments getById(int id);
    Payments createPending(int appointmentId, String method);
    Payments confirm(int appointmentId);
}
