/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

/**
 *
 * @author vanlong
 */
public interface QrService {
    String generateMomoQR(String amount, String description);
    String generateBankingQR(String amount, String description);
}
