import { NavLink, Outlet } from "react-router-dom";
import Header from "../../components/Header";
import "./DoctorDashboard.css";

const DoctorDashboard = () => {
    const user = JSON.parse(localStorage.getItem("polyclinic_user") || "{}");

    return (
        <>
            <Header />
            <div className="doctor-layout">
                <aside className="doctor-sidebar">
                    <div className="sidebar-header">
                        {user.name && (
                            <div className="sidebar-username">{user.name}</div>
                        )}
                    </div>
                    <nav className="sidebar-nav">
                        <NavLink to="/doctor/dashboard" end
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-speedometer2 me-2"></i>Tổng quan
                        </NavLink>
                        <NavLink to="/doctor/dashboard/appointments"
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-calendar-check me-2"></i>Lịch hẹn bệnh nhân
                        </NavLink>
                        <NavLink to="/doctor/dashboard/schedules"
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-clock me-2"></i>Lịch làm việc
                        </NavLink>
                        <div className="sidebar-divider"></div>
                        <NavLink to="/profile"
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-person-circle me-2"></i>Hồ sơ cá nhân
                        </NavLink>
                    </nav>
                </aside>

                <main className="doctor-content">
                    <Outlet />
                </main>
            </div>
        </>
    );
};

export default DoctorDashboard;