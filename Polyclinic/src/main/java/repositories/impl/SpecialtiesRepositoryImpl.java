/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositories.impl;

import com.pkdk.pojo.Specialties;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import repositories.SpecialtiesRepository;

/**
 *
 * @author Admin
 */
@Repository
public class SpecialtiesRepositoryImpl implements SpecialtiesRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<Specialties> getSpecs(){
        Session session = this.factory.getObject().getCurrentSession();
        Query query = session.createQuery("From Specialties", Specialties.class);
        return query.getResultList();
    }
}
