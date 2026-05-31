/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.jobs;

import com.pkdk.pojo.PrescriptionItems;
import com.pkdk.pojo.PrescriptionReservations;
import com.pkdk.service.MedicineService;
import com.pkdk.service.NotificationService;
import com.pkdk.service.PrescriptionReservationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Component
public class ReservationExpiryJob {

    @Autowired
    private PrescriptionReservationService prescriptionReservationService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void releaseExpiredReservations(){
        List<PrescriptionReservations> expiredList = this.prescriptionReservationService.getExpired();
        for (PrescriptionReservations r : expiredList){
            for (PrescriptionItems item : r.getPrescriptionId().getPrescriptionItemsCollection()) {
                this.medicineService.restoreStock(item.getMedicineId().getId(),item.getQuantity());
            }
            this.prescriptionReservationService.confirmExpired(r);
            this.notificationService.createPrescriptionExpiredNotification(
                r.getPatientId().getUserId(),
                r.getExpiresAt()
            );
        }
    }
    
}
