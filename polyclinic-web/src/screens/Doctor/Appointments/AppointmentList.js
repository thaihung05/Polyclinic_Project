import { useCallback, useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import { Alert, Badge, Table } from "react-bootstrap";
import MySpinner from "../../../components/MySpinner";
import Swal from "sweetalert2";

const statusLabel = (s) => {
    if (s === "PENDING")   return "Chờ xác nhận";
    if (s === "CONFIRMED") return "Đã xác nhận";
    if (s === "COMPLETED") return "Hoàn thành";
    if (s === "CANCELLED") return "Đã hủy";
    return s;
};

const statusBg = (s) => {
    if (s === "PENDING")   return "warning";
    if (s === "CONFIRMED") return "primary";
    if (s === "COMPLETED") return "success";
    if (s === "CANCELLED") return "danger";
    if (s === "NO_SHOW")   return "secondary";
    return "light";
}

const AppointmentList = () => {
    const token = localStorage.getItem('polyclinic_token');
    const [loading, setLoading] = useState(false);
    const [appointments, setAppointments] = useState([]);
    const [tab, setTab] = useState("");

    const loadAppointments = useCallback(async () => {
        try {
            setLoading(true);
            let res = await authApis(token).get(endpoints['doctor-appointments']);
            setAppointments(res.data);
        } catch(ex){
            console.log(ex);
        }finally{
            setLoading(false);
        }
    }, [token]);

    useEffect(()=> {
        loadAppointments();
    },[loadAppointments])

    const updateStatus = async (id, newStatus) => {
        let body = { status: newStatus }

        if (newStatus === "CANCELLED"){
            let result = await Swal.fire({
                title: "Lý do hủy?",
                input: "textarea",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "Xác nhận hủy!",
                cancelButtonText: "Đóng",
                inputValidator: (value) => {
                    if (!value) return "Vui lòng nhập lý do hủy hẹn!!";
                }
            });
            if (!result.isConfirmed) return;
            body.cancelReason = result.value;
            body.cancelledBy = "ROLE_DOCTOR";
        } else {
            let confirm = await Swal.fire({
                title: "Xác nhận",
                text: `Cập nhật trạng thái sang "${statusLabel(newStatus)}"?`,
                icon: "question",
                showCancelButton: true,
                confirmButtonText: "Xác nhận",
                cancelButtonText: "Đóng"
            });
            if (!confirm.isConfirmed) return;
        }

        try {
            await authApis(token).patch(endpoints['appointment-status'](id), body);
            Swal.fire({
                icon: "success",
                title: "Cập nhật thành công!",
                showConfirmButton: false,
                timer: 1000
            })
            loadAppointments();
        } catch(ex){
            console.log(ex)
            Swal.fire({
                icon: "error",
                title: "Đã xảy ra lỗi!",
                text: "Không thể thay đổi trạng thái!"
            })
        }

    }

    let tabs = ["", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"];
    let statusCount = {};
    for (let i = 0;i<appointments.length;i++){
        let a = appointments[i];
        if (statusCount[a.status])
            statusCount[a.status] +=1
        else
            statusCount[a.status] = 1
    }

    const tabLabel = (t) => {
        if (t === "") return `Tất cả (${appointments.length})`;
        return `${statusLabel(t)} (${statusCount[t] || 0})`
    }

    const list = tab ? appointments.filter(a => a.status === tab) : appointments;

    return(
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
                        </tr>
                    </thead>
                    <tbody>
                        {list.map((a, i) => (
                            <tr key={a.id}>
                                <td>{i+1}</td>
                                <td>{a.patientId.userId.name}</td>
                                <td>{a.scheduledAt}</td>
                                <td>{a.symptoms}</td>
                                <td>
                                    {a.status === "CONFIRMED" && (
                                        <button className="btn btn-sm btn-outline-secondary">Vào khám</button>
                                    )}
                                </td>
                                <td>
                                    <Badge bg={statusBg(a.status)}>{statusLabel(a.status)}</Badge>
                                </td>
                                <td>
                                    {a.status !== "COMPLETED" && a.status !== "CANCELLED" ? (
                                        <select
                                            className="form-select form-select-sm"
                                            value={a.status}
                                            onChange={e => updateStatus(a.id, e.target.value)}
                                        >
                                            <option value="PENDING">Chờ xác nhận</option>
                                            <option value="CONFIRMED">Đã xác nhận</option>
                                            <option value="COMPLETED">Hoàn thành</option>
                                            <option value="CANCELLED">Đã hủy</option>
                                        </select>
                                    ):(
                                        <span className="text-muted fst-italic">Không đổi được trạng thái</span>
                                    )}
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