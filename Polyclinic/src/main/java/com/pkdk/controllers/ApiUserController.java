/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.enums.UserRole;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Users;
import com.pkdk.service.DoctorService;
import com.pkdk.service.PatientService;
import com.pkdk.service.UserService;
import com.pkdk.utils.JwtUtils;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author vanlong
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam Map<String, String> info,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        String username = info.get("username");
        String password = info.get("password");
        String name = info.get("name");
        String phone = info.get("phone");
        String email = info.get("email");
        String confirmPassword = info.get("confirmPassword");

        String dateOfBirth = info.get("dateOfBirth");
        String gender = info.get("gender");
        String address = info.get("address");

        username = username != null ? username.trim() : null;
        password = password != null ? password.trim() : null;
        name = name != null ? name.trim() : null;
        phone = phone != null ? phone.trim() : null;
        email = email != null ? email.trim() : null;
        confirmPassword = confirmPassword != null ? confirmPassword.trim() : null;
        dateOfBirth = dateOfBirth != null ? dateOfBirth.trim() : null;
        gender = gender != null ? gender.trim() : null;
        address = address != null ? address.trim() : null;

        if (username == null || username.isEmpty()) {
            return new ResponseEntity<>("Username không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (username.length() < 4) {
            return new ResponseEntity<>("Username phải ít nhất 4 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (username.length() > 20) {
            return new ResponseEntity<>("Username không dài quá 20 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.isEmpty()) {
            return new ResponseEntity<>("Mật khẩu không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (password.length() < 6) {
            return new ResponseEntity<>("Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (password.length() > 20) {
            return new ResponseEntity<>("Mật khẩu phải không dài quá 20 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return new ResponseEntity<>("Mật khẩu lần 2 không được để trống", HttpStatus.BAD_REQUEST);
        } else if (!password.equals(confirmPassword)) {
            return new ResponseEntity<>("Xác nhận mật khẩu không khớp", HttpStatus.BAD_REQUEST);
        }
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>("Họ và tên không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (phone == null || phone.isEmpty()) {
            return new ResponseEntity<>("Số điện thoại không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (!phone.matches("^\\d{10}$")) {
            return new ResponseEntity<>("Số điện thoại phải có 10 số", HttpStatus.BAD_REQUEST);
        }

        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>("Email không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return new ResponseEntity<>("Email không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                sdf.setLenient(false);
                Date dob = sdf.parse(dateOfBirth);
                if (dob.after(new Date())) {
                    return new ResponseEntity<>("Ngày sinh không hợp lệ", HttpStatus.BAD_REQUEST);
                }
            } catch (ParseException e) {
                return new ResponseEntity<>("Lỗi: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        if (gender != null && !gender.isEmpty()) {
            if (!gender.trim().equals("MALE") && !gender.trim().equals("FEMALE") && !gender.trim().equals("OTHER")) {
                return new ResponseEntity<>("Giới tính không hợp lệ (MALE | FEMALE | OTHER)", HttpStatus.BAD_REQUEST);
            }
        }

        if (address != null && address.length() > 500) {
            return new ResponseEntity<>("Địa chỉ không được vượt quá 500 ký tự", HttpStatus.BAD_REQUEST);
        }

        info.put("username", username);
        info.put("password", password);
        info.put("name", name);
        info.put("phone", phone);
        info.put("email", email);
        info.put("dateOfBirth", dateOfBirth);
        info.put("gender", gender);
        info.put("address", address);

        Users checkUser = this.userService.getUserByUserName(username);
        if (checkUser != null) {
            return new ResponseEntity<>("Username đã tồn tại", HttpStatus.CONFLICT);
        }

        Users u = this.userService.addUser(info, avatar);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users u) {

        if (this.userService.authenticate(u.getUsername(), u.getPassword())) {
            try {
                String token = JwtUtils.generateToken(u.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<?> getProfile(Principal principal) {
        Users u = this.userService.getUserByUserName(principal.getName());

        Map<String, Object> info = new HashMap<>();
        info.put("id", u.getId());
        info.put("username", u.getUsername());
        info.put("name", u.getName());
        info.put("email", u.getEmail());
        info.put("phone", u.getPhone());
        info.put("avatar", u.getAvatar());
        info.put("role", u.getRole());
        info.put("isActive", u.getIsActive());

        if (UserRole.ROLE_PATIENT.name().equals(u.getRole())) {
            Patients p = this.patientService.getPatientByUserId(u.getId());
            if (p != null) {
                info.put("gender", p.getGender());
                info.put("address", p.getAddress());
                info.put("dateOfBirth", p.getDateOfBirth());
            } else {
                info.put("gender", null);
                info.put("address", null);
                info.put("dateOfBirth", null);
            }
        } else if (UserRole.ROLE_DOCTOR.name().equals(u.getRole())) {
            Doctors d = this.doctorService.getDoctorByUserId(u.getId());
            if (d != null) {
                info.put("gender", d.getGender());
                info.put("address", d.getAddress());
                info.put("dateOfBirth", d.getDateOfBirth());
                info.put("specialty", d.getSpecialtyId() != null ? d.getSpecialtyId().getName() : null);
            } else {
                info.put("gender", null);
                info.put("address", null);
                info.put("dateOfBirth", null);
                info.put("specialty", null);
            }
        }

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PatchMapping("/secure/profile")
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @RequestParam Map<String, String> info,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        String name = info.get("name");
        String phone = info.get("phone");
        String email = info.get("email");
        name = name != null ? name.trim() : null;
        phone = phone != null ? phone.trim() : null;
        email = email != null ? email.trim() : null;

        if (phone != null && !phone.isEmpty()) {
            if (!phone.matches("^\\d{10}$")) {
                return new ResponseEntity<>("Số điện thoại phải có 10 số", HttpStatus.BAD_REQUEST);
            }
            info.put("phone", phone);
        }

        if (name != null) {
            if (name.isEmpty()) {
                return new ResponseEntity<>("Họ và tên không được để trống", HttpStatus.BAD_REQUEST);
            }
            info.put("name", name);
        }

        if (email != null) {
            if (email.isEmpty()){
                return new ResponseEntity<>("Email không được để trống", HttpStatus.BAD_REQUEST);
            }
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                return new ResponseEntity<>("Email không hợp lệ", HttpStatus.BAD_REQUEST);
            }
            info.put("email", email);
        }

        try {
            Users updated = this.userService.updateProfile(principal.getName(), info, avatar);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>("Lỗi: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PatchMapping("/secure/profile/change-password")
    public ResponseEntity<?> changePassword(
            Principal principal,
            @RequestBody Map<String, String> body) {

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        String confirmNewPassword = body.get("confirmNewPassword");

        oldPassword = oldPassword != null ? oldPassword.trim() : null;
        newPassword = newPassword != null ? newPassword.trim() : null;
        confirmNewPassword = confirmNewPassword != null ? confirmNewPassword.trim() : null;

        if (oldPassword == null || oldPassword.isEmpty()) {
            return new ResponseEntity<>("Mật khẩu cũ không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return new ResponseEntity<>("Mật khẩu mới không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (newPassword.length() < 6) {
            return new ResponseEntity<>("Mật khẩu mới phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (newPassword.length() > 20) {
            return new ResponseEntity<>("Mật khẩu mới không dài quá 20 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (confirmNewPassword == null || confirmNewPassword.trim().isEmpty()) {
            return new ResponseEntity<>("Mật khẩu xác nhận không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (!newPassword.equals(confirmNewPassword)) {
            return new ResponseEntity<>("Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST);
        }
        if (oldPassword.equals(newPassword.trim())) {
            return new ResponseEntity<>("Mật khẩu mới phải khác mật khẩu cũ", HttpStatus.BAD_REQUEST);
        }

        boolean success = this.userService.changePassword(principal.getName(), oldPassword, newPassword);

        if (!success) {
            return new ResponseEntity<>("Mật khẩu cũ không đúng", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("Đổi mật khẩu thành công", HttpStatus.OK);
    }

    private void updatePatientProfile() {

    }

    private void updateDoctorProfile() {

    }
}
