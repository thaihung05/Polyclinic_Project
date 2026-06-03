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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Appointments book(int doctorId, int scheduleId, int patientId, String symptoms) {
        Doctors doctor = this.doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("Không tìm thấy bác sĩ");
        }

        DoctorSchedules schedule = this.scheduleService.getByIdWithLock(scheduleId);
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

        if (this.existsByPatientAndTime(patientId, schedule.getStartTime())) {
            throw new RuntimeException("Bạn đã có lịch hẹn vào khung giờ này rồi!");
        }
        if (this.existsByPatientDoctorAndDate(patientId, doctorId, schedule.getStartTime())) {
            throw new RuntimeException("Bạn đã có lịch hẹn với bác sĩ này trong hôm nay rồi!");
        }

        Appointments appointment = new Appointments();
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setScheduledAt(schedule.getStartTime());
        appointment.setSymptoms(symptoms);
        appointment.setStatus(AppointmentStatus.PENDING.toString());
        appointment.setNgayTao(new Date());
        this.save(appointment);

        this.scheduleService.deactivate(schedule);

        String doctorName = doctor.getUserId().getName();
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
    public boolean existsByPatientDoctorAndDate(int patientId, int doctorId, Date date) {
        return this.appointmentRepo.existsByPatientDoctorAndDate(patientId, doctorId, date);
    }

    @Override
    @Transactional
    public Appointments cancelAppointments(int appointmentId, String cancelReason, String cancelBy) {
        Appointments appointment = this.getById(appointmentId);
        if(appointment == null){
            throw new RuntimeException("Không tìm thấy cuộc hẹn");
        }
        if(appointment.getStatus().equals(AppointmentStatus.CANCELLED.name())){
            throw new RuntimeException("Lịch hẹn này đã được hủy");
        }
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        if (appointment.getScheduledAt() != null && appointment.getScheduledAt().after(cal.getTime())){
            DoctorSchedules slot = 
                    this.scheduleService.getByDoctorAndStartTime(appointment.getDoctorId().getId(), appointment.getScheduledAt());
            
            if(slot!=null && !slot.getIsActive()){
                this.scheduleService.reactivate(slot);
            }
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED.name());
        appointment.setCancelledBy(cancelBy);
        appointment.setCancelReason(cancelReason);
        
        this.save(appointment);
        
        String scheduledAt = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(appointment.getScheduledAt());
        String patientName = appointment.getPatientId().getUserId().getName();
        this.notificationService.createCancelNotificationForPatient(appointment.getPatientId().getUserId(), scheduledAt);
        this.notificationService.createCancelNotificationForDoctor(appointment.getDoctorId().getUserId(), patientName, scheduledAt);
        
        return appointment;
    }

    @Override
    @Transactional
    public Appointments finishAppointments(int appointmentId, String newStatus) {
        Appointments appointment = this.getById(appointmentId);
        if(appointment == null){
            throw new RuntimeException("Không tìm thấy cuộc hẹn");
        }
        if(!appointment.getStatus().equals(AppointmentStatus.CONFIRMED.name())){
            throw new RuntimeException("Chỉ được cập nhật lịch hẹn đã xác nhận");
        }
        
        appointment.setStatus(newStatus);
        this.save(appointment);
        
        return appointment;
    }

    @Override
    @Transactional
    public Appointments addMeetingUrl(int appointmentId, String meetingUrl) {
        Appointments appointment = this.getById(appointmentId);
        if(appointment == null){
            throw new RuntimeException("Không tìm thấy cuộc hẹn");
        }
        if(appointment.getMeetingUrl() != null){
            throw new RuntimeException("Đã có meeting url cho lịch hẹn này");
        }
        
        appointment.setMeetingUrl(meetingUrl);
        this.save(appointment);
        
        return appointment;
    }

    @Override
    public List<Appointments> getAll(String kw, String status, String fromDate, String toDate, int page) {
        return this.appointmentRepo.getAll(kw, status, fromDate, toDate, page);
    }

    @Override
    public long countAll(String kw, String status, String fromDate, String toDate) {
        return this.appointmentRepo.countAll(kw, status, fromDate, toDate);
    }
}

