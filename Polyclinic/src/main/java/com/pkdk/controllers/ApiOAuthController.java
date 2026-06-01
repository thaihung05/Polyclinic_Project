/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Users;
import com.pkdk.service.PatientService;
import com.pkdk.service.UserService;
import com.pkdk.utils.JwtUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class ApiOAuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body){
        String credential = body.get("credential");
        if (credential == null || credential.isEmpty()){
            return new ResponseEntity<>("Thiếu credential", HttpStatus.BAD_REQUEST);
        }
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + credential))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return new ResponseEntity<>("Token Google không hợp lệ", HttpStatus.UNAUTHORIZED);
            }
            
            ObjectMapper mapper =  new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            String email = node.has("email") ? node.get("email").asText() : null;
            String name = node.has("name") ? node.get("name").asText() : "Google User";
            String picture = node.has("picture") ? node.get("picture").asText() : null;
            
            if (email == null) {
                return new ResponseEntity<>("Không lấy được email từ Google", HttpStatus.BAD_REQUEST);
            }
            
            Users u = this.userService.getUserByEmail(email);
            if (u == null){
                u = this.createOAuthUser(email, name, picture);
            }
            
            String token = JwtUtils.generateToken(u.getUsername());
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception ex) {
            return new ResponseEntity<>("Lỗi đăng nhập Google: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        
    }
    
    private Users createOAuthUser(String email, String name, String picture) {
        Users u = new Users();
        String base = email.split("@")[0];
        u.setUsername(base + "_" + UUID.randomUUID().toString().substring(0, 6));
        u.setName(name);
        u.setEmail(email);
        u.setAvatar(picture != null ? picture :
            "https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg");
        u.setRole("ROLE_PATIENT");
        u.setPassword(this.passwordEncoder.encode(UUID.randomUUID().toString()));
        u.setPhone("0000000000");
        u.setIsActive(true);
        this.userService.saveOrUpdate(u);
        Patients p = new Patients();
        p.setUserId(u);
        this.patientService.addOrUpdate(p);
        return u;
    }
}
