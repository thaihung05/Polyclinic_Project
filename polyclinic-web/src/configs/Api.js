import axios from "axios";
import cookies from 'react-cookies';

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

    'doctor-schedules': (doctorId) => `/secure/doctors/${doctorId}/schedules`,
    'doctor-schedule-item': (doctorId, scheduleId) => `/secure/doctors/${doctorId}/schedules/${scheduleId}`,
    'my-doctor': '/secure/doctors/me',


    'medicines': '/medicines',
    'medicine-detail': (id) => `/medicines/${id}`,
    'medicines-secure': '/secure/medicines',
    'medicine-update': (id) => `/secure/medicines/${id}`,
    'medicine-delete': (id) => `/secure/medicines/${id}`,
    'medicines-low-stock': '/secure/medicines/alerts/low-stock',
    'medicines-near-expiry': '/secure/medicines/alerts/near-expiry',
    
    
    'patient-appointments': '/secure/patient/appointments',
    'medical-records':'/secure/medical-records',
    'lab-results':'/secure/lab-results',
    'book-appointment':'/secure/appointments',
    'payment-create':'/secure/payment/create',
    'payment-confirm':'/secure/payment/confirm',

    'appointment-detail': (id) => `/${id}`,
    'appointment-medical-record': (appointmentId) => `/appointments/${appointmentId}/medical-record`,
    'create-medical-record': (appointmentId) => `/secure/appointments/${appointmentId}/medical-record`,
    'update-medical-record': (recordId) => `/secure/medical-records/${recordId}`,

    'prescriptions-by-record': (recordId) => `/medical-records/${recordId}/prescriptions`,
    'create-prescription':     (recordId) => `/secure/medical-records/${recordId}/prescriptions`,

    'lab-results-by-appointment': (appointmentId) => `/appointments/${appointmentId}/lab-results`,
    'create-lab-result': (appointmentId) => `/secure/appointments/${appointmentId}/lab-results`,
    'update-lab-result': (id) => `/secure/lab-results/${id}`,


    'notifications':'/secure/notifications',
    'read-notifications':(id) => `/secure/notifications/${id}/read`

};

export default axios.create({
    baseURL: BASE_URL
})

export const authApis = () => axios.create({
    baseURL: BASE_URL,
    headers: {'Authorization': `Bearer ${cookies.load('token')}`}
})