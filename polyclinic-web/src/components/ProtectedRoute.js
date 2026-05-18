import { Navigate } from "react-router-dom";
import { useContext } from "react";
import { MyUserContext } from "../configs/Contexts";

const ProtectedRoute = ({children, requiredRole}) =>{
    const [user] = useContext(MyUserContext);

    if (!user) return <Navigate to="/login" />;
    if (requiredRole && user.role !== requiredRole) return <Navigate to="/" />;
    return children;
}

export default ProtectedRoute;