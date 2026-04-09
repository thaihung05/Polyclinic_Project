/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repositories;

import com.pkdk.pojo.Users;

/**
 *
 * @author Admin
 */
public interface UserRepository {
    Users getUserByUsername(String username);
    Users addUser(Users u);
}
