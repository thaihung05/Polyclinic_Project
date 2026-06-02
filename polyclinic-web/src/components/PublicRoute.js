import { Navigate } from "react-router-dom";
import { useContext } from "react";
import { MyUserContext } from "../configs/Contexts";

const PublicRoute = ({ children }) => {
    const [user] = useContext(MyUserContext);

    if (user) {
        if (user.role === "ROLE_DOCTOR") return <Navigate to="/doctor/dashboard" />;
        else if (user.role === "ROLE_PHARMACIST") return <Navigate to="/pharmacist/dashboard" />;
        return <Navigate to="/" />;
    }
    return children;
};

export default PublicRoute;
