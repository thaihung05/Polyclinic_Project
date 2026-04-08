/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import com.pkdk.pojo.Users;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface UserService {
    Users getUserByUsername(String username);
    Users addUser(Map<String, String> info, MultipartFile avatar);
}
