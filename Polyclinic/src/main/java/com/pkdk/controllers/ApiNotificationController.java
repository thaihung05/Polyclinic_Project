/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Notifications;
import com.pkdk.pojo.Users;
import com.pkdk.service.NotificationService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vanlong
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiNotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    
    @GetMapping("/secure/notifications")
    public ResponseEntity<?> getNotification(Principal principal){
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null){
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
        }
        List<Notifications> list = this.notificationService.getByUserId(u.getId());
        return new ResponseEntity<>(list,HttpStatus.OK);
        
    }
    
    
    @GetMapping("/secure/notifications/{id}/read")
    public ResponseEntity<?> markAsRead(Principal principal, @PathVariable(value="id") int id){
        Notifications noti = this.notificationService.getById(id);
        if (noti==null){
            return new ResponseEntity<>("Không tìm thấy thông báo", HttpStatus.BAD_REQUEST);
        }
        this.notificationService.markAsRead(noti);
        return new ResponseEntity<>(noti,HttpStatus.OK);       
    }
    
}