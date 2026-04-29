import { Navigate } from "react-router-dom";

const ProtectedRoute = ({children, requiredRole}) =>{
    const token = localStorage.getItem('polyclinic_token')
    const user = JSON.parse(localStorage.getItem('polyclinic_user') || 'null')
    if (!token || !user) return <Navigate to={"/login"} />
    if (requiredRole && user.role !== requiredRole ) return <Navigate to={'/'} />
    return children;
}

export default ProtectedRoute;