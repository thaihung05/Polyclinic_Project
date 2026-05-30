/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Medicines;
import com.pkdk.service.MedicineService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/admin/medicines")
public class AdminMedicineController {
    
    @Autowired
    private MedicineService medicineService;
    
    @GetMapping
    public String list(Model model, 
            @RequestParam(value = "tab", defaultValue = "all") String tab,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @RequestParam(value = "status", defaultValue = "") String status,
            @RequestParam(value = "lowThreshold", defaultValue = "10") int lowThreshold,
            @RequestParam(value = "nearDays", defaultValue = "30") int nearDays) {
        
        model.addAttribute("medicines", medicineService.getAll(kw, status));
        model.addAttribute("lowStockList", medicineService.getLowStock(lowThreshold));
        model.addAttribute("nearExpiryList", medicineService.getNearExiry(nearDays));
        model.addAttribute("kw", kw);
        model.addAttribute("status", status);
        model.addAttribute("lowThreshold", lowThreshold);
        model.addAttribute("nearDays", nearDays);
        model.addAttribute("activeTab", tab);
        model.addAttribute("activePage", "medicines");
        return "medicine-list";
    }
    
    @GetMapping("/add")
    public String addView(Model model){
        model.addAttribute("medicine", new Medicines());
        model.addAttribute("activePage", "medicines");
        return "medicine-form";
    }
    
    @GetMapping("/{id}")
    public String editView(Model model,
            @PathVariable("id") int id){
        model.addAttribute("medicine", medicineService.getById(id));
        model.addAttribute("activePage", "medicines");
        return "medicine-form";
    }
    
    @PostMapping
    public String save(@ModelAttribute("medicine") Medicines m,
            @RequestParam(name = "expiryDateStr", required = false) String expiryDateStr) {
        if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                try {
                    m.setExpiryDate(new SimpleDateFormat("yyyy-MM-dd").parse(expiryDateStr));
                } catch(ParseException e) {
                    
                }
            }
        medicineService.save(m);
        return "redirect:/admin/medicines";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id){
        medicineService.delete(id);
        return "redirect:/admin/medicines";
    }
    
}
