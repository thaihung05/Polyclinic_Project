/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.Payments;

/**
 *
 * @author vanlong
 */
public interface PaymentRepository {
    Payments save(Payments payment);
    Payments getById(int id);
}
