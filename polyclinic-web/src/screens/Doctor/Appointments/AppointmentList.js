import { useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import { Alert, Badge, Button, Table } from "react-bootstrap";
import MySpinner from "../../../components/MySpinner";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";

const statusLabel = (s) => {
    if (s === "CONFIRMED") return "Đã xác nhận";
    if (s === "COMPLETED") return "Hoàn thành";
    if (s === "CANCELLED") return "Đã hủy";
    if (s === "NO_SHOW") return "Vắng khám";
    return s;
};

const statusBg = (s) => {
    if (s === "CONFIRMED") return "primary";
    if (s === "COMPLETED") return "success";
    if (s === "CANCELLED") return "danger";
    if (s === "NO_SHOW") return "secondary";
    return "light";
}

const AppointmentList = () => {
    const [loading, setLoading] = useState(false);
    const [appointments, setAppointments] = useState([]);
    const [tab, setTab] = useState("");
    const navigate = useNavigate();

    const loadAppointments = async () => {
        try {
            setLoading(true);
            let res = await authApis().get(endpoints['doctor-appointments']);
            setAppointments(res.data);
        } catch (ex) {
            console.log(ex);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadAppointments();
    }, [])

    const updateStatus = async (id, newStatus) => {
        let body = { 
            status: newStatus 
        };
        let confirm = await Swal.fire({
            title: "Xác nhận",
            text: `Cập nhật trạng thái sang "${statusLabel(newStatus)}"?`,
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Đóng"
        });
        if (!confirm.isConfirmed) return;

        try {
            await authApis().patch(endpoints['appointment-status'](id), body);
            Swal.fire({ icon: "success", title: "Cập nhật thành công!", showConfirmButton: false, timer: 1000 });
            loadAppointments();
        } catch (ex) {
            Swal.fire({ 
                icon: "error", 
                title: "Đã xảy ra lỗi!", 
                text: "Không thể thay đổi trạng thái!" 
            });
        }
    }


    let tabs = ["", "CONFIRMED", "COMPLETED", "CANCELLED", "NO_SHOW"];
    let statusCount = {};
    for (let i = 0; i < appointments.length; i++) {
        let a = appointments[i];
        if (statusCount[a.status])
            statusCount[a.status] += 1
        else
            statusCount[a.status] = 1
    }

    const tabLabel = (t) => {
        if (t === "") return `Tất cả (${appointments.length})`;
        return `${statusLabel(t)} (${statusCount[t] || 0})`
    }

    const list = tab ? appointments.filter(a => a.status === tab) : appointments;

    return (
        <div>
            <h4 className="mb-4 fw-bold">Lịch hẹn bệnh nhân</h4>

            <div className="mb-3 d-flex gap-2 flex-wrap">
                {tabs.map(t => (
                    <button key={t} className={`btn btn-sm ${tab === t ? "btn-primary" : "btn-outline-secondary"}`} onClick={() => setTab(t)}>
                        {tabLabel(t)}
                    </button>
                ))}
            </div>
            {loading && <MySpinner />}
            {!loading && list.length === 0 && <Alert variant="success">Không có lịch hẹn</Alert>}
            {!loading && list.length > 0 && (
                <Table>
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Bệnh nhân</th>
                            <th>Ngày giờ hẹn</th>
                            <th>Triệu chứng</th>
                            <th>Link khám bệnh</th>
                            <th>Trạng thái</th>
                            <th>Cập nhật</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {list.map((a, i) => (
                            <tr key={a.id}>
                                <td>{i + 1}</td>
                                <td>{a.patientId?.userId?.name}</td>
                                <td>{a.scheduledAt ? new Date(a.scheduledAt).toLocaleString("vi-VN") : "—"}</td>
                                <td>{a.symptoms}</td>
                                <td>
                                    {a.status === "CONFIRMED" && a.meetingUrl && (
                                        <a
                                            href={a.meetingUrl}
                                            target="_blank"
                                            rel="noreferrer"
                                            className="btn btn-sm btn-outline-success"
                                        >
                                            Vào khám
                                        </a>
                                    )}
                                </td>
                                <td>
                                    <Badge bg={statusBg(a.status)}>{statusLabel(a.status)}</Badge>
                                </td>
                                <td>
                                    {a.status === "CONFIRMED" ? (
                                        <select
                                            className="form-select form-select-sm"
                                            value={a.status}
                                            onChange={e => updateStatus(a.id, e.target.value)}
                                        >
                                            <option value="CONFIRMED">-- Chọn --</option>
                                            <option value="COMPLETED">Hoàn thành</option>
                                            <option value="NO_SHOW">Vắng khám</option>
                                        </select>
                                    ) : (
                                        <span className="text-muted fst-italic">-</span>
                                    )}
                                </td>
                                <td>
                                    <Button variant="outline-primary" size="sm"
                                        onClick={() => navigate(`/doctor/dashboard/appointments/${a.id}`)}
                                        disabled={a.status === "COMPLETED" || a.status === 'CANCELLED'}
                                    >
                                        Chi tiết
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}
        </div>
    )

}

export default AppointmentList;