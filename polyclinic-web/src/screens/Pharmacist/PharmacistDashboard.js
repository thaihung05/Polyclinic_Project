import { NavLink, Outlet } from "react-router-dom";
import Header from "../../components/Header";
import "../Doctor/DoctorDashboard.css";
import cookies from 'react-cookies';

const PharmacistDashboard = () => {
    const user = cookies.load('user');

    return (
        <>
            <Header />
            <div className="doctor-layout">
                <aside className="doctor-sidebar">
                    <div className="sidebar-header">
                        {user.name && (
                            <div className="sidebar-username">Dược sĩ {user.name}</div>
                        )}
                    </div>
                    <nav className="sidebar-nav">
                        <NavLink to="/pharmacist/dashboard" end
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-speedometer2 me-2"></i>Tổng quan
                        </NavLink>
                        <NavLink to="/pharmacist/dashboard/medicines"
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-capsule me-2"></i>Quản lý kho thuốc
                        </NavLink>
                        <NavLink to="/pharmacist/dashboard/prescriptions"
                            className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}
                        >
                            <i className="bi bi-file-earmark-medical me-2"></i>Đơn thuốc chờ cấp
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

export default PharmacistDashboard;