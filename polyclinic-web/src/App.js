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
                    <Route index element={<AppointmentList />} />
                </Route>
                <Route path="/profile" element={
                    <ProtectedRoute><Profile /></ProtectedRoute>
                } />
            </Routes>
        </BrowserRouter>
    );
}

export default App;