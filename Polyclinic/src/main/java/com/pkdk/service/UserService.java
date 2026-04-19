/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Users;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 *
 * @author Admin
 */
public interface UserService extends UserDetailsService {
    List<Users> getUsers(Map<String, String> params);
    Users getUserById(int id);
    void deleteUser(int id);
    void addOrUpdate(Users u, String rawPassword, Integer specialtyId);
}
