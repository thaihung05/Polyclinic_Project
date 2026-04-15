/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Specialties;
import com.pkdk.pojo.Users;
import com.pkdk.repositories.UserRepository;
import com.pkdk.services.DoctorService;
import com.pkdk.services.PatientService;
import com.pkdk.services.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pkdk.services.SpecialtyService;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private SpecialtyService specialtiesService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Users getUserById(int id) {
        return this.userRepo.getUserById(id);
    }

    @Override
    public void deleteUser(int id) {
        this.doctorService.deleteByUserId(id);
        this.patientService.deleteByUserId(id);
        this.userRepo.deleteUser(id);
    }

    @Override
    public void addOrUpdate(Users u, String rawPassword, Integer specialtyId) {
        Users oldUser = null;
        if (u.getId() != null) {
            oldUser = this.userRepo.getUserById(u.getId());
        }
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            u.setPassword(this.passwordEncoder.encode(rawPassword));
        } else if (oldUser != null) {
            u.setPassword(oldUser.getPassword());
        }
        if (u.getFile() != null && !u.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        u.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto")
                );
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new RuntimeException("Upload avatar failed!");
            }
        } else if (oldUser != null) {
            u.setAvatar(oldUser.getAvatar());
        }
        this.userRepo.saveOrUpdate(u);
        if ("ROLE_DOCTOR".equals(u.getRole())) {
            if (specialtyId == null) {
                throw new RuntimeException("Doctor must have specialty!");
            }
            this.patientService.deleteByUserId(u.getId());
            Doctors d = this.doctorService.getDoctorByUserId(u.getId());
            if (d == null) {
                d = new Doctors();
                d.setUserId(u);
                d.setConsultationFee(java.math.BigDecimal.ZERO);
                d.setAvailableOnline(false);
            }
            Specialties specialty = this.specialtiesService.getSpecialtyById(specialtyId);
            d.setSpecialtyId(specialty);
            this.doctorService.addOrUpdate(d);
        } else if ("ROLE_PATIENT".equals(u.getRole())) {
            this.doctorService.deleteByUserId(u.getId());
            Patients p = this.patientService.getPatientByUserId(u.getId());
            if (p == null) {
                p = new Patients();
                p.setUserId(u);
            }
            this.patientService.addOrUpdate(p);

        } else if ("ROLE_ADMIN".equals(u.getRole())) {
            this.doctorService.deleteByUserId(u.getId());
            this.patientService.deleteByUserId(u.getId());
        }
    }

    @Override
    public org.springframework.security.core.userdetails.User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Users u = this.userRepo.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username!");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                authorities
        );
    }

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        return this.userRepo.getUsers(params);
    }

}
