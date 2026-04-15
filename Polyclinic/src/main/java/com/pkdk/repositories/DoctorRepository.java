/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repositories;

import com.pkdk.pojo.Doctors;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DoctorRepository {
    Doctors getDoctorByUserId(int Id);
    void addOrUpdate(Doctors d);
    void deleteByUserId(int id);
    List<Doctors> getDoctorsBySpecialtyId(int specialtyId);
}
