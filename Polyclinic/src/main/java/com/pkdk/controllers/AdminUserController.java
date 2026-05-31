/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Users;
import com.pkdk.service.DoctorService;
import com.pkdk.service.PatientService;
import com.pkdk.service.SpecialtyService;
import com.pkdk.service.UserService;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/admin/users")
@PropertySource("classpath:configs.properties")
public class AdminUserController {
    
    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private PatientService patientService;

    @GetMapping
    public String listUser(Model model, @RequestParam Map<String, String> params) {
        String kw = params.getOrDefault("kw", "");
        int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
        int currentPage = Integer.parseInt(params.getOrDefault("page", "1"));
        int totalPages = (int) Math.ceil((double) this.userService.countUsers(kw) / pageSize);
        model.addAttribute("users", this.userService.getUsers(params));
        model.addAttribute("kw", kw);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "users");
        return "users";
    }

    @GetMapping("/add")
    public String addView(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("specialties", this.specialtyService.getSpecs());
        model.addAttribute("activePage", "users");
        return "user-form";
    }
    
    @GetMapping("/{id}")
    public String updateView(Model model, @PathVariable("id") int id) {
        Users u = this.userService.getUserById(id);

        model.addAttribute("user", u);
        model.addAttribute("specialties", this.specialtyService.getSpecs());
        model.addAttribute("activePage", "users");

        if ("ROLE_DOCTOR".equals(u.getRole())) {
            Doctors d = this.doctorService.getDoctorByUserId(id);
            if (d != null) {
                model.addAttribute("doctor", d);
                if (d.getSpecialtyId() != null){
                    model.addAttribute("specialtyId",d.getSpecialtyId().getId());
                }
            }
        } else if ("ROLE_PATIENT".equals(u.getRole())) {
            model.addAttribute("patient", this.patientService.getPatientByUserId(id));
        }
        return "user-form";
    }
    
    @PostMapping
    public String addUser(@ModelAttribute("user") Users u,
            @RequestParam(name = "rawPassword", required = false) String rawPassword,
            @RequestParam(name = "specialtyId", required = false) Integer specialtyId,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "dateOfBirthStr", required = false) String dateOfBirthStr,
            @RequestParam(name = "bio", required = false) String bio,
            @RequestParam(name = "consultationFee", required = false) BigDecimal consultationFee,
            @RequestParam(name = "availableOnline", defaultValue = "false") boolean availableOnline){
        
        this.userService.addOrUpdate(u, rawPassword, specialtyId);
        if ("ROLE_DOCTOR".equals(u.getRole())) {
            Doctors d = this.doctorService.getDoctorByUserId(u.getId());
            if (d != null) {
                if (bio != null && !bio.isEmpty()) d.setBio(bio);
                if (consultationFee != null) d.setConsultationFee(consultationFee);
                d.setAvailableOnline(availableOnline);
                if (gender != null && !gender.isEmpty()) d.setGender(gender);
                if (address != null && !address.isEmpty()) d.setAddress(address);
                if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                    try { d.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirthStr)); }
                    catch (ParseException e) { }
                }
                this.doctorService.addOrUpdate(d);
            }
        } else if ("ROLE_PATIENT".equals(u.getRole())) {
            Patients p = this.patientService.getPatientByUserId(u.getId());
            if (p != null) {
                if (gender != null && !gender.isEmpty()) p.setGender(gender);
                if (address != null && !address.isEmpty()) p.setAddress(address);
                if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                    try { p.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirthStr)); }
                    catch (ParseException e) { }
                }
                this.patientService.addOrUpdate(p);
            }
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        this.userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
