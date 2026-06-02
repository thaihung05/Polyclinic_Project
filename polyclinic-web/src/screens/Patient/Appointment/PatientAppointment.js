import { useContext, useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import Swal from "sweetalert2";
import { Badge, Button, Pagination, Table } from "react-bootstrap";
import Header from "../../../components/Header";
import Footer from "../../../components/Footer";
import { MyUserContext } from "../../../configs/Contexts";
import Moment from "react-moment";
import MySpinner from "../../../components/MySpinner";

const PAGE_SIZE = 5;

const PatientAppointment = () => {
    const [user] = useContext(MyUserContext);
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);

    const loadAppointments = async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoints["patient-appointments"]);
            const sorted = [...res.data].sort((a, b) => new Date(b.scheduledAt) - new Date(a.scheduledAt));
            setAppointments(sorted);
            setPage(1);
        } catch (err) {
            console.log(err);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadAppointments();
    }, []);

    const renderStatus = (status) => {
        if (status === "PENDING" || status === "pending")
            return <Badge bg="warning" text="dark">Chờ xác nhận</Badge>
        else if (status === "confirmed" || status === "CONFIRMED")
            return <Badge bg="success">Đã xác nhận</Badge>
        else if (status === "cancelled" || status === "CANCELLED")
            return <Badge bg="danger">Đã hủy</Badge>
        else if (status === "completed" || status === "COMPLETED")
            return <Badge bg="secondary">Hoàn thành</Badge>
    };

    const cancelAppointment = async (appointment) => {
        const result = await Swal.fire({
            title: "Hủy lịch hẹn",
            input: "textarea",
            inputLabel: "Lý do hủy",
            inputPlaceholder: "Nhập lý do hủy lịch hẹn...",
            showCancelButton: true,
            confirmButtonText: "Xác nhận hủy",
            cancelButtonText: "Đóng",
            confirmButtonColor: "#d33",
            allowOutsideClick: () => !Swal.isLoading(),
            preConfirm: async (cancelReason) => {
                const btn = Swal.getConfirmButton();
                btn.style.minWidth = btn.offsetWidth + 'px';
                btn.style.whiteSpace = 'nowrap';
                btn.disabled=true;
                btn.innerHTML=`<span class="spinner-border spinner-border-sm me-2" role="status"></span> Đang hủy`;
                try {
                    await authApis().patch(endpoints["appointment-status"](appointment.id), {
                        status: "CANCELLED",
                        cancelReason: cancelReason || "",
                        cancelledBy: user?.role,
                    });

                } catch (err) {
                    btn.disabled = false;
                    btn.style.minWidth = '';
                    btn.style.whiteSpace = '';
                    btn.innerHTML = "Xác nhận hủy";
                    Swal.showValidationMessage(err?.response?.data || "Không thể hủy lịch hẹn.");
                }
            }
        });
        if (result.isConfirmed) {
            await Swal.fire("Đã hủy!", "Lịch hẹn đã được hủy thành công.", "success");
            loadAppointments();
        }




    };

    const totalPages = Math.ceil(appointments.length / PAGE_SIZE);
    const displayed = appointments.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

    return (
        <>
            <Header />
            <main className="container my-4">
                <h4 className="fw-bold mb-4 text-center">Lịch hẹn của tôi</h4>
                {loading ? (
                    <MySpinner />
                ) : appointments.length === 0 ? (
                    <div className="text-center text-muted py-5">
                        Bạn chưa có lịch hẹn nào.
                    </div>
                ) : (
                    <div className="m-4">
                        <Table bordered hover responsive>
                            <thead className="table-light">
                                <tr>
                                    <th>Số</th>
                                    <th>Bác sĩ</th>
                                    <th>Chuyên khoa</th>
                                    <th>Thời gian</th>
                                    <th>Triệu chứng</th>
                                    <th>Lý do hủy</th>
                                    <th>Link khám online</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                {displayed.map((appointment, idx) => (
                                    <tr key={appointment.id}>
                                        <td>{idx + 1}</td>
                                        <td>{appointment.doctorId?.userId?.name || ""}</td>
                                        <td>{appointment.doctorId?.specialtyId?.name || ""}</td>
                                        <td>
                                            <Moment format="DD/MM/YYYY - HH:mm">{appointment.scheduledAt}</Moment>
                                        </td>
                                        <td>{appointment.symptoms || ""}</td>
                                        <td>{appointment.status === "CANCELLED" ? (appointment.cancelReason || "Không có lý do") : "—"}</td>
                                        <td>
                                            {appointment.meetingUrl && appointment.status === "CONFIRMED"
                                                ? <a href={appointment.meetingUrl} target="_blank" rel="noreferrer"
                                                    className="btn btn-success btn-sm">
                                                    <i className="bi bi-camera-video-fill me-1"></i>Vào phòng khám
                                                </a>
                                                : ""}
                                        </td>
                                        <td>{renderStatus(appointment.status)}</td>
                                        <td>
                                            {(appointment.status === "PENDING" || appointment.status === "CONFIRMED") && (
                                                <Button variant="danger" size="sm" onClick={() => cancelAppointment(appointment)}>
                                                    Hủy
                                                </Button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>

                        {totalPages > 1 && (
                            <Pagination className="justify-content-center mt-3">
                                <Pagination.Prev onClick={() => setPage(p => p - 1)} disabled={page === 1} />
                                {Array.from({ length: totalPages }, (_, i) => (
                                    <Pagination.Item key={i + 1} active={page === i + 1} onClick={() => setPage(i + 1)}>
                                        {i + 1}
                                    </Pagination.Item>
                                ))}
                                <Pagination.Next onClick={() => setPage(p => p + 1)} disabled={page === totalPages} />
                            </Pagination>
                        )}
                    </div>
                )}
            </main>
            <Footer />
        </>
    )
};

export default PatientAppointment;
