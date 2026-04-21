/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.service.impl;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.pkdk.pojo.Appointments;
import com.pkdk.service.GoogleMeetingService;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class GoogleMeetingServiceImpl implements GoogleMeetingService {

    @Autowired
    private Calendar calendarService;

    @Override
    public String createMeeting(Appointments appointment) {
        try {
            Date start = appointment.getScheduledAt();
            Date end = new Date(start.getTime() + 30 * 60 * 1000);
            Event event = new Event().setSummary("Khám bệnh online");
            String description = String.format(
                    "Bệnh nhân: %s\nBác sĩ: %s\nTriệu chứng: %s",
                    appointment.getPatientId().getUserId().getName(),
                    appointment.getDoctorId().getUserId().getName(),
                    appointment.getSymptoms()
            );
            event.setDescription(description);
            EventDateTime startTime = new EventDateTime()
                    .setDateTime(new DateTime(start))
                    .setTimeZone("Asia/Ho_Chi_Minh");

            EventDateTime endTime = new EventDateTime()
                    .setDateTime(new DateTime(end))
                    .setTimeZone("Asia/Ho_Chi_Minh");
            event.setStart(startTime);
            event.setEnd(endTime);
            
            ConferenceData conferenceData = new ConferenceData();
            CreateConferenceRequest request = new CreateConferenceRequest();
            request.setRequestId(UUID.randomUUID().toString());

            conferenceData.setCreateRequest(request);
            event.setConferenceData(conferenceData);

            Event createdEvent = calendarService.events()
                    .insert("thaihung.work05@gmail.com", event)
                    .setConferenceDataVersion(1)
                    .execute();

            return createdEvent.getHangoutLink();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
