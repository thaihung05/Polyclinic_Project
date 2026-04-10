/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.impl;

import com.pkdk.pojo.Specialties;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.SpecialtiesRepository;
import services.SpecialtiesService;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class SpecialtiesServiceImpl implements SpecialtiesService{
    
    @Autowired
    private SpecialtiesRepository SpecRepo;

    @Override
    public List<Specialties> getSpecs() {
        return this.SpecRepo.getSpecs();
    }
    
}
