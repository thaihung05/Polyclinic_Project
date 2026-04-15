/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Users;
import com.pkdk.services.DoctorService;
import com.pkdk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pkdk.services.SpecialtyService;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping
    public String listUser(Model model, @RequestParam Map<String, String> params) {
        List<Users> users = this.userService.getUsers(params);
        int pageSize = this.env.getProperty("PAGE_SIZE", Integer.class);
        int currentPage = Integer.parseInt(params.getOrDefault("page", "1"));
        int totalPages = (int) Math.ceil(
                (double) this.userService.getUsers(null).size() / pageSize
        );
        model.addAttribute("users", users);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageTitle", "Quản lý người dùng");
        model.addAttribute("pageDescription", "Danh sách tài khoản trong hệ thống!");
        model.addAttribute("activePage", "users");
        return "users";
    }

    @GetMapping("/add")
    public String addView(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("specialties", this.specialtyService.getSpecs());
        model.addAttribute("pageTitle", "Thêm người dùng");
        model.addAttribute("pageDescription", "Tạo mới tài khoản hệ thống");
        model.addAttribute("activePage", "users");
        return "user-form";
    }

    @PostMapping
    public String addUser(@ModelAttribute("user") Users u,
            @RequestParam(name = "rawPassword", required = false) String rawPassword,
            @RequestParam(name = "specialtyId", required = false) Integer specialtyId) {
        this.userService.addOrUpdate(u, rawPassword, specialtyId);
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}")
    public String updateView(Model model, @PathVariable("id") int id) {
        Users u = this.userService.getUserById(id);

        model.addAttribute("user", u);
        model.addAttribute("specialties", this.specialtyService.getSpecs());
        model.addAttribute("pageTitle", "Cập nhật người dùng");
        model.addAttribute("pageDescription", "Chỉnh sửa thông tin tài khoản");
        model.addAttribute("activePage", "users");

        if ("ROLE_DOCTOR".equals(u.getRole())) {
            Doctors d = this.doctorService.getDoctorByUserId(id);
            if (d != null && d.getSpecialtyId() != null) {
                model.addAttribute("specialtyId", d.getSpecialtyId().getId());
            }
        }
        return "user-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        this.userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
