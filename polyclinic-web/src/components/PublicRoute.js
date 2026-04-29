import { Navigate } from "react-router-dom";

const PublicRoute = ({ children }) => {
    const token = localStorage.getItem('polyclinic_token');
    const user = JSON.parse(localStorage.getItem('polyclinic_user') || 'null');

    if (token && user) {
        if (user.role === "ROLE_DOCTOR") return <Navigate to="/doctor/dashboard" />;
        return <Navigate to="/" />;
    }

    return children;
};

export default PublicRoute;
