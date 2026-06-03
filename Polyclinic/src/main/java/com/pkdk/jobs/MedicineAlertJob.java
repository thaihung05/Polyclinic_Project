/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.jobs;

import com.pkdk.enums.UserRole;
import com.pkdk.pojo.Medicines;
import com.pkdk.pojo.Users;
import com.pkdk.service.MedicineService;
import com.pkdk.service.NotificationService;
import com.pkdk.service.UserService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vanlong
 */
@Component
public class MedicineAlertJob {
    private static final int LOW_STOCK_THRESHOLD=10;
    private static final int NEAR_EXPIRY_DAYS=30;
    
    @Autowired
    private MedicineService medicineService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    
    
    private final Set<String> sentAlert= new HashSet<>();
    private String lastRunDate = "";
    
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void checkMedicineAlerts(){
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        if (!today.equals(lastRunDate)){
            sentAlert.clear();
            lastRunDate=today;
        }
        
        List<Users> pharmacists = this.userService.getUsersByRole(UserRole.ROLE_PHARMACIST.name());
        
        if(pharmacists.isEmpty()){
            return;
        }
        
        for(Medicines m : this.medicineService.getLowStock(LOW_STOCK_THRESHOLD)){
            String key = "stock_"+m.getId()+"_"+today;
            if(!sentAlert.contains(key)){
                for (Users pharmacist: pharmacists){
                    this.notificationService.createMedicineLowStockNotification(
                            pharmacist, m.getName(), m.getStockQuantity()
                    );
                }
                sentAlert.add(key);
            }
        }
        
        for (Medicines m : this.medicineService.getNearExiry(NEAR_EXPIRY_DAYS)){
            String key = "expiry_"+m.getId()+"_"+today;
            if (!sentAlert.contains(key)){
                for(Users pharmacist: pharmacists){
                    this.notificationService.createMedicineNearExpiryNotification(
                            pharmacist, m.getName(), m.getExpiryDate()
                    );
                }
                sentAlert.add(key);
            }
        }
    }
}
