/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Medicines;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface MedicineService {
    List<Medicines> getAll();
    Medicines getById(int id);
}
