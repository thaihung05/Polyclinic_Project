/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.LabResults;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface LabResultService {
    List<LabResults> getByAppointmentId(int appointmentId);
    LabResults getById(int id);
    void save(LabResults labResult);
}
