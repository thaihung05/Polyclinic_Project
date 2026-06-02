/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Doctors;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DoctorService {
    List<Doctors> getAll(String kw, int page);
    long countAll(String kw);
    Doctors getDoctorByUserId(int userId);
    void addOrUpdate(Doctors d);
    void deleteByUserId(int userId);
    List<Doctors> getDoctorsBySpecialtyId(int specialtyId);
    Doctors getDoctorById(int id);
    List<Doctors> getAllDoctors();
}
