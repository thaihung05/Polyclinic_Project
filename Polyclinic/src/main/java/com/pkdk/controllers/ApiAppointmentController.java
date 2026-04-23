/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.DoctorService;
import com.pkdk.service.GoogleMeetingService;
import com.pkdk.service.PatientService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api/secure/appointments")
public class ApiAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private GoogleMeetingService googleMeetingService;

    @GetMapping("/secure/patient/appointments")
public ResponseEntity<?> getMyAppointments(Principal principal) {
    Users u = userService.getUserByUserName(principal.getName());
    if (u == null)
        return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);

    Patients patient = patientService.getPatientByUserId(u.getId());
    if (patient == null)
        return new ResponseEntity<>("Tài khoản không phải bệnh nhân", HttpStatus.BAD_REQUEST);

    List<Appointments> list = appointmentService.getByPatientId(patient.getId());
    return new ResponseEntity<>(list, HttpStatus.OK);
}
  
  @GetMapping("/secure/doctor/appointments")
public ResponseEntity<?> getDoctorAppointments(Principal principal) {
    Users u = userService.getUserByUserName(principal.getName());
    if (u == null)
        return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);

    Doctors doctor = doctorService.getDoctorByUserId(u.getId());
    if (doctor == null)
        return new ResponseEntity<>("Tài khoản không phải bác sĩ", HttpStatus.BAD_REQUEST);

    List<Appointments> list = appointmentService.getByDoctorId(doctor.getId());
    return new ResponseEntity<>(list, HttpStatus.OK);
}
  
  @GetMapping("/admin/appointments")
public ResponseEntity<?> getAllAppointments(
        @RequestParam(name = "doctorId", required = false) Integer doctorId,
        @RequestParam(name = "patientId", required = false) Integer patientId) {

    if (doctorId != null) {
        List<Appointments> list = appointmentService.getByDoctorId(doctorId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    if (patientId != null) {
        List<Appointments> list = appointmentService.getByPatientId(patientId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    List<Appointments> list = appointmentService.getAll();
    return new ResponseEntity<>(list, HttpStatus.OK);
}
  
  @PostMapping("/secure/appointments")
    public ResponseEntity<?> bookAppointment(
            Principal principal,
            @RequestBody Map<String, Object> body) {
        
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null){
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
        }
        
        Patients p = this.patientService.getPatientByUserId(u.getId());
        if(p==null){
            return new ResponseEntity<>("Không tìm thấy thông tin bệnh nhân", HttpStatus.BAD_REQUEST);
        }
        
        Integer doctorId = (Integer) body.get("doctorId");
        Integer scheduleId = (Integer) body.get("scheduleId");
        String symptoms = (String) body.get("symptoms");
        
        symptoms=symptoms!=null?symptoms.trim():null;
        
        if (doctorId == null)
            return new ResponseEntity<>("Thiếu thông tin doctorId", HttpStatus.BAD_REQUEST);
        if(scheduleId == null)
            return new ResponseEntity<>("Thiếu thông tin scheduleId", HttpStatus.BAD_REQUEST);
 
        try {
            Appointments appt = appointmentService.book(doctorId, scheduleId, p.getId(), symptoms);
            return new ResponseEntity<>(appt, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>("Lỗi tạo lịch hẹn: "+ ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id") int id) {
        Appointments a = this.appointmentService.getById(id);
        if (a == null) {
            return new ResponseEntity<>("Không tìm thấy cuộc hẹn", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(a, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointments appointment) {
        if (appointment.getDoctorId().getId() == null) {
            return new ResponseEntity<>("Vui lòng nhập id của bác sĩ", HttpStatus.BAD_REQUEST);
        }
        if (appointment.getPatientId().getId() == null) {
            return new ResponseEntity<>("Vui lòng nhập id của bệnh nhân", HttpStatus.BAD_REQUEST);
        }

        Doctors d = this.doctorService.getDoctorById(appointment.getDoctorId().getId());
        Patients p = this.patientService.getPatientByUserId(appointment.getPatientId().getId());

        if (d == null) {
            return new ResponseEntity<>("Bác sĩ không tồn tại", HttpStatus.NOT_FOUND);
        }
        if (p == null) {
            return new ResponseEntity<>("Bệnh nhân không tồn tại", HttpStatus.NOT_FOUND);
        }

        appointment.setId(null);
        appointment.setDoctorId(d);
        appointment.setPatientId(p);
        appointment.setStatus("pending");
        appointment.setNgayTao(new Date());
        this.appointmentService.save(appointment);
        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") int id,
            @RequestBody Map<String, String> body) {
        Appointments a = this.appointmentService.getById(id);
        if (a == null) {
            return new ResponseEntity<>("Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND);
        }

        String newStatus = body.get("status");
        if (newStatus == null) {
            return new ResponseEntity<>("Thiếu trạng thái mới", HttpStatus.BAD_REQUEST);
        }

        if (body.containsKey("cancelReason")) {
            a.setCancelReason(body.get("cancelReason"));
            a.setCancelledBy(body.get("cancelledBy"));
        }

        a.setStatus(newStatus);
        this.appointmentService.save(a);
        return new ResponseEntity<>(a, HttpStatus.OK);
    }

    @PatchMapping("/{id}/meeting-url")
    public ResponseEntity<?> updateMeetingUrl(@PathVariable("id") int id,
            @RequestBody Map<String, String> body) {
        Appointments a = this.appointmentService.getById(id);
        if (a == null) {
            return new ResponseEntity<>("Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND);
        }

        String meetLink = googleMeetingService.createMeeting(a);
        if (meetLink == null) {
            return new ResponseEntity<>("Không tạo được link Meeting", HttpStatus.BAD_REQUEST);
        }

        if (a.getMeetingUrl() != null) {
            return new ResponseEntity<>("Đã có meeting", HttpStatus.BAD_REQUEST);
        }

        a.setMeetingUrl(meetLink);
        this.appointmentService.save(a);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}