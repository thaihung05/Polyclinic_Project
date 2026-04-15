/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Specialties;
import com.pkdk.services.DoctorService;
import com.pkdk.services.SpecialtyService;
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
@RequestMapping("/admin/specialties")
public class AdminSpecialtyController {

    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("specialties", this.specialtyService.getSpecs());
        model.addAttribute("pageTitle", "Quản lý chuyên khoa");
        model.addAttribute("pageDescription", "Danh sách chuyên khoa trong hệ thống");
        model.addAttribute("activePage", "specialties");
        return "specialties";
    }

    @GetMapping("add")
    public String addView(Model model) {
        model.addAttribute("specialty", new Specialties());
        model.addAttribute("pageTitle", "Thêm chuyên khoa");
        model.addAttribute("pageDescription", "Nhập thông tin chuyên khoa mới");
        model.addAttribute("activePage", "specialties");
        return "specialty-form";
    }

    @GetMapping("/{id}")
    public String updateView(Model model, @PathVariable(value = "id") int id) {
        model.addAttribute("specialty", this.specialtyService.getSpecialtyById(id));
        model.addAttribute("pageTitle", "Cập nhật chuyên khoa");
        model.addAttribute("pageDescription", "Chỉnh sửa thông tin chuyên khoa");
        model.addAttribute("activePage", "specialties");
        return "specialty-form";
    }

    @PostMapping
    public String addOrUpdate(@ModelAttribute(value = "specialty") Specialties s) {
        this.specialtyService.addOrUpdate(s);
        return "redirect:/admin/specialties";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") int id) {
        this.specialtyService.deleteSpecialty(id);
        return "redirect:/admin/specialties";
    }

    @GetMapping("/{id}/doctors")
    public String specialtyDetail(Model model,
            @PathVariable("id") int id) {

        model.addAttribute("specialty",
                this.specialtyService.getSpecialtyById(id));

        model.addAttribute("doctors",
                this.doctorService.getDoctorsBySpecialtyId(id));

        model.addAttribute("allSpecialties",
                this.specialtyService.getSpecs());

        model.addAttribute("pageTitle",
                "Danh sách bác sĩ theo chuyên khoa");

        model.addAttribute("pageDescription",
                "Các bác sĩ thuộc chuyên khoa");

        model.addAttribute("activePage",
                "specialties");

        return "specialty-detail";
    }

    @PostMapping("/change-doctor-specialty")
    public String changeDoctorSpecialty(
            @RequestParam("doctorId") int doctorId,
            @RequestParam("specialtyId") int specialtyId) {

        Doctors d = this.doctorService.getDoctorById(doctorId);
        Specialties s = this.specialtyService.getSpecialtyById(specialtyId);

        d.setSpecialtyId(s);
        this.doctorService.addOrUpdate(d);

        return "redirect:/admin/specialties/" + specialtyId + "/doctors";
    }
}
