import { useEffect, useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import "./DoctorDashboard.css";

const DoctorDashboard = () => {
    return (
        <>
            <Header />
            <div className="doctor-layout">
                <aside className="doctor-sidebar">
                    <div className="sidebar-header">
                        <i className="bi bi-hospital me-2"></i>Bác sĩ
                    </div>
                    <nav className="sidebar-nav">
                        <NavLink to="/doctor/dashboard" end className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
                            <i className="bi bi-calendar-check me-2"></i>Lịch hẹn bệnh nhân
                        </NavLink>
                        <NavLink to="/doctor/dashboard/schedules" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
                            <i className="bi bi-clock me-2"></i>Lịch làm việc
                        </NavLink>
                    </nav>
                </aside>

                <main className="doctor-content">
                    <Outlet />
                </main>
            </div>
        </>
    );
}

export default DoctorDashboard;