/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Patients;

/**
 *
 * @author Admin
 */
public interface PatientService {
    Patients getPatientByUserId(int userId);
    Patients getPatientById(int id);
    void addOrUpdate(Patients p);
    void deleteByUserId(int userId);
}
