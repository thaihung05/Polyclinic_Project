import axios from "axios";

const BASE_URL = "http://localhost:8080/Polyclinic/api";

export const endpoints = {
    specialties: "/specialties",
    doctors: "/doctors",
    login: "/login",
    register: "/register"
};

export default axios.create({
    baseURL: BASE_URL
})