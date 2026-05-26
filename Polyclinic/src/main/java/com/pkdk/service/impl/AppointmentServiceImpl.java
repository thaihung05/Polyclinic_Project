/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.pkdk.enums.AppointmentStatus;
import com.pkdk.pojo.Appointments;
import com.pkdk.pojo.DoctorSchedules;
import com.pkdk.pojo.Doctors;
import com.pkdk.pojo.Patients;
import com.pkdk.repository.AppointmentRepository;
import com.pkdk.service.AppointmentService;
import com.pkdk.service.DoctorService;
import com.pkdk.service.NotificationService;
import com.pkdk.service.PatientService;
import com.pkdk.service.ScheduleService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vanlong
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private NotificationService notificationService;

    
    @Override
    public Appointments book(int doctorId, int scheduleId, int patientId, String symptoms) {
        Doctors doctor = this.doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("Không tìm thấy bác sĩ");
        }

        DoctorSchedules schedule = this.scheduleService.getById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("Lịch không hợp lệ");
        }
        if (!schedule.getIsActive()) {
            throw new RuntimeException("Lịch đã bị vô hiệu hóa");
        }

        if (!schedule.getDoctorId().getId().equals(doctorId)) {
            throw new RuntimeException("Lịch không thuộc về bác sĩ này");
        }

        //Bị lỗi vì new Date() lấy giờ hệ thống server, nếu server chạy timezone UTC thì 2026-04-25 08:00:00 có thể bị hiểu là UTC, trong khi thực tế giờ VN là UTC+7.
//        if (schedule.getStartTime().before(new Date())) {
//            throw new RuntimeException("Lịch đã qua, Vui lòng chọn lịch khác");
//        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Date nowVN = cal.getTime();

        if (schedule.getStartTime().before(nowVN)) {
            throw new RuntimeException("Lịch đã qua, Vui lòng chọn lịch khác");
        }

        Patients patient = this.patientService.getPatientById(patientId);
        if (patient == null) {
            throw new RuntimeException("Không tìm thấy bệnh nhân");
        }
        
        if (this.existsByPatientAndTime(patientId, schedule.getStartTime()))
            throw new RuntimeException("Bạn đã có lịch hẹn vào khung giờ này rồi!");

        Appointments appointment = new Appointments();
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setScheduledAt(schedule.getStartTime());
        appointment.setSymptoms(symptoms);
        appointment.setStatus(AppointmentStatus.PENDING.toString());
        appointment.setNgayTao(new Date());
        this.save(appointment);

        this.scheduleService.deactivate(schedule);
        
        String doctorName=doctor.getUserId().getName();
        String scheduledAt = new SimpleDateFormat("HH:mm dd/MM/yyyy")
                            .format(appointment.getScheduledAt());
        this.notificationService.createAppointmentNotification(patient.getUserId(), doctorName, scheduledAt);
        
        String patientName = patient.getUserId().getName();
        this.notificationService.createNewBookingNotificationForDoctor(doctor.getUserId(), patientName, scheduledAt);
        
        return appointment;
    }

    @Override
    public List<Appointments> getByPatientId(int patientId) {
        return appointmentRepo.getByPatientId(patientId);
    }

    @Override
    public Appointments getById(int id) {
        return this.appointmentRepo.getById(id);
    }
  
    @Override
    public List<Appointments> getByDoctorId(int doctorId) {
        return this.appointmentRepo.getByDoctorId(doctorId);
    }
  
    @Override
    public void save(Appointments appointment) {
        this.appointmentRepo.save(appointment);
    }

    @Override
    public boolean existsByPatientAndTime(int patientId, Date scheduledAt) {
        return this.appointmentRepo.existsByPatientAndTime(patientId, scheduledAt);
    }

    @Override
    public Appointments bookFollowUp(int doctorId, int scheduleId, int patientId, String sysptoms) {
        Doctors doctor = this.doctorService.getDoctorById(doctorId);
        if (doctor==null)
            throw new RuntimeException("Không tìm thấy bác sĩ!");
        DoctorSchedules ds = this.scheduleService.getById(scheduleId);
        if (ds==null)
            throw new RuntimeException("Lịch không hợp lệ!");
        if (!ds.getIsActive())
            throw new RuntimeException("Lịch hẹn đã ngừng hoạt động!");
        if (!ds.getDoctorId().getId().equals(doctorId))
            throw new RuntimeException("Lịch không thuôc về bác sĩ đang thao tác!");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Date now = cal.getTime();
        
        if (ds.getStartTime().before(now))
            throw new RuntimeException("Lịch đã qua, vui lòng chọn lịch khác!");
        Patients p = this.patientService.getPatientById(patientId);
        if (p==null)
            throw new RuntimeException("Không tìm thấy bệnh nhân!");
        if (this.existsByPatientAndTime(patientId, ds.getStartTime()))
            throw new RuntimeException("Bệnh nhân đã có lịch hẹn vào khung giờ này rồi!");
        
        Appointments a = new Appointments();
        a.setDoctorId(doctor);
        a.setPatientId(p);
        a.setScheduledAt(ds.getStartTime());
        a.setSymptoms(sysptoms);
        a.setStatus(AppointmentStatus.PENDING.toString());
        a.setNgayTao(new Date());
        
        this.save(a);
        this.scheduleService.save(ds);
        
        String doctorName = doctor.getUserId().getName();
        String scheduledAt = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(a.getScheduledAt());
        this.notificationService.createFollowUpNotification(p.getUserId(), doctorName, scheduledAt);
        
        return a;
    }
}