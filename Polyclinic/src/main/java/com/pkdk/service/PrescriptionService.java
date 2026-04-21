/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Prescriptions;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface PrescriptionService {
    List<Prescriptions> getByMedicalRecordId(int medicalRecordId);
    Prescriptions getById(int id);
    void save(Prescriptions prescription);
}
