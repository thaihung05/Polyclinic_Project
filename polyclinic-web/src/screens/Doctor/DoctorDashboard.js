import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Api";
import Header from "../../components/Header";

const DoctorDashboard = () => {

    const navigate = useNavigate();
    const token = localStorage.getItem('polyclinic_token');

    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(false);

    const loadAppointments = async () => {
        try {
            setLoading(true);
            const res = await authApis(token).get(endpoints['doctor-appointments']);
            setAppointments(res.data);
        }
        catch(err){
            console.log(err);
        }
        finally{
            setLoading(false);
        }
    };

    useEffect(()=>{
        loadAppointments();
    }, []);

    return(
        <>
            <Header />
        </>
    );
}

export default DoctorDashboard;