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
    List<Medicines> getAll(String kw, String status);
    Medicines getById(int id);
    void save(Medicines medicine);
    void delete(int id);
    List<Medicines> getLowStock(int threshold);
    List<Medicines> getNearExiry(int days);
    void deductStock(int medicineId, int quantity);
    void restoreStock(int medicineId, int quantity);
}
