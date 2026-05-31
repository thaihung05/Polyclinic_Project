/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.PrescriptionReservations;
import com.pkdk.pojo.Prescriptions;
import com.pkdk.repository.PrescriptionReservationRepository;
import com.pkdk.service.PrescriptionReservationService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PrescriptionReservationServiceImpl implements PrescriptionReservationService{

    private static final int RESERVATION_MINUTES = 1;
    
    @Autowired
    private PrescriptionReservationRepository prescriptionReservationRepo;
    
    @Override
    public void create(Prescriptions prescription, Patients patient, Doctors doctor) {
        PrescriptionReservations r = new PrescriptionReservations();
        r.setDoctorId(doctor);
        r.setPatientId(patient);
        r.setPrescriptionId(prescription);
        r.setReservedAt(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, RESERVATION_MINUTES);
        r.setExpiresAt(cal.getTime());
        this.prescriptionReservationRepo.save(r);
    }

    @Override
    public PrescriptionReservations getByPrescriptionId(int prescriptionId) {
        return this.prescriptionReservationRepo.getByPrescriptionId(prescriptionId);
    }

    @Override
    public void confirmPaid(int prescriptionId) {
        PrescriptionReservations p = this.prescriptionReservationRepo.getByPrescriptionId(prescriptionId);
        if (p == null){
            throw new RuntimeException("Không tìm thấy reservation cho đơn thuốc!!!");
        }
        p.setIsPaid(true);
        this.prescriptionReservationRepo.save(p);
    }

    @Override
    public void confirmDispensed(int prescriptionId) {
        PrescriptionReservations p = this.prescriptionReservationRepo.getByPrescriptionId(prescriptionId);
        if (p == null){
            throw new RuntimeException("Không tìm thấy reservation cho đơn thuốc!!!");
        }
        p.setIsDispensed(true);
        this.prescriptionReservationRepo.save(p);
    }

    @Override
    public void confirmExpired(PrescriptionReservations reservation) {
        reservation.setIsExpired(true);
        this.prescriptionReservationRepo.save(reservation);
    }

    @Override
    public List<PrescriptionReservations> getPaidAndNotDispensed() {
        return this.prescriptionReservationRepo.findPaidAndNotDispensed();
    }

    @Override
    public List<PrescriptionReservations> getExpired() {
        return this.prescriptionReservationRepo.findExpired();
    }
    
}
