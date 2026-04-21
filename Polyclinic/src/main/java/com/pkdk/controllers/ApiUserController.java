/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Users;
import com.pkdk.service.UserService;
import com.pkdk.utils.JwtUtils;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
public class ApiUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestParam Map<String, String> info,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        String username = info.get("username");
        String password = info.get("password");
        String name = info.get("name");
        String phone = info.get("phone");
        String confirmPassword = info.get("confirmPassword");

        String dateOfBirth = info.get("dateOfBirth");
        String gender = info.get("gender");
        String address = info.get("address");

        username = username != null ? username.trim() : null;
        password = password != null ? password.trim() : null;
        name = name != null ? name.trim() : null;
        phone = phone != null ? phone.trim() : null;
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

        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                sdf.setLenient(false);
                Date dob = sdf.parse(dateOfBirth);
                if (dob.after(new Date())) {
                    return new ResponseEntity<>("Ngày sinh không hợp lệ", HttpStatus.BAD_REQUEST);
                }
            } catch (ParseException e) {
                return new ResponseEntity<>("Định dạng ngày sinh không hợp lệ (dd-MM-yyyy)", HttpStatus.BAD_REQUEST);
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
    @CrossOrigin
    public ResponseEntity<Users> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByUserName(principal.getName()), HttpStatus.OK);
    }

    @PutMapping("/secure/profile")
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @RequestParam Map<String, String> info,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        String name = info.get("name");
        String phone = info.get("phone");
        name = name != null ? name.trim() : null;
        phone = phone != null ? phone.trim() : null;

        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^\\d{10}$")) {
                return ResponseEntity.badRequest().body("Số điện thoại phải có 10 số");
            }
            info.put("phone", phone);
        }

        if (name != null) {
            if (name.isEmpty()) {
                return ResponseEntity.badRequest().body("Họ và tên không được để trống");
            }
            info.put("name", name);
        }

        try {
            Users updated = this.userService.updateProfile(principal.getName(), info, avatar);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }

    @PutMapping("/secure/profile/change-password")
    public ResponseEntity<?> changePassword(
            Principal principal,
            @RequestBody Map<String, String> body) {

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        String confirmNewPassword = body.get("confirmNewPassword");

        // Validate
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không được để trống");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không được để trống");
        }
        if (newPassword.trim().length() < 6) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        if (newPassword.trim().length() > 20) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không dài quá 20 ký tự");
        }
        if (confirmNewPassword == null || confirmNewPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Xác nhận mật khẩu mới không được để trống");
        }
        if (!newPassword.trim().equals(confirmNewPassword.trim())) {
            return ResponseEntity.badRequest().body("Xác nhận mật khẩu mới không khớp");
        }
        if (oldPassword.trim().equals(newPassword.trim())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải khác mật khẩu cũ");
        }

        boolean success = this.userService.changePassword(principal.getName(), oldPassword.trim(), newPassword.trim());

        if (!success) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu cũ không đúng");
        }

        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}
