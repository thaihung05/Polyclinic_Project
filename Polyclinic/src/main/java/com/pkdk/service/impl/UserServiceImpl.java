/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Specialties;
import com.pkdk.pojo.Users;
import com.pkdk.repository.UserRepository;
import com.pkdk.service.DoctorService;
import com.pkdk.service.PatientService;
import com.pkdk.service.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.pkdk.service.SpecialtyService;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private SpecialtyService specialtyService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Users getUserById(int id) {
        return this.userRepo.getUserById(id);
    }

    @Override
    public List<Users> getUsers(Map<String, String> params) {
        return this.userRepo.getUsers(params);
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

        this.handlePassword(u, rawPassword, oldUser);
        this.handleAvatar(u, oldUser);

        this.userRepo.saveOrUpdate(u);
        this.handleRoleData(u, specialtyId);
    }

    private void handlePassword(Users u, String rawPassword, Users oldUser) {
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            u.setPassword(this.passwordEncoder.encode(rawPassword));
        } else if (oldUser != null) {
            u.setPassword(oldUser.getPassword());
        }
    }

    private void handleAvatar(Users u, Users oldUser) {
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
    }

    private void handleRoleData(Users u, Integer specialtyId) {
        if ("ROLE_DOCTOR".equals(u.getRole())) {
            this.saveDoctorRole(u, specialtyId);
        } else if ("ROLE_PATIENT".equals(u.getRole())) {
            this.savePatientRole(u);
        } else if ("ROLE_ADMIN".equals(u.getRole())) {
            this.doctorService.deleteByUserId(u.getId());
            this.patientService.deleteByUserId(u.getId());
        }
    }

    private void saveDoctorRole(Users u, Integer specialtyId) {
        if (specialtyId == null) {
            throw new RuntimeException("Doctor must have specialty!");
        }

        this.patientService.deleteByUserId(u.getId());

        Doctors d = this.doctorService.getDoctorByUserId(u.getId());
        if (d == null) {
            d = new Doctors();
            d.setUserId(u);
            d.setConsultationFee(BigDecimal.ZERO);
            d.setAvailableOnline(false);
        }

        Specialties s = this.specialtyService.getSpecialtyById(specialtyId);
        d.setSpecialtyId(s);
        this.doctorService.addOrUpdate(d);
    }

    private void savePatientRole(Users u) {
        this.doctorService.deleteByUserId(u.getId());

        Patients p = this.patientService.getPatientByUserId(u.getId());
        if (p == null) {
            p = new Patients();
            p.setUserId(u);
        }

        this.patientService.addOrUpdate(p);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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
    public Users getUserByUserName(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public Users addUser(Map<String, String> info, MultipartFile avatar) {
        Users u = new Users();
        u.setName(info.get("name"));
        u.setIsActive(true);
        u.setPhone(info.get("phone"));
        u.setUsername(info.get("username"));
        u.setRole("ROLE_PATIENT");
        u.setEmail(info.get("email"));
        u.setPassword(this.passwordEncoder.encode(info.get("password")));
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto")
                );
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new RuntimeException("Upload avatar failed!");
            }
        } else {
            u.setAvatar("https://res.cloudinary.com/dx4i4a03w/image/upload/v1767614792/restaurant/avatars/uvp1wsa1gsqmcmpnfcev.jpg");
        }

        this.userRepo.addUser(u);
        this.createNewPatient(u, info);
        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepo.authenticate(username, password);
    }

    @Override
    public Users updateProfile(String username, Map<String, String> info, MultipartFile avatar) {
        Users u = this.userRepo.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        String name = info.get("name");
        String phone = info.get("phone");
        if (name != null && !name.isEmpty()) {
            u.setName(name);
        }
        if (phone != null && !phone.isEmpty()) {
            u.setPhone(phone);
        }
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto")
                );
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new RuntimeException("Upload avatar failed!");
            }
        }

        this.userRepo.updateUser(u);

        if ("ROLE_PATIENT".equals(u.getRole())) {
            Patients p = this.patientService.getPatientByUserId(u.getId());
            if (p == null) {
                p = new Patients();
                p.setUserId(u);
            }

            String dateOfBirthStr = info.get("dateOfBirth");
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    p.setDateOfBirth(sdf.parse(dateOfBirthStr));
                } catch (ParseException e) {
                    throw new RuntimeException("Sai định dạng ngày sinh (dd-MM-yyyy)");
                }
            }

            String gender = info.get("gender");
            if (gender != null && !gender.isEmpty()) {
                p.setGender(gender);
            }

            String address = info.get("address");
            if (address != null) {
                p.setAddress(address);
            }

            this.patientService.addOrUpdate(p);
        }

        return u;

    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Users u = this.userRepo.getUserByUsername(username);
        if (u == null) {
            return false;
        }

        // Kiểm tra mật khẩu cũ
        if (!this.passwordEncoder.matches(oldPassword, u.getPassword())) {
            return false;
        }

        u.setPassword(this.passwordEncoder.encode(newPassword));
        this.userRepo.updateUser(u);
        return true;
    }
    
    
    private void createNewPatient(Users u, Map<String, String> info){
        Patients p = new Patients();
        p.setUserId(u);

        String dateOfBirthStr = info.get("dateOfBirth");
        if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                p.setDateOfBirth(sdf.parse(dateOfBirthStr.trim()));
            } catch (ParseException e) {
                throw new RuntimeException("Sai định dạng ngày sinh (dd-MM-yyyy)");
            }
        }

        String gender = info.get("gender");
        if (gender != null && !gender.isEmpty()) {
            p.setGender(gender);
        }

        String address = info.get("address");
        if (address != null && !address.isEmpty()) {
            p.setAddress(address);
        }

        this.patientService.addOrUpdate(p);
    }
    
}
