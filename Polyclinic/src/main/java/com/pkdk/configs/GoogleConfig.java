/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.configs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Admin
 */
@Configuration
public class GoogleConfig {
    private static final String APPLICATION_NAME = "Medical Appointment";

    @Bean
    public Calendar getCalendarService() throws Exception {
        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = new ClassPathResource("credentials.json").getInputStream();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),
                new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT,
                        JacksonFactory.getDefaultInstance(),
                        clientSecrets,
                        Collections.singleton(CalendarScopes.CALENDAR))
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                        .setAccessType("offline")
                        .build();
        System.out.println(new java.io.File("tokens").getAbsolutePath());
        Credential credential = new AuthorizationCodeInstalledApp(
                flow,
                new LocalServerReceiver())
                .authorize("user");

        return new Calendar.Builder(HTTP_TRANSPORT,JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
