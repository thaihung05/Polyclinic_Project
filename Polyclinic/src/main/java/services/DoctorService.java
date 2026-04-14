/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import com.pkdk.pojo.Doctors;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DoctorService {
    List<Doctors> getDoctors(int specialtyId);
}
