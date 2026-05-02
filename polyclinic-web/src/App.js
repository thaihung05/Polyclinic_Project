import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./screens/Home/Home";
import Login from "./screens/User/Login";
import Register from "./screens/User/Register";
import Profile from "./screens/User/Profile";
import Appointment from "./screens/Appointment/Appointment";
import ProtectedRoute from "./components/ProtectedRoute";
import PublicRoute from "./components/PublicRoute";
import DoctorDashboard from "./screens/Doctor/DoctorDashboard";
import AppointmentList from "./screens/Doctor/Appointments/AppointmentList";
import PatientAppointment from "./screens/Patient/Appointment/PatientAppointment";
import MedicalHistory from "./screens/Patient/MedicalHistory";
import DoctorHome from "./screens/Doctor/Home/DoctorHome";


function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
                <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
                <Route path="/appointment" element={
                    <ProtectedRoute requiredRole="ROLE_PATIENT"><Appointment /></ProtectedRoute>
                } />

                <Route path="/doctor/dashboard" element={
                    <ProtectedRoute requiredRole="ROLE_DOCTOR"><DoctorDashboard /></ProtectedRoute>
                    }>
                    <Route index element={<DoctorHome />} />
                </Route>
                <Route path="/profile" element={
                    <ProtectedRoute><Profile /></ProtectedRoute>
                } />
                <Route path="/patient/appointments" element={<ProtectedRoute><PatientAppointment/></ProtectedRoute>}></Route>
                <Route path="/patient/medical-history" element={<ProtectedRoute><MedicalHistory/></ProtectedRoute>}></Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;