/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service;

import com.pkdk.pojo.LabResults;
import com.pkdk.pojo.MedicalRecords;
import java.util.List;

/**
 *
 * @author vanlong
 */
public interface MedicalHistoryService {
    List<MedicalRecords> getMedicalRecordsByPatientId(int patientId);
    List<LabResults> getLabResultsByPatientId(int patientId);
}
