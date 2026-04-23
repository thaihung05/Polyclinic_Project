/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Medicines;
import com.pkdk.service.MedicineService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
public class ApiMedicineController {
    
    @Autowired
    private MedicineService medicineService;
    
    @GetMapping("/api/medicines")
    public ResponseEntity<?> getAll(){
        List<Medicines> list = this.medicineService.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping("/api/medicines/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id") int id){
        Medicines m = this.medicineService.getById(id);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy thuốc hợp lệ", HttpStatus.NOT_FOUND);
            
        return new ResponseEntity<>(m, HttpStatus.OK);
    }
    
    @PostMapping("/api/secure/medicines")
    public ResponseEntity<?> create(@RequestBody Medicines medicine){
        if (medicine.getName() == null)
            return new ResponseEntity<>("Tên thuốc không được trống", HttpStatus.BAD_REQUEST);
        if (medicine.getCode()== null)
            return new ResponseEntity<>("Mã thuốc không được trống", HttpStatus.BAD_REQUEST);
        medicine.setId(null);
        medicine.setIsActive(true);
        this.medicineService.save(medicine);
        return new ResponseEntity<>(medicine, HttpStatus.CREATED);
    }
    
    @PutMapping("/api/secure/medicines/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id,
            @RequestBody Medicines medicine){
        
        Medicines m = this.medicineService.getById(id);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy thuốc hợp lệ", HttpStatus.NOT_FOUND);
        
        m.setCode(medicine.getCode());
        m.setName(medicine.getName());
        m.setCategory(medicine.getCategory());
        m.setConcentration(medicine.getConcentration());
        m.setExpiryDate(medicine.getExpiryDate());
        m.setGenericName(medicine.getGenericName());
        m.setUnit(medicine.getUnit());
        m.setManufacturer(medicine.getManufacturer());
        m.setStockQuantity(medicine.getStockQuantity());
        m.setPrice(medicine.getPrice());
        
        this.medicineService.save(m);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }
    
    @DeleteMapping("/api/secure/medicines/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        Medicines m = this.medicineService.getById(id);
        if (m==null)
            return new ResponseEntity<>("Không tìm thấy thuốc hợp lệ", HttpStatus.NOT_FOUND);
        
        this.medicineService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/api/secure/medicines/alerts/low-stock")
    public ResponseEntity<?> lowStockAlert(@RequestParam(name = "threshold", defaultValue = "10") int threshold){
        List<Medicines> list = this.medicineService.getLowStock(threshold);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping("/api/secure/medicines/alerts/near-expiry")
    public ResponseEntity<?> nearExpiryAlert(@RequestParam(name = "days", defaultValue = "30") int days){
        List<Medicines> list = this.medicineService.getNearExiry(days);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
}
