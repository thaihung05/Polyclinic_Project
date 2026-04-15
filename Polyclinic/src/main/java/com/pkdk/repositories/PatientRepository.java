/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repositories;

import com.pkdk.pojo.Patients;

/**
 *
 * @author Admin
 */
public interface PatientRepository {
    Patients getPatientByUserId(int userId);
    void addOrUpdate(Patients p);
    void deleteByUserId(int userId);
}
