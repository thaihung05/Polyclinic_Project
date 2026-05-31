import { useCallback, useEffect, useState } from "react";
import { Badge, Button, Spinner, Table } from "react-bootstrap";
import Swal from "sweetalert2";
import Moment from "react-moment";
import { authApis, endpoints } from "../../../configs/Api";
import MySpinner from "../../../components/MySpinner";

const PAGE_SIZE = 10;

const PrescriptionDispensing = () => {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [dispensing, setDispensing] = useState(null);
    const [page, setPage] = useState(1);

    const loadReservations = useCallback(async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoints['pharmacist-prescriptions']);
            setReservations(res.data || []);
        } catch (err) {
            Swal.fire({ icon: "error", title: "Lỗi tải danh sách đơn thuốc!", showConfirmButton: false, timer: 1000 });
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => { loadReservations(); }, [loadReservations]);

    const dispense = async (prescriptionId) => {
        const confirm = await Swal.fire({
            title: "Xác nhận cấp phát",
            text: "Bạn đã chuẩn bị đủ thuốc và sẵn sàng cấp cho bệnh nhân?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Xác nhận cấp phát",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#198754"
        });
        if (!confirm.isConfirmed) return;

        try {
            setDispensing(prescriptionId);
            await authApis().post(endpoints['dispense-prescription'](prescriptionId));
            Swal.fire({ icon: "success", title: "Cấp phát thành công!", showConfirmButton: false, timer: 1500 });
            loadReservations();
        } catch (err) {
            Swal.fire({ icon: "error", title: "Cấp phát thất bại!", text: err.response?.data || "Đã xảy ra lỗi!" });
        } finally {
            setDispensing(null);
        }
    };

    const displayed = (reservations || []).slice(0, page * PAGE_SIZE);

    if (loading)
        return <div className="text-center py-5"><MySpinner /></div>;

    return (
        <div className="container-fluid py-3">
            <h4 className="mb-3">
                <i className="bi bi-file-earmark-medical me-2"></i>
                Đơn thuốc chờ cấp phát
                <Badge bg="warning" text="dark" className="ms-2">{reservations.length}</Badge>
            </h4>

            {reservations.length === 0 ? (
                <div className="alert alert-info">Không có đơn thuốc nào đang chờ cấp phát.</div>
            ) : (
                <>
                    <Table bordered hover responsive>
                        <thead className="table-light">
                            <tr>
                                <th>STT</th>
                                <th>Bệnh nhân</th>
                                <th>Bác sĩ kê</th>
                                <th>Ngày kê</th>
                                <th>Thuốc</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {displayed.map((r, idx) => {
                                const don = r.prescriptionId;
                                const benhNhan = r.patientId?.userId;
                                const bacSi = r.doctorId?.userId;
                                const items = don?.prescriptionItemsCollection || [];
                                return (
                                    <tr key={r.id}>
                                        <td>{idx + 1}</td>
                                        <td>
                                            <strong>{benhNhan?.name || "—"}</strong>
                                            <br /><small className="text-muted">{benhNhan?.phone}</small>
                                        </td>
                                        <td>{bacSi?.name || "—"}</td>
                                        <td>
                                            <Moment format="DD/MM/YYYY HH:mm">{don?.ngayTao}</Moment>
                                        </td>
                                        <td>
                                            {(items || []).map((item, i) => (
                                                <div key={i} className="small">
                                                    <span className="fw-semibold">{item.medicineId?.name}</span>
                                                    {" — "}{item.quantity} {item.medicineId?.unit}
                                                    <span className="text-muted"> ({item.dosage})</span>
                                                </div>
                                            ))}
                                            {don?.note && <div className="text-muted small mt-1">Ghi chú: {don.note}</div>}
                                        </td>
                                        <td>
                                            <Button
                                                variant="success"
                                                size="sm"
                                                disabled={dispensing === don?.id}
                                                onClick={() => dispense(don?.id)}
                                            >
                                                {dispensing === don?.id
                                                    ? <><Spinner animation="border" size="sm" className="me-1" />Đang cấp phát...</>
                                                    : <><i className="bi bi-check-circle me-1"></i>Cấp phát</>
                                                }
                                            </Button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </Table>
                    {reservations.length > displayed.length && (
                        <div className="text-center mt-2">
                            <Button variant="outline-primary" size="sm" onClick={() => setPage(p => p + 1)}>
                                Xem thêm
                            </Button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default PrescriptionDispensing;
