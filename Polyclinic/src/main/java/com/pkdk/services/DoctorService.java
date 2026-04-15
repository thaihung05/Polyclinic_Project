/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.services;

import com.pkdk.pojo.Doctors;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DoctorService {
    Doctors getDoctorByUserId(int userId);
    void addOrUpdate(Doctors d);
    void deleteByUserId(int userId);
    List<Doctors> getDoctorsBySpecialtyId(int specialtyId);
    Doctors getDoctorById(int id);
}
