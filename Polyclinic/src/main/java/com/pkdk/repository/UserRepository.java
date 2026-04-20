/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.Users;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface UserRepository {
    List<Users> getUsers(Map<String, String> params);
    Users getUserById(int id);
    Users getUserByUsername(String username);
    void saveOrUpdate(Users u);
    void deleteUser(int id);
    Users addUser(Users u);
    boolean authenticate(String username, String password);
}
