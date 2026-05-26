/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.controllers;

import com.pkdk.enums.AppointmentStatus;
import com.pkdk.enums.UserRole;
import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.pojo.Users;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.DoctorService;
import com.pkdk.service.GoogleMeetingService;
import com.pkdk.service.NotificationService;
import com.pkdk.service.PatientService;
import com.pkdk.service.ScheduleService;
import com.pkdk.service.UserService;
import java.security.Principal;
import java.text.SimpleDateFormat;
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
@RequestMapping("/api")
public class ApiAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private GoogleMeetingService googleMeetingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/secure/patient/appointments")
    public ResponseEntity<?> getMyAppointments(Principal principal) {
        Users u = userService.getUserByUserName(principal.getName());
        if (u == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
        }

        Patients patient = patientService.getPatientByUserId(u.getId());
        if (patient == null) {
            return new ResponseEntity<>("Tài khoản không phải bệnh nhân", HttpStatus.BAD_REQUEST);
        }

        List<Appointments> list = appointmentService.getByPatientId(patient.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/secure/doctor/appointments")
    public ResponseEntity<?> getDoctorAppointments(Principal principal) {
        Users u = userService.getUserByUserName(principal.getName());
        if (u == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
        }

        Doctors doctor = doctorService.getDoctorByUserId(u.getId());
        if (doctor == null) {
            return new ResponseEntity<>("Tài khoản không phải bác sĩ", HttpStatus.BAD_REQUEST);
        }

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

        return new ResponseEntity<>("Cần truyền doctorId hoặc patientid", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/secure/appointments")
    public ResponseEntity<?> bookAppointment(
            Principal principal,
            @RequestBody Map<String, Object> body) {

        Users u = this.userService.getUserByUserName(principal.getName());
        if (u == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST);
        }

        Patients p = this.patientService.getPatientByUserId(u.getId());
        if (p == null) {
            return new ResponseEntity<>("Không tìm thấy thông tin bệnh nhân", HttpStatus.BAD_REQUEST);
        }

        Integer doctorId = (Integer) body.get("doctorId");
        Integer scheduleId = (Integer) body.get("scheduleId");
        String symptoms = (String) body.get("symptoms");
        
        symptoms = symptoms != null ? symptoms.trim() : null;

        if (doctorId == null) {
            return new ResponseEntity<>("Thiếu thông tin doctorId", HttpStatus.BAD_REQUEST);
        }
        if (scheduleId == null) {
            return new ResponseEntity<>("Thiếu thông tin scheduleId", HttpStatus.BAD_REQUEST);
        }
        

        try {
            Appointments appt = this.appointmentService.book(doctorId, scheduleId, p.getId(), symptoms);
            return new ResponseEntity<>(appt, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>("Lỗi tạo lịch hẹn: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
    
    @PostMapping("/secure/doctor/appointments/follow-up")
    public ResponseEntity<?> bookAppointmentForPatient(@RequestBody Map<String, Object> body, 
            Principal principal){
        
        Users u = this.userService.getUserByUserName(principal.getName());
        if (u==null)
            return new ResponseEntity<>("Không tìm thấy thông tin người dùng!",HttpStatus.UNAUTHORIZED);
        
        if (!UserRole.ROLE_DOCTOR.name().equals(u.getRole()))
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền tạo lịch tái khám!",HttpStatus.FORBIDDEN);
        
        Doctors doctor = this.doctorService.getDoctorByUserId(u.getId());
        if (doctor==null)
            return new ResponseEntity<>("Không tìm thấy thông tin bác sĩ!",HttpStatus.BAD_REQUEST);
        
        Integer patientId = (Integer) body.get("patientId");
        Integer scheduleId = (Integer) body.get("scheduleId");
        String symptoms = (String) body.get("symptoms");
        
        if (patientId == null)
            return new ResponseEntity<>("Thiếu thông tin patientId", HttpStatus.BAD_REQUEST);
        if (scheduleId == null)
            return new ResponseEntity<>("Thiếu thông tin scheduleId", HttpStatus.BAD_REQUEST);

        try {
            Appointments appt = this.appointmentService.bookFollowUp(doctor.getId(), scheduleId, patientId, symptoms);
            return new ResponseEntity<>(appt, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>("Lỗi tạo tái khám: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
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

    @PatchMapping("/secure/appointments/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") int id,
            @RequestBody Map<String, String> body, Principal principal) {

        Users u = this.userService.getUserByUserName(principal.getName());
        if (u == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        }

        Appointments a = this.appointmentService.getById(id);
        if (a == null) {
            return new ResponseEntity<>("Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND);
        }

        String newStatus = body.get("status");
        if (newStatus == null) {
            return new ResponseEntity<>("Thiếu trạng thái mới", HttpStatus.BAD_REQUEST);
        }

        String role = u.getRole();
        if (UserRole.ROLE_DOCTOR.name().equals(role)) {
            Doctors d = this.doctorService.getDoctorByUserId(u.getId());
            if (d == null || !a.getDoctorId().getId().equals(d.getId())) {
                return new ResponseEntity<>("Bác sĩ chỉ cập nhật trạng thái lịch hẹn của mình", HttpStatus.FORBIDDEN);
            }
            if (!newStatus.equals("COMPLETED") && !newStatus.equals("NO_SHOW")) {
                return new ResponseEntity<>("Bác sĩ không có quyền đặt trạng thái: " + newStatus, HttpStatus.FORBIDDEN);
            }
            if (!"CONFIRMED".equals(a.getStatus())){
                return new ResponseEntity<>("Chỉ được cập nhật lịch hẹn đã xác nhận", HttpStatus.BAD_REQUEST);
            }
        } else if (UserRole.ROLE_PATIENT.name().equals(role)) {
            Patients p = this.patientService.getPatientByUserId(u.getId());
            if (p == null || !a.getPatientId().getId().equals(p.getId())) {
                return new ResponseEntity<>("Bệnh nhân chỉ cập nhật trạng thái lịch hẹn của mình", HttpStatus.FORBIDDEN);
            }
            if ("CANCELLED".equals(newStatus)) {
                a.setCancelReason(body.get("cancelReason"));
                a.setCancelledBy(u.getName());
                Date now = new Date();
                if (a.getScheduledAt() != null && a.getScheduledAt().after(now)) {
                    DoctorSchedules slot = this.scheduleService.getByDoctorAndStartTime(
                            a.getDoctorId().getId(), a.getScheduledAt());
                    if (slot != null && !slot.getIsActive()) {
                        this.scheduleService.reactivate(slot);
                    }
                }
            } else {
                return new ResponseEntity<>("Bệnh nhân chỉ được quyền hủy lịch hẹn của mình", HttpStatus.FORBIDDEN);
            }

        } else if (!UserRole.ROLE_ADMIN.name().equals(role)) {
            return new ResponseEntity<>("Không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN);
        }

        if (body.containsKey("cancelReason")) {
            a.setCancelReason(body.get("cancelReason"));
            a.setCancelledBy(body.get("cancelledBy"));
        }

        a.setStatus(newStatus);

        this.appointmentService.save(a);
        if(newStatus.equals(AppointmentStatus.CANCELLED.name())){
            String scheduledAt = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(a.getScheduledAt());
            String patientName = a.getPatientId().getUserId().getName();
            String cancelledBy = u.getName();
            
            this.notificationService.createCancelNotificationForPatient(
                    a.getPatientId().getUserId(), scheduledAt, cancelledBy);
            
            this.notificationService.createCancelNotificationForDoctor(
                    a.getDoctorId().getUserId(), patientName, scheduledAt);
        }
        return new ResponseEntity<>(a, HttpStatus.OK);
    }

    @PatchMapping("/secure/appointments/{id}/meeting-url")
    public ResponseEntity<?> updateMeetingUrl(@PathVariable("id") int id,
            @RequestBody Map<String, String> body, Principal principal) {

        Users u = this.userService.getUserByUserName(principal.getName());
        if (u == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED);
        }

        if (!UserRole.ROLE_DOCTOR.name().equals(u.getRole())) {
            return new ResponseEntity<>("Chỉ bác sĩ mới có quyền tạo link họp", HttpStatus.FORBIDDEN);
        }

        Doctors d = this.doctorService.getDoctorByUserId(u.getId());
        if (d == null) {
            return new ResponseEntity<>("Không tìm thấy thông tin bác sĩ", HttpStatus.BAD_REQUEST);
        }

        Appointments a = this.appointmentService.getById(id);
        if (a == null) {
            return new ResponseEntity<>("Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND);
        }

        if (!a.getDoctorId().getId().equals(d.getId())) {
            return new ResponseEntity<>("Bác sĩ chỉ được tạo link họp cho lịch hẹn của mình", HttpStatus.FORBIDDEN);
        }

        if (a.getMeetingUrl() != null) {
            return new ResponseEntity<>("Đã có meeting", HttpStatus.BAD_REQUEST);
        }

        String meetLink = googleMeetingService.createMeeting(a);
        if (meetLink == null) {
            return new ResponseEntity<>("Không tạo được link Meeting", HttpStatus.BAD_REQUEST);
        }

        a.setMeetingUrl(meetLink);
        this.appointmentService.save(a);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
