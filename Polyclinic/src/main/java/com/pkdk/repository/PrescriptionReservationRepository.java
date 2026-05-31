/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pkdk.repository;

import com.pkdk.pojo.PrescriptionReservations;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface PrescriptionReservationRepository {
    void save(PrescriptionReservations r);
    PrescriptionReservations getByPrescriptionId(int id);
    List<PrescriptionReservations> findExpired();
    List<PrescriptionReservations> findPaidAndNotDispensed();
}
