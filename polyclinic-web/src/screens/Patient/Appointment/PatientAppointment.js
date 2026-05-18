import { useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import Swal from "sweetalert2";
import { Badge, Spinner, Table } from "react-bootstrap";
import Header from "../../../components/Header";
import Footer from "../../../components/Footer";


const PatientAppointment = () => {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(false);

    const loadAppointments = async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoints["patient-appointments"]);
            setAppointments(res.data);
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

    const formatDateTime = (dateStr) => {
        if (!dateStr) return "—";
        const date = new Date(dateStr);
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        return `${day}-${month}-${year} ${hours}:${minutes}`;
    };

    return (
        <>
            <Header />
            <main lassName="container my-4">
                <h4 className="fw-bold mt-4 text-center">Lịch hẹn của tôi</h4>
                {loading ? (
                    <div className="text-center py-5">
                        <Spinner animation="border" />
                    </div>
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
                                    <th>Ghi chú</th>
                                    <th>Link khám online</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                {appointments.map((appointment, idx) => (
                                    <tr key={appointment.id}>
                                        <td>{idx + 1}</td>
                                        <td>{appointment.doctorId?.userId?.name || ""}</td>
                                        <td>{appointment.doctorId?.specialtyId?.name || ""}</td>
                                        <td>{formatDateTime(appointment.scheduledAt)}</td>
                                        <td>{appointment.symptoms || ""}</td>
                                        <td>{appointment.cancelReason || ""}</td>
                                        <td>
                                            {appointment.meetingUrl ? <a href={appointment.meetingUrl} target="_blank" rel="noreferrer">Tham gia</a> : ""}
                                        </td>
                                        <td>{renderStatus(appointment.status)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </div>
                )}

            </main>
            <Footer />
        </>
    )
};

export default PatientAppointment;