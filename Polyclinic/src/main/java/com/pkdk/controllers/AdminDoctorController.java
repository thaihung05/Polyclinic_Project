/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Specialties;
import com.pkdk.service.DoctorService;
import com.pkdk.service.SpecialtyService;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/admin/doctors")
@PropertySource("classpath:configs.properties")
public class AdminDoctorController {
    
    @Autowired
    private Environment env;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private SpecialtyService specialtyService;
    
    @GetMapping
    public String list(Model model,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @RequestParam(value = "page", defaultValue = "1") int page){
        int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
        long total = this.doctorService.countAll(kw);
        int totalPages = (int) Math.ceil((double) total/pageSize);
        
        model.addAttribute("doctors", this.doctorService.getAll(kw, page));
        model.addAttribute("kw",kw);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "doctors");
        return "doctor-list";
    }
    
    @GetMapping("/{id}")
    public String editView(@PathVariable("id") int id, Model model){
        model.addAttribute("doctor", this.doctorService.getDoctorById(id));
        model.addAttribute("specialties", this.specialtyService.getSpecs());
        model.addAttribute("activePage", "doctors");
        return "doctor-form";
    }
    
    @PostMapping("/{id}")
    public String save(@PathVariable("id") int id,
            @RequestParam("bio") String bio,
            @RequestParam("consultationFee") BigDecimal consultationFee,
            @RequestParam(value = "availableOnline", defaultValue = "false") boolean availableOnline,
            @RequestParam("specialtyId") int specialtyId,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "dateOfBirthStr", required = false) String dateOfBirthStr) {
        
        Doctors d = this.doctorService.getDoctorById(id);
        if (d!=null){
            d.setBio(bio);
            d.setConsultationFee(consultationFee);
            d.setAvailableOnline(availableOnline);
            d.setGender(gender);
            d.setAddress(address);
            if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()){
                try{
                    d.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirthStr));
                } catch (ParseException e) {
                    
                }
            }
            Specialties s = new Specialties();
            s.setId(specialtyId);
            d.setSpecialtyId(s);
            this.doctorService.addOrUpdate(d);
        }
        return "redirect:/admin/doctors";
        
    }
}
