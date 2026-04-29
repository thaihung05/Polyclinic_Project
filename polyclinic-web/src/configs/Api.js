import axios from "axios";

const BASE_URL = "http://localhost:8080/Polyclinic/api";

export const endpoints = {
    'specialties': "/specialties",
    
    'login': "/login",
    'register': "/register",

    'doctors': "/doctors",
    'schedules': (doctorId) => `/doctors/${doctorId}/schedules`,

    'profile': '/secure/profile',
    'doctor-appointments': '/secure/doctor/appointments',
    'appointment-status': (id) => `/${id}/status`,
    'appointment-meeting': (id) => `/${id}/meeting`


};

export default axios.create({
    baseURL: BASE_URL
})

export const authApis = (token) => axios.create({
    baseURL: BASE_URL,
    headers: {'Authorization': `Bearer ${token}`}
})