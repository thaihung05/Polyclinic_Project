import { useEffect, useRef, useState } from "react";
import { authApis, endpoints } from "../../configs/Api";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import { Badge, Button, Modal, Spinner, Tab, Table, Tabs } from "react-bootstrap";
import Swal from "sweetalert2";
import Moment from "react-moment";


const MedicalHistory = () => {

    const [medicalRecords, setMedicalRecords] = useState([]);
    const [labResults, setLabResults] = useState([]);
    const [prescriptions, setPrescriptions] = useState([]);
    const [paymentData, setPaymentData] = useState({});
    const [selectedRecord, setSelectedRecord] = useState(null);
    const [loading, setLoading] = useState(false);
    const [countdowns, setCountdowns] = useState({});

    const handlePrint = () => {
        const area = document.getElementById('prescription-print-area');
        if (!area) return;
        const win = window.open('','_blank');
        win.document.write(`<html><head><title>Đơn thuốc</title>
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
            </head><body class="p-4">${area.innerHTML}</body></html>
        `);
        win.document.close();
        win.onload = () => { win.print(); win.close(); };
    }

    const formatDateTime = (dateStr) => {
        if (!dateStr) return "-";

        const date = new Date(dateStr);
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
    }

    const loadHistory = async () => {
        try {
            setLoading(true);
            const [recordRes, labRes, prescriptionRes] = await Promise.all([
                authApis().get(endpoints['medical-records']),
                authApis().get(endpoints['lab-results']),
                authApis().get(endpoints['patient-prescriptions'])
            ]);
            setLabResults(labRes.data);
            setMedicalRecords(recordRes.data);
            setPrescriptions(prescriptionRes.data);
        }
        catch (err) {
            Swal.fire("Lỗi", "Không tải được lịch sử khám bệnh", "error");
        }
        finally {
            setLoading(false)
        }
    };

    useEffect(() => {
        loadHistory();
    }, []);

    useEffect(() => {
        const timer = setInterval(() => {
            const now = new Date();
            const updated = {};
            const newlyExpired = [];
            (prescriptions || []).forEach(p => {
                const res = p.prescriptionReservations;
                if (res && !res.isExpired && !p.isPaid && res.expiresAt) {
                    const diff = new Date(res.expiresAt) - now;
                    if (diff > 0) {
                        const h = Math.floor(diff / 3600000);
                        const m = Math.floor((diff % 3600000) / 60000);
                        const s = Math.floor((diff % 60000) / 1000);
                        const fmt = (n) => String(n).padStart(2, '0');
                        updated[p.id] = h > 0
                            ? `${fmt(h)}:${fmt(m)}:${fmt(s)}`
                            : `${fmt(m)}:${fmt(s)}`;
                    } else {
                        newlyExpired.push(p.id);
                    }
                }
            });
            setCountdowns(updated);

            if (newlyExpired.length > 0) {
                setPrescriptions(prev =>
                    prev.map(p =>
                        newlyExpired.includes(p.id)
                            ? {
                                ...p,
                                prescriptionReservations: {
                                    ...p.prescriptionReservations,
                                    isExpired: true
                                }
                            }
                            : p
                    )
                );
            }
        }, 1000);
        return () => clearInterval(timer);
    }, [prescriptions]);


    const createPrescriptionPayment = async (prescriptionId) => {
        try {
            const res = await authApis().post(endpoints['prescription-payment-create'](prescriptionId));
            setPaymentData(prev => ({ ...prev, [prescriptionId]: res.data }));
        } catch (err) {
            Swal.fire("Lỗi", err.response?.data || "Không tạo được thanh toán", "error");
        }
    }

    const confirmPrescriptionPayment = async (prescriptionId) => {
        const confirm = await Swal.fire({
            title: "Xác nhận thanh toán",
            text: "Bạn đã thanh toán đơn thuốc này chưa?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Tôi đã thanh toán",
            cancelButtonText: "Hủy"
        });
        if (confirm.isConfirmed) {
            try {
                await authApis().post(endpoints['prescription-payment-confirm'](prescriptionId));
                Swal.fire("Thành công", "Thanh toán xác nhận thành công. Đến quầy dược để lấy thuốc!", "success");
                setSelectedRecord(null);
                loadHistory();
            }
            catch (err) {
                Swal.fire("Lỗi", err.response?.data || "Không xác nhận được thanh toán", "error");
            }
        }

    }

    const calcTotal = (items) => (items || []).reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);

    const formatCurrency = (amount) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    const selectedPrescriptions = prescriptions.filter(p => p.medicalRecordId?.id === selectedRecord?.id);

    if (loading) {
        return (
            <>
                <Header />
                <div className="text-center py-5"><Spinner animation="border" /></div>
                <Footer />
            </>
        );
    }
    else {
        return (
            <>
                <Header />
                <main className="container my-4">
                    <h4 className="fw-bold mb-4">Lịch sử khám bệnh</h4>
                    <Tabs defaultActiveKey="records" className="mb-3">
                        <Tab eventKey="records" title="Hồ sơ bệnh án">
                            {medicalRecords.length === 0 ? (
                                <div className="text-center text-muted py-5">Chưa có hồ sơ bệnh án nào.</div>
                            ) : (
                                <Table bordered hover responsive>
                                    <thead className="table-light">
                                        <tr>
                                            <th>Số</th>
                                            <th>Bác sĩ</th>
                                            <th>Chuyên khoa</th>
                                            <th>Ngày khám</th>
                                            <th>Triệu chứng chính</th>
                                            <th>Chẩn đoán</th>
                                            <th>Kế hoạch điều trị</th>
                                            <th>Ngày tái khám</th>
                                            <th>Ghi chú</th>
                                            <th>Đơn thuốc</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {(medicalRecords || []).map((rec, idx) => {
                                            const hasPrescription = prescriptions.some(p => p.medicalRecordId?.id === rec.id);
                                            return (
                                                <tr key={rec.id}>
                                                    <td>{idx + 1}</td>
                                                    <td>{rec.appointmentId?.doctorId?.userId?.name || "—"}</td>
                                                    <td>{rec.appointmentId?.doctorId?.specialtyId?.name || "—"}</td>
                                                    <td>{formatDateTime(rec.appointmentId?.scheduledAt) || "—"}</td>
                                                    <td>{rec.chiefComplaint || "—"}</td>
                                                    <td>{rec.diagnosis || "—"}</td>
                                                    <td>{rec.treatmentPlan}</td>
                                                    <td>{formatDateTime(rec.followUpDate) || "—"}</td>
                                                    <td>{rec.notes || "—"}</td>
                                                    <td className="text-center">
                                                        {hasPrescription ? (
                                                            <Button variant="outline-primary" size="sm"
                                                                onClick={() => setSelectedRecord(rec)}>
                                                                Xem đơn thuốc
                                                            </Button>
                                                        ) : (
                                                            <span className="text-muted small">Không có</span>
                                                        )}
                                                    </td>
                                                </tr>
                                            )
                                        }
                                        )}
                                    </tbody>
                                </Table>
                            )}
                        </Tab>
                        <Tab eventKey="lab" title="Hồ sơ xét nghiệm">
                            {labResults.length === 0 ? (
                                <div className="text-center text-muted py-5">Chưa có hồ sơ xét nghiệm nào.</div>
                            ) : (
                                <Table bordered hover responsive>
                                    <thead className="table-light">
                                        <tr>
                                            <th>Số</th>
                                            <th>Tên xét nghiệm</th>
                                            <th>Mã xét nghiệm</th>
                                            <th>Kết quả</th>
                                            <th>Đơn vị</th>
                                            <th>Ngày thực hiện</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {(labResults || []).map((lab, idx) => (
                                            <tr key={lab.id}>
                                                <td>{idx + 1}</td>
                                                <td>{lab.testName || "-"}</td>
                                                <td>{lab.testCode || "—"}</td>
                                                <td>{lab.result || "—"}</td>
                                                <td>{lab.unit || "—"}</td>
                                                <td>{formatDateTime(lab.performedAt) || "—"}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </Tab>
                    </Tabs>
                </main>
                <Footer />

                <Modal show={selectedRecord != null} onHide={() => setSelectedRecord(null)} size="lg" centered>
                    <Modal.Header closeButton>
                        <Modal.Title>
                            Đơn thuốc - {selectedRecord?.appointmentId?.doctorId?.userId?.name || 'Bác sĩ'}
                            <span className="text-muted fs-6 ms-2">
                                <Moment format="DD/MM/YYYY">
                                    {selectedRecord?.appointmentId?.scheduledAt}
                                </Moment>
                            </span>
                        </Modal.Title>
                    </Modal.Header>

                    <Modal.Body id="prescription-print-area">
                        {selectedPrescriptions.length === 0 ? (
                            <p className="text-muted mb-0">Hồ sơ này không có đơn thuốc.</p>
                        ) : (
                            selectedPrescriptions.map((p, idx) => (
                                <div key={p.id} className={idx > 0 ? "mt-3 pt-3 border-top" : ""}>
                                    <div className="d-flex justify-content-between align-items-center mb-2">
                                        <span className="fw-semibold">
                                            Đơn thuốc #{idx + 1} - <Moment format="DD/MM/YYYY">{p.ngayTao}</Moment>
                                        </span>
                                        {p.prescriptionReservations?.isExpired
                                            ? <Badge bg="danger">Đã hết hạn</Badge>
                                            : p.isPaid && p.isDispensed
                                                ? <Badge bg="primary">Đã cấp phát</Badge>
                                                : p.isPaid
                                                    ? <Badge bg="success">Đã thanh toán — Chờ cấp phát</Badge>
                                                    : countdowns[p.id]
                                                        ? <Badge bg="warning" text="dark">Còn {countdowns[p.id]}</Badge>
                                                        : <Badge bg="warning" text="dark">Chưa thanh toán</Badge>
                                        }
                                    </div>


                                    {p.note && <p className="text-muted small mb-2">Ghi chú bác sĩ: {p.note}</p>}
                                    <Table bordered size="sm" className="mb-2">
                                        <thead className="table-light">
                                            <tr>
                                                <th>Tên thuốc</th>
                                                <th>Số lượng</th>
                                                <th>Đơn giá</th>
                                                <th>Thành tiền</th>
                                                {p.isPaid && <><th>Liều dùng</th><th>Số ngày</th><th>Hướng dẫn</th></>}
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {(p.prescriptionItemsCollection || []).map(item => (
                                                <tr key={item.id}>
                                                    <td>{item.medicineId?.name || "—"}</td>
                                                    <td>{item.quantity}</td>
                                                    <td>{formatCurrency(item.unitPrice)}</td>
                                                    <td>{formatCurrency(item.unitPrice * item.quantity)}</td>
                                                    {p.isPaid && (
                                                        <>
                                                            <td>{item.dosage}</td>
                                                            <td>{item.durationDays} ngày</td>
                                                            <td>{item.instructions || '-'}</td>
                                                        </>
                                                    )}

                                                </tr>
                                            ))}
                                        </tbody>
                                        <tfoot>
                                            <tr>
                                                <td colSpan={5} className="text-end fw-bold">Tổng cộng:</td>
                                                <td className="fw-bold">{formatCurrency(calcTotal(p.prescriptionItemsCollection))}</td>
                                                {p.isPaid && <td colSpan={3}></td>}
                                            </tr>
                                        </tfoot>
                                    </Table>

                                    {!p.isPaid && !p.prescriptionReservations?.isExpired && (
                                        <div className="mt-2">
                                            {!paymentData[p.id] ? (
                                                <Button variant="primary" size="sm" onClick={() => createPrescriptionPayment(p.id)}>
                                                    Tạo QR thanh toán
                                                </Button>
                                            ) : (
                                                <div className="d-flex flex-column align-items-center gap-2">
                                                    <p className="mb-1 text-muted small">Quét mã QR để thanh toán:</p>
                                                    <img src={paymentData[p.id].qrUrl} alt="QR thanh toán" style={{ width: 180, height: 180, border: '1px solid #ddd' }} />
                                                    <Button variant="success" size="sm" onClick={() => confirmPrescriptionPayment(p.id)}>
                                                        Xác nhận thanh toán
                                                    </Button>
                                                </div>
                                            )}
                                        </div>
                                    )}
                                    {p.prescriptionReservations?.isExpired && (
                                        <div className="mt-2 text-danger small">
                                            <i className="bi bi-exclamation-circle me-1"></i>
                                            Đơn thuốc đã hết hạn. Vui lòng liên hệ bác sĩ để kê đơn mới.
                                        </div>
                                    )}
                                </div>


                            ))
                        )}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="outline-primary" onClick={handlePrint}>
                            <i className="bi bi-printer me-1"></i> In đơn thuốc
                        </Button>
                        <Button variant="secondary" onClick={() => setSelectedRecord(null)}>Đóng</Button>
                    </Modal.Footer>
                </Modal>
            </>
        );
    }

};

export default MedicalHistory;