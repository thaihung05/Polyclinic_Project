/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositories.impl;

import com.pkdk.pojo.Doctors;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import repositories.DoctorRepository;

/**
 *
 * @author Admin
 */
@Repository
public class DoctorRepositoryImpl implements DoctorRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Doctors> getDoctors(int specialtyId) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT d FROM Doctors d WHERE d.specialtyId.id = :specialtyId";

        return session.createQuery(hql, Doctors.class)
                .setParameter("specialtyId", specialtyId)
                .getResultList();
    }
    
}
