package com.pkdk.service;

import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.PrescriptionReservations;
import com.pkdk.pojo.Prescriptions;
import java.util.List;

public interface PrescriptionReservationService {
    void create(Prescriptions prescription, Patients patient, Doctors doctor);
    PrescriptionReservations getByPrescriptionId(int prescriptionId);
    void confirmPaid(int prescriptionId);
    void confirmDispensed(int prescriptionId);
    void confirmExpired(PrescriptionReservations reservation);
    List<PrescriptionReservations> getPaidAndNotDispensed();
    List<PrescriptionReservations> getExpired();
}
