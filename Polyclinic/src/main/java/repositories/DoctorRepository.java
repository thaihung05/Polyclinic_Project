/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositories;

import com.pkdk.pojo.Doctors;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DoctorRepository {
    List<Doctors> getDoctors(int specialtyId);
}
