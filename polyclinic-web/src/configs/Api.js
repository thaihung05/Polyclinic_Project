import axios from "axios";

const BASE_URL = "http://localhost:8080/Polyclinic/api";

export const endpoints = {
    'specialties': "/specialties",
    
    'login': "/login",
    'register': "/register",

    'doctors': "/doctors",
    'schedules': (doctorId) => `/doctors/${doctorId}/schedules`,

    'profile': '/secure/profile',
    'update-profile': '/secure/profile',
    'change-password': '/secure/profile/change-password',
    'doctor-appointments': '/secure/doctor/appointments',
    'appointment-status': (id) => `/secure/appointments/${id}/status`,
    // 'appointment-meeting': (id) => `/${id}/meeting`,

    'doctor-schedules': (doctorId) => `/secure/doctors/${doctorId}/schedules`,
    'doctor-schedule-item': (doctorId, scheduleId) => `/secure/doctors/${doctorId}/schedules/${scheduleId}`,
    'patient-appointments': '/secure/patient/appointments',
    'medical-records':'/secure/medical-records',
    'lab-results':'/secure/lab-results',
    'book-appointment':'/secure/appointments'

};

export default axios.create({
    baseURL: BASE_URL
})

export const authApis = (token) => axios.create({
    baseURL: BASE_URL,
    headers: {'Authorization': `Bearer ${token}`}
})