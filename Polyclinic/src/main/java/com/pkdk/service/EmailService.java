/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

/**
 *
 * @author vanlong
 */
public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
}
