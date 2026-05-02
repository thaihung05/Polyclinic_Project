import { useCallback, useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import { Alert, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";

const statusLable = (s) => {
    if (s==="PENDING") return "Chờ xác nhận";
    if (s==="CONFIRMED") return "Đã xác nhận";
    if (s==="COMPLETED") return "Hoàn thành";
    if (s==="CANCELLED") return "Đã hủy";
    return s; 
}

const statusBg = (s) => {
    if (s==="PENDING") return "warning";
    if (s==="CONFIRMED") return "primary";
    if (s==="COMPLETED") return "success";
    if (s==="CANCELLED") return "danger";
    return s;
}

const DoctorHome = () =>{
    const token = localStorage.getItem("polyclinic_token");
    const user = JSON.parse(localStorage.getItem("polyclinic_user"));
    const navigate = useNavigate();
    
    const [appointments, setAppoiments] = useState([]);
    const [loading, setLoading] = useState(false);


    const loadAppointments = useCallback(async () => {
        try{
            setLoading(true);
            const res = await authApis(token).get(endpoints['doctor-appointments']);
            setAppoiments(res.data || []);
        } catch(ex){
            console.error(ex);
        } finally{
            setLoading(false);
        }
    },[token]);

    useEffect(() => {
        loadAppointments();
    }, [loadAppointments]);

    const today = new Date().toISOString().slice(0,10);
    const todayAppointments = appointments.filter(a=>{
        const d = a.scheduledAt ? a.scheduledAt.slice(0, 10) : "";
        return d === today;
    });

    const peding = appointments.filter(a=>
        a.status === "PENDING"
    ).length;
    const confirmed = appointments.filter(a=>
        a.status === "CONFIRMED"
    ).length;
    const completed = appointments.filter(a=>
        a.status === "COMPLETED"
    ).length;


    const card = [
        {label: "Tổng lịch hẹn", value: appointments.length, bg:"#e8f4f8"},
        {label: "Chờ xác nhận", value: peding, bg:"#e8f4f8"},
        {label: "Đã xác nhận", value: confirmed, bg:"#e8f4f8"},
        {label: "Hoàn thành", value: completed, bg:"#e8f4f8"}
    ]

    return(
        <div>
            <div className="mb-4">
                <h4 className="fw-bold mb-1">
                    Xin chào, bác sĩ {user.name}
                </h4>
                <p>
                    Hôm nay: {new Date().toLocaleString("vi-VN", {weekday: "long", year: "numeric", month:"long", day:"numeric"})}
                </p>
            </div>

            <div className="row g-3 mb-4">
                {card.map((c,i) => (
                    <div className="col-6 col-md-3" key={i}>
                        <div className="card border-0 shadow-sm h-100" style={{ background: c.bg }}>
                            <div className="card-body d-flex align-items-center gap-3">
                                <div className="rounded-circel d-flex align-item-center justify-content-center">
                                </div>
                                <div>
                                    <div className="fs-4 fw-bold">{c.value}</div>
                                    <div className="small text-muted">{c.label}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            <div className="card shadow-sm">
                <div className="card-header fw-semibold bg-white d-flex justify-content-between align-items-center">
                    <span>
                        <i className="bi bi-calendar-day me-2 text-primary"></i>
                        Lịch hẹn hôm nay
                        <Badge bg="primary" className="ms-2">{todayAppointments.length}</Badge>
                    </span>
                    <button className="btn btn-sm btn-outline-primary" onClick={() => navigate("/doctor/dashboard/appointments")}>
                        Xem tất cả
                    </button>
                </div>
            </div>
            <div>
                {loading && <div className="p-3"><MySpinner /></div>}
                {!loading && todayAppointments.length === 0 && (
                    <Alert variant="light" className="m-3 text-muted">
                        <i className="bi bi-check-circle me-2"></i>Bác sĩ không có lịch hẹn hôm nay nè!
                    </Alert>
                )}
                {!loading && todayAppointments.length > 0 && (
                    <div className="table-responsive">
                        <table className="table table-hover mb-0">
                            <thead className="table-light">
                                <tr>
                                    <th>Giờ hẹn</th>
                                    <th>Bệnh nhân</th>
                                    <th>Triệu chứng</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                {todayAppointments.map(a=> (
                                    <tr key={a.id}>
                                        <td className="fw-semibold">{a.scheduledAt.slice(11,16)}</td>
                                        <td>{a.patientId.userId.name}</td>
                                        <td className="text-muted">{a.symptoms || "-"}</td>
                                        <td>
                                            <Badge bg={statusBg(a.status)}>{statusLable(a.status)}</Badge>
                                        </td>
                                        <td>
                                            {(a.status === "CONFIRMED" || a.status === "COMPLETED") && (
                                                <button className="btn btn-sm btn-outline-info" onClick={() => navigate(`/doctor/dashboard/medical-records/${a.id}`)}>
                                                    <i className="bi bi-file-medical me-1"></i>Hồ sơ
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}

export default DoctorHome;