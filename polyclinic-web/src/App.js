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
import ScheduleManager from "./screens/Doctor/Schedules/ScheduleManager";
import { useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer";
import cookies from 'react-cookies';
import { MyNotificationContext, MyUserContext, SpecialtyContext } from "./configs/Contexts";
import MedicineManager from "./screens/Doctor/Medicines/MedicineManager";
import AppointmentDetail from "./screens/Doctor/Appointments/AppointmentDetail";
import Notification from "./screens/Notification/Notification";
import PharmacistDashboard from "./screens/Pharmacist/PharmacistDashboard";
import PharmacistHome from "./screens/Pharmacist/PharmacistHome";
import PrescriptionDispensing from "./screens/Pharmacist/Prescriptions/PrescriptionDispensing";
import MyNotificationReducer from "./reducers/MyNotificationReducer";
import SpecialtyReducer from "./reducers/SpecialtyReducer";


function App() {
    const [user, dispatch] = useReducer(MyUserReducer, cookies.load('user') || null);
    const [notification, notificationDispatch] = useReducer(MyNotificationReducer, { loading: false, notifications: [], error: '' });
    const [specialty, specialtyDispatch] = useReducer(SpecialtyReducer, { specialties: [], doctors: [], loading: false, error: '' });
    return (
        <MyUserContext.Provider value={[user, dispatch]}>
            <MyNotificationContext.Provider value={[notification, notificationDispatch]}>
                <SpecialtyContext.Provider value={[specialty, specialtyDispatch]}>
                    <BrowserRouter>
                        <Routes>
                            <Route path="/" element={<Home />} />
                            <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
                            <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
                            <Route path="/notifications" element={<ProtectedRoute><Notification /></ProtectedRoute>} />
                            <Route path="/appointment" element={
                                <ProtectedRoute requiredRole="ROLE_PATIENT"><Appointment /></ProtectedRoute>
                            } />

                            <Route path="/doctor/dashboard" element={
                                <ProtectedRoute requiredRole="ROLE_DOCTOR"><DoctorDashboard /></ProtectedRoute>
                            }>
                                <Route index element={<DoctorHome />} />
                                <Route path="appointments" element={<AppointmentList />} />
                                <Route path="appointments/:appointmentId" element={<AppointmentDetail />} />
                                <Route path="schedules" element={<ScheduleManager />} />
                            </Route>

                            <Route path="/pharmacist/dashboard" element={
                                <ProtectedRoute requiredRole="ROLE_PHARMACIST"><PharmacistDashboard /></ProtectedRoute>
                            }>
                                <Route index element={<PharmacistHome />} />
                                <Route path="medicines" element={<MedicineManager />} />
                                <Route path="prescriptions" element={<PrescriptionDispensing />} />
                            </Route>

                            <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
                            <Route path="/patient/appointments" element={<ProtectedRoute><PatientAppointment /></ProtectedRoute>}></Route>
                            <Route path="/patient/medical-history" element={<ProtectedRoute><MedicalHistory /></ProtectedRoute>}></Route>
                        </Routes>
                    </BrowserRouter>
                </SpecialtyContext.Provider>

            </MyNotificationContext.Provider>
        </MyUserContext.Provider>

    );
}

export default App;