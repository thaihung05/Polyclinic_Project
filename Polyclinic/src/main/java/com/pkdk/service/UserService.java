/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.Users;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface UserService extends UserDetailsService {
    List<Users> getUsers(Map<String, String> params);
    long countUsers(String kw);
    Users getUserById(int id);
    void deleteUser(int id);
    void addOrUpdate(Users u, String rawPassword, Integer specialtyId);
    Users addUser(Map<String, String> info, MultipartFile avatar);
    Users getUserByUserName(String username);
    Users getUserByEmail(String email);
    boolean authenticate(String username, String password);
    Users updateProfile(String username, Map<String, String> info, MultipartFile avatar);
    boolean changePassword(String username, String oldPassword, String newPassword);
    void saveOrUpdate(Users u);
    List<Users> getUsersByRole(String role);
}
