/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.MedicalRecords;

/**
 *
 * @author Admin
 */
public interface MedicalRecordRepository {
    MedicalRecords getByAppointmentId(int appointmentId);
    MedicalRecords getById(int id);
    void save(MedicalRecords record);
}
