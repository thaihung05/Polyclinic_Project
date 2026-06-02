import { useCallback, useContext, useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import { Alert, Badge, Table } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";
import { MyUserContext } from "../../../configs/Contexts";

const statusLable = (s) => {
    if (s==="CONFIRMED") return "Đã xác nhận";
    if (s==="COMPLETED") return "Hoàn thành";
    if (s==="CANCELLED") return "Đã hủy";
    if (s==="NO_SHOW") return "Không đến";
    return s;
}

const statusBg = (s) => {
    if (s==="CONFIRMED") return "primary";
    if (s==="COMPLETED") return "success";
    if (s==="CANCELLED") return "danger";
    if (s==="NO_SHOW") return "secondary";
    return "secondary";
}

const DoctorHome = () =>{
    const [user] = useContext(MyUserContext);
    const navigate = useNavigate();

    const [appointments, setAppoiments] = useState([]);
    const [loading, setLoading] = useState(false);


    const loadAppointments = useCallback(async () => {
        try{
            setLoading(true);
            const res = await authApis().get(endpoints['doctor-appointments']);
            setAppoiments(res.data || []);
        } catch(ex){
            console.error(ex);
        } finally{
            setLoading(false);
        }
    },[]);

    useEffect(() => {
        loadAppointments();
    }, [loadAppointments]);

    const today = new Date().toISOString().slice(0,10);

    const todayAppointments = appointments.filter(a => {
        const t = new Date(a.scheduledAt);
        return t.toISOString().slice(0,10) === today && t >= new Date();
    }).sort((a, b) => new Date(a.scheduledAt) - new Date(b.scheduledAt));

    const confirmed = appointments.filter(a=>
        a.status === "CONFIRMED"
    ).length;
    const completed = appointments.filter(a=>
        a.status === "COMPLETED"
    ).length;
    const noShow = appointments.filter(a=>
        a.status === "NO_SHOW"
    ).length;

    const card = [
        {label: "Tổng lịch hẹn", value: appointments.length, bg:"#e8f4f8", icon:"bi-calendar2-week", color:"#0ea5e9"},
        {label: "Hôm nay", value: todayAppointments.length, bg:"#fff8e1", icon:"bi-calendar-day", color:"#f59e0b"},
        {label: "Đã xác nhận", value: confirmed, bg:"#e3f2fd", icon:"bi-check-circle", color:"#3b82f6"},
        {label: "Hoàn thành", value: completed, bg:"#e8f5e9", icon:"bi-check2-all", color:"#22c55e"},
        {label: "Không đến", value: noShow, bg:"#f5f5f5", icon:"bi-person-x", color:"#9ca3af"},
    ]

    return(
        <div>
            <div className="mb-4">
                <h4 className="fw-bold mb-1">
                    Xin chào, Bác sĩ {user.name}
                </h4>
                <p>
                    Hôm nay: {new Date().toLocaleString("vi-VN", {weekday: "long", year: "numeric", month:"long", day:"numeric"})}
                </p>
            </div>

            <div className="row g-3 mb-4">
                {card.map((c,i) => (
                    <div className="col-6 col-md" key={i}>
                        <div className="card border-0 shadow-sm h-100" style={{ background: c.bg }}>
                            <div className="card-body d-flex align-items-center gap-3">
                                <i className={`bi ${c.icon} fs-2`} style={{ color: c.color }}></i>
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
                <div className="card-body p-0">
                {loading && <div className="p-3"><MySpinner /></div>}
                {!loading && todayAppointments.length === 0 && (
                    <Alert variant="light" className="m-3 text-muted">
                        <i className="bi bi-check-circle me-2"></i>Bác sĩ không có lịch hẹn hôm nay nè!
                    </Alert>
                )}
                {!loading && todayAppointments.length > 0 && (
                    <div className="table-responsive">
                        <Table className="table table-hover mb-0">
                            <thead className="table-light">
                                <tr>
                                    <th>Giờ hẹn</th>
                                    <th>Bệnh nhân</th>
                                    <th>SĐT</th>
                                    <th>Triệu chứng</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                {todayAppointments.map(a=> (
                                    <tr key={a.id}>
                                        <td className="fw-semibold">
                                            {new Date(a.scheduledAt).toLocaleTimeString("vi-VN", {
                                                hour: "2-digit",
                                                minute: "2-digit"
                                            })}
                                        </td>
                                        <td>{a.patientId.userId.name}</td>
                                        <td className="text-muted">{a.patientId.userId.phone || "-"}</td>
                                        <td className="text-muted">{a.symptoms || "-"}</td>
                                        <td>
                                            <Badge bg={statusBg(a.status)}>{statusLable(a.status)}</Badge>
                                        </td>
                                        <td>
                                            {(a.status === "CONFIRMED" || a.status === "COMPLETED") && (
                                                <button className="btn btn-sm btn-outline-info" onClick={() => navigate(`/doctor/dashboard/appointments/${a.id}`)}>
                                                    <i className="bi bi-file-medical me-1"></i>Hồ sơ
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </div>
                )}
                </div>
            </div>
        </div>
    )
}

export default DoctorHome;