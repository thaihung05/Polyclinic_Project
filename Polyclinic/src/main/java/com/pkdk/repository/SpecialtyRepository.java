/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.Specialties;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface SpecialtyRepository {
    List<Specialties> getSpecs();
    Specialties getSpecialtyById(int id);
    void addOrUpdate(Specialties s);
    void deleteSpecialty(int id);
}
