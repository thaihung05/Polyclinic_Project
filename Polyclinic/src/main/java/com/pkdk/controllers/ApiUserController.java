/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Users;
import com.pkdk.service.UserService;
import com.pkdk.utils.JwtUtils;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
        username = username != null ? username.trim() : null;
        password = password != null ? password.trim() : null;
        name = name != null ? name.trim() : null;
        phone = phone != null ? phone.trim() : null;
        confirmPassword = confirmPassword != null ? confirmPassword.trim() : null;
    
    
        if (username == null || username.trim().isEmpty()) {
            return new ResponseEntity<>("Username không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (username.trim().length() < 4) {
            return new ResponseEntity<>("Username phải ít nhất 4 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (username.trim().length() > 20) {
            return new ResponseEntity<>("Username không dài quá 20 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            return new ResponseEntity<>("Mật khẩu không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (password.trim().length() < 6) {
            return new ResponseEntity<>("Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (password.trim().length() > 20) {
            return new ResponseEntity<>("Mật khẩu phải không dài quá 20 ký tự", HttpStatus.BAD_REQUEST);
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return new ResponseEntity<>("Mật khẩu lần 2 không được để trống", HttpStatus.BAD_REQUEST);
        } else if (!password.equals(confirmPassword)) {
            return new ResponseEntity<>("Xác nhận mật khẩu không khớp", HttpStatus.BAD_REQUEST);
        }
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Họ và tên không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (phone == null || phone.trim().isEmpty()) {
            return new ResponseEntity<>("Số điện thoại không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (!phone.trim().matches("^\\d{10}$")) {
            return new ResponseEntity<>("Số điện thoại phải có 10 số", HttpStatus.BAD_REQUEST);
        }

        info.put("username", username.trim());
        info.put("password", password.trim());
        info.put("name", name.trim());
        info.put("phone", phone.trim());
       

        Users checkUser = this.userService.getUserByUserName(username.trim());
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
}
