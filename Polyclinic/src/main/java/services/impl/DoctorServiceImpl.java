/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.impl;

import com.pkdk.pojo.Doctors;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.DoctorRepository;
import services.DoctorService;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class DoctorServiceImpl implements DoctorService{
    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public List<Doctors> getDoctors(int specialtyId) {
        return this.doctorRepository.getDoctors(specialtyId);
    }
}
