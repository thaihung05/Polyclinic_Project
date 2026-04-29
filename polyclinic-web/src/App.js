import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./screens/Home/Home";
import Login from "./screens/User/Login";
import Register from "./screens/User/Register";
import Appointment from "./screens/Appointment/Appointment";
import ProtectedRoute from "./components/ProtectedRoute";
import DoctorDashboard from "./screens/Doctor/DoctorDashboard";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/appointment" element={
                    <ProtectedRoute requiredRole="ROLE_PATIENT"><Appointment /></ProtectedRoute>
                } />

                <Route path="/doctor/dashboard" element={
                    <ProtectedRoute requiredRole="ROLE_DOCTOR"><DoctorDashboard /></ProtectedRoute>
                } />
            </Routes>
        </BrowserRouter>
    );
}

export default App;