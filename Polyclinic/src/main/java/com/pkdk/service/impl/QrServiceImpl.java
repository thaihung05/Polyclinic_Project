/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.service.QrService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author vanlong
 */
@Service
public class QrServiceImpl implements QrService {

    private static final String MOMO_BANK_CODE = "MOMO";          
    private static final String MOMO_ACCOUNT = "0123456789";  
    private static final String BANKING_BANK_CODE = "TCB";
    private static final String BANKING_ACCOUNT = "1234567890";
    private static final String ACCOUNT_NAME = "PHONG KHAM TH VL";

    @Override
    public String generateMomoQR(String amount, String description) {
        return buildVietQRUrl(MOMO_BANK_CODE, MOMO_ACCOUNT, amount, description);
    }

    @Override
    public String generateBankingQR(String amount, String description) {
        return buildVietQRUrl(BANKING_BANK_CODE, BANKING_ACCOUNT, amount, description);
    }

    private String buildVietQRUrl(String bankCode, String accountNo, String amount, String description) {
        try {
            String encodedName = URLEncoder.encode(ACCOUNT_NAME, "UTF-8");
            String encodedDesc = URLEncoder.encode(description, "UTF-8");
            return String.format(
                    "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                    bankCode, accountNo, amount, encodedDesc, encodedName
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi tạo QR: " + e.getMessage());
        }
    }

}
