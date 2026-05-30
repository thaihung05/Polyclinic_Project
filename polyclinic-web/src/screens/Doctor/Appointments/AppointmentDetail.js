import { useCallback, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Alert, Badge, Button, Card, Col, Container, Form, Modal, Nav, Row, Spinner, Tab, Table } from "react-bootstrap";
import Swal from "sweetalert2";
import Apis, { authApis, endpoints } from "../../../configs/Api";

const statusVariant = (s) => ({
    PENDING: "warning",
    CONFIRMED: "primary",
    COMPLETED: "success",
    CANCELLED: "danger",
    NO_SHOW: "secondary",
}[s]);

const statusLabel = (s) => ({
    PENDING: "Chờ xác nhận",
    CONFIRMED: "Đã xác nhận",
    COMPLETED: "Hoàn thành",
    CANCELLED: "Đã hủy",
    NO_SHOW: "Vắng khám",
}[s]);

const AppointmentDetail = () => {
    const { appointmentId } = useParams();
    const navigate = useNavigate();

    const [appt, setAppt] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [record, setRecord] = useState(null);
    const [recordLoading, setRecordLoading] = useState(true);
    const [recordForm, setRecordForm] = useState({
        chiefComplaint: "", 
        diagnosis: "", 
        treatmentPlan: "", 
        followUpDate: "", 
        notes: ""
    });
    const [savingRecord, setSavingRecord] = useState(false);

    const [prescriptions, setPrescriptions] = useState([]);
    const [medicines, setMedicines] = useState([]);
    const [showPrescModal, setShowPrescModal] = useState(false);
    const [prescNote, setPrescNote] = useState("");
    const [prescItems, setPrescItems] = useState([
        { 
            medicineId: "", 
            quantity: 1, dosage: "", 
            durationDays: 1, 
            instructions: "", 
            unitPrice: 0 }
    ]);
    const [savingPresc, setSavingPresc] = useState(false);

    const [labResults, setLabResults] = useState([]);
    const [showLabModal, setShowLabModal] = useState(false);
    const [editingLab, setEditingLab] = useState(null);
    const [labForm, setLabForm] = useState({
        testName: "", 
        testCode: "", 
        result: "", 
        unit: "", 
        isAbnormal: false, 
        performedAt: ""
    });
    const [savingLab, setSavingLab] = useState(false);

    const fetchAppt = useCallback(async () => {
        try {
            setLoading(true);
            const res = await Apis.get(endpoints["appointment-detail"](appointmentId));
            setAppt(res.data);
        } catch (err) {
            setError("Không tìm thấy cuộc hẹn.");
        } finally {
            setLoading(false);
        }
    }, [appointmentId]);

    const fetchRecord = useCallback(async () => {
        try {
            setRecordLoading(true);
            const res = await Apis.get(endpoints["appointment-medical-record"](appointmentId));
            setRecord(res.data);
            setRecordForm({
                chiefComplaint: res.data.chiefComplaint || "",
                diagnosis: res.data.diagnosis || "",
                treatmentPlan: res.data.treatmentPlan || "",
                followUpDate: new Date(res.data.followUpDate).toISOString().slice(0, 16),
                notes: res.data.notes || ""
            });
        } catch {

        } finally {
            setRecordLoading(false);
        }
    }, [appointmentId]);

    const fetchLabResults = useCallback(async () => {
        try {
            const res = await Apis.get(endpoints["lab-results-by-appointment"](appointmentId));
            setLabResults(res.data);
        } catch {
            setLabResults([]);
        }
    }, [appointmentId]);

    const fetchMedicines = async () => {
        try {
            const res = await Apis.get(endpoints["medicines"]);
            setMedicines(res.data);
        } catch { 
            setMedicines([]);
        }
    };

    const fetchPrescriptions = async (recordId) => {
        try {
            const res = await Apis.get(endpoints["prescriptions-by-record"](recordId));
            setPrescriptions(res.data);
        } catch { 
            setPrescriptions([]); 
        }
    };

    useEffect(() => {
        fetchAppt();
        fetchRecord();
        fetchLabResults();
    }, [fetchAppt, fetchRecord, fetchLabResults]);

    useEffect(() => { 
        fetchMedicines(); 
    }, []);

    useEffect(() => {
        if (record?.id) 
            fetchPrescriptions(record.id);
    }, [record]);

    const openLabModal = (lab = null) => {
        setEditingLab(lab);
        setLabForm(lab ? {
            testName: lab.testName,
            testCode: lab.testCode,
            result: lab.result,
            unit: lab.unit,
            isAbnormal: lab.isAbnormal || false,
            performedAt: lab.performedAt ? new Date(lab.performedAt).toISOString().slice(0, 16) : ""
        } : { 
            testName: "", 
            testCode: "", 
            result: "", unit: "", 
            isAbnormal: false, 
            performedAt: "" });
        setShowLabModal(true);
    };

    const handleSaveLab = async () => {
        if (!labForm.testName.trim() || !labForm.result.trim()) {
            Swal.fire("Thiếu thông tin", "Vui lòng nhập tên xét nghiệm và kết quả", "warning");
            return;
        }
        setSavingLab(true);
        try {
            const payload = {
                testName: labForm.testName,
                testCode: labForm.testCode || null,
                result: labForm.result,
                unit: labForm.unit || null,
                isAbnormal: labForm.isAbnormal,
                performedAt: labForm.performedAt || null
            };
            if (editingLab) {
                await authApis().put(endpoints["update-lab-result"](editingLab.id), payload);
                Swal.fire({ 
                    icon: "success", 
                    title: "Đã cập nhật kết quả xét nghiệm", 
                    timer: 1500, 
                    showConfirmButton: false });
            } else {
                await authApis().post(endpoints["create-lab-result"](appointmentId), payload);
                Swal.fire({ 
                    icon: "success", 
                    title: "Đã thêm kết quả xét nghiệm", 
                    timer: 1500, 
                    showConfirmButton: false });
            }
            setShowLabModal(false);
            fetchLabResults();
        } catch (err) {
            Swal.fire("Lỗi", err.response?.data || "Không thể lưu kết quả xét nghiệm", "error");
        } finally {
            setSavingLab(false);
        }
    };

    const addPrescItem = () => setPrescItems(prev => [...prev,
        { 
            medicineId: "", 
            quantity: 1, 
            dosage: "", 
            durationDays: 1, 
            instructions: "", 
            unitPrice: 0 }
    ]);

    const removePrescItem = (idx) => setPrescItems(prev => prev.filter((_, i) => i !== idx));

    const updatePrescItem = (idx, field, value) => {
        setPrescItems(prev => {
            const updated = [...prev];
            updated[idx] = { ...updated[idx], [field]: value };
            if (field === "medicineId") {
                const med = medicines.find(m => String(m.id) === String(value));
                if (med) updated[idx].unitPrice = med.price || 0;
            }
            return updated;
        });
    };

    const handleSavePresc = async () => {
        if (!record) {
            Swal.fire("Chú ý", "Vui lòng tạo hồ sơ bệnh án trước khi kê đơn", "warning");
            return;
        }
        const invalid = prescItems.find(it => !it.medicineId || !it.dosage.trim());
        if (invalid) {
            Swal.fire("Thiếu thông tin", "Vui lòng chọn thuốc và nhập liều lượng", "warning");
            return;
        }
        const overStock = prescItems.find(it => {
            const med = medicines.find(m => String(m.id) === String(it.medicineId));
            return med && Number(it.quantity) > med.stockQuantity;
        });
        if (overStock) {
            const med = medicines.find(m => String(m.id) === String(overStock.medicineId));
            Swal.fire("Vượt tồn kho", `Thuốc "${med.name}" chỉ còn ${med.stockQuantity} ${med.unit}, không thể kê ${overStock.quantity}`, "error");
            return;
        }
        setSavingPresc(true);
        try {
            const payload = {
                note: prescNote,
                prescriptionItemsCollection: prescItems.map(it => ({
                    medicineId: { id: Number(it.medicineId) },
                    quantity: Number(it.quantity),
                    dosage: it.dosage,
                    durationDays: Number(it.durationDays),
                    instructions: it.instructions,
                    unitPrice: Number(it.unitPrice)
                }))
            };
            await authApis().post(endpoints["create-prescription"](record.id), payload);
            Swal.fire({ icon: "success", title: "Đã kê đơn thuốc", timer: 1500, showConfirmButton: false });
            setShowPrescModal(false);
            setPrescNote("");
            setPrescItems([{ 
                medicineId: "", 
                quantity: 1, 
                dosage: "", 
                durationDays: 1, 
                instructions: "", 
                unitPrice: 0 }]);
            fetchPrescriptions(record.id);
            fetchMedicines();
        } catch (err) {
            Swal.fire("Lỗi", err.response?.data || "Không thể lưu đơn thuốc", "error");
        } finally {
            setSavingPresc(false);
        }
    };

    const handleSaveRecord = async (e) => {
        e.preventDefault();
        if (!recordForm.chiefComplaint.trim() || !recordForm.diagnosis.trim()) {
            Swal.fire("Thiếu thông tin", "Vui lòng nhập lý do khám và chẩn đoán", "warning");
            return;
        }
        setSavingRecord(true);
        try {
            const payload = {
                ...recordForm,
                followUpDate: recordForm.followUpDate.replace('T', ' ') + ':00'
            };
            if (record) {
                const res = await authApis().put(endpoints["update-medical-record"](record.id), payload);
                setRecord(res.data);
                Swal.fire({ 
                    icon: "success", 
                    title: "Đã cập nhật hồ sơ bệnh án", 
                    timer: 1500, 
                    showConfirmButton: false });
            } else {
                const res = await authApis().post(endpoints["create-medical-record"](appointmentId), payload);
                setRecord(res.data);
                Swal.fire({ 
                    icon: "success", 
                    title: "Đã tạo hồ sơ bệnh án", 
                    timer: 1500, 
                    showConfirmButton: false });
            }
        } catch (err) {
            console.error(err);
            Swal.fire("Lỗi", err.response?.data || "Không thể lưu hồ sơ bệnh án", "error");
        } finally {
            setSavingRecord(false);
        }
    };

    if (loading) return (
        <div className="text-center mt-5">
            <Spinner animation="border" variant="primary" />
            <p className="mt-2">Đang tải...</p>
        </div>
    );

    if (error) return (
        <Container className="mt-4">
            <Alert variant="danger">{error}</Alert>
            <Button variant="secondary" onClick={() => navigate(-1)}>Quay lại</Button>
        </Container>
    );

    if (!appt) return null;

    return (
        <Container fluid className="mt-3">
            <div className="d-flex align-items-center gap-3 mb-4">
                <Button variant="outline-secondary" size="sm" onClick={() => navigate(-1)}>
                    Quay lại
                </Button>
                <h4 className="mb-0 fw-bold">
                    Chi tiết cuộc hẹn #{appt.id}{" "}
                    <Badge bg={statusVariant(appt.status)}>{statusLabel(appt.status)}</Badge>
                </h4>
            </div>

            <Tab.Container defaultActiveKey="info">
                <Nav variant="tabs" className="mb-3">
                    <Nav.Item>
                        <Nav.Link eventKey="info">Thông tin hẹn</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey="record">
                            Hồ sơ bệnh án {record && <i class="bi bi-bookmark-check-fill"></i>}
                        </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey="presc">
                            Kê đơn thuốc {prescriptions.length > 0 && <Badge bg="primary" pill>{prescriptions.length}</Badge>}
                        </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey="lab">
                            Kết quả xét nghiệm {labResults.length > 0 && <Badge bg="primary" pill>{labResults.length}</Badge>}
                        </Nav.Link>
                    </Nav.Item>
                </Nav>

                <Tab.Content>
                    <Tab.Pane eventKey="info">
                        <Card>
                            <Card.Body>
                                <Row className="gy-3">
                                    <Col md={6}>
                                        <p className="mb-1 text-muted small">Bệnh nhân</p>
                                        <p className="fw-semibold">{appt.patientId?.userId?.name || "—"}</p>
                                    </Col>
                                    <Col md={6}>
                                        <p className="mb-1 text-muted small">Bác sĩ</p>
                                        <p className="fw-semibold">{appt.doctorId?.userId?.name || "—"}</p>
                                    </Col>
                                    <Col md={6}>
                                        <p className="mb-1 text-muted small">Thời gian hẹn</p>
                                        <p className="fw-semibold">
                                            {appt.scheduledAt ? new Date(appt.scheduledAt).toLocaleString("vi-VN") : "—"}
                                        </p>
                                    </Col>
                                    <Col md={6}>
                                        <p className="mb-1 text-muted small">Chuyên khoa</p>
                                        <p className="fw-semibold">{appt.doctorId?.specialtyId?.name || "—"}</p>
                                    </Col>
                                    <Col md={12}>
                                        <p className="mb-1 text-muted small">Triệu chứng</p>
                                        <p>{appt.symptoms || "—"}</p>
                                    </Col>
                                    {appt.meetingUrl && (
                                        <Col md={12}>
                                            <p className="mb-1 text-muted small">Khám online</p>
                                            <a href={appt.meetingUrl} target="_blank" rel="noreferrer"
                                               className="btn btn-success btn-sm">
                                                <i className="bi bi-camera-video-fill me-1"></i>Vào phòng khám
                                            </a>
                                        </Col>
                                    )}
                                    {appt.cancelReason && (
                                        <Col md={12}>
                                            <p className="mb-1 text-muted small">Lý do hủy</p>
                                            <p className="text-danger">{appt.cancelReason}</p>
                                        </Col>
                                    )}
                                </Row>
                            </Card.Body>
                        </Card>
                    </Tab.Pane>

                    <Tab.Pane eventKey="record">
                        <Card>
                            <Card.Header className="fw-semibold">
                                {record ? "Cập nhật hồ sơ bệnh án" : "Tạo hồ sơ bệnh án"}
                            </Card.Header>
                            <Card.Body>
                                {recordLoading ? (
                                    <div className="text-center py-3">
                                        <Spinner animation="border" size="sm" /> Đang tải hồ sơ...
                                    </div>
                                ) : (
                                    <Form onSubmit={handleSaveRecord}>
                                        <Row>
                                            <Col md={6}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Lý do khám <span className="text-danger">*</span></Form.Label>
                                                    <Form.Control
                                                        as="textarea" 
                                                        rows={3}
                                                        value={recordForm.chiefComplaint}
                                                        onChange={e => 
                                                            setRecordForm(p => ({ 
                                                                ...p, 
                                                                chiefComplaint: e.target.value 
                                                            }))
                                                        }
                                                        placeholder="Bệnh nhân đến khám vì..."
                                                    />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Chẩn đoán <span className="text-danger">*</span></Form.Label>
                                                    <Form.Control
                                                        as="textarea" rows={3}
                                                        value={recordForm.diagnosis}
                                                        onChange={e => setRecordForm(p => ({ ...p, diagnosis: e.target.value }))}
                                                        placeholder="Kết quả chẩn đoán..."
                                                    />
                                                </Form.Group>
                                            </Col>
                                            <Col md={12}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Phác đồ điều trị</Form.Label>
                                                    <Form.Control
                                                        as="textarea" rows={2}
                                                        value={recordForm.treatmentPlan}
                                                        onChange={e => setRecordForm(p => ({ ...p, treatmentPlan: e.target.value }))}
                                                        placeholder="Hướng điều trị, thuốc sử dụng..."
                                                    />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Ngày tái khám</Form.Label>
                                                    <Form.Control
                                                        type="datetime-local"
                                                        value={recordForm.followUpDate}
                                                        onChange={e => setRecordForm(p => ({ ...p, followUpDate: e.target.value }))}
                                                    />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Ghi chú thêm</Form.Label>
                                                    <Form.Control
                                                        as="textarea" rows={2}
                                                        value={recordForm.notes}
                                                        onChange={e => setRecordForm(p => ({ ...p, notes: e.target.value }))}
                                                        placeholder="Lưu ý đặc biệt..."
                                                    />
                                                </Form.Group>
                                            </Col>
                                        </Row>
                                        <Button type="submit" variant="primary" disabled={savingRecord}>
                                            {savingRecord ? <><Spinner size="sm" animation="border" className="me-2" />Đang lưu...</>
                                                : (record ? "Cập nhật hồ sơ" : "Tạo hồ sơ")}
                                        </Button>
                                    </Form>
                                )}
                            </Card.Body>
                        </Card>
                    </Tab.Pane>
                    <Tab.Pane eventKey="presc">
                        <Card>
                            <Card.Header className="d-flex justify-content-between align-items-center">
                                <span className="fw-semibold">Danh sách đơn thuốc</span>
                                <Button size="sm" variant="success" onClick={() => setShowPrescModal(true)}>
                                    + Kê đơn mới
                                </Button>
                            </Card.Header>
                            <Card.Body>
                                {!record && (
                                    <Alert variant="warning">Vui lòng tạo hồ sơ bệnh án trước khi kê đơn thuốc.</Alert>
                                )}
                                {record && prescriptions.length === 0 && (
                                    <Alert variant="info" className="mb-0">Chưa có đơn thuốc nào.</Alert>
                                )}
                                {record && prescriptions.length > 0 && (
                                    prescriptions.map((presc, pi) => (
                                        <Card key={presc.id} className="mb-3 border-primary">
                                            <Card.Header className="bg-light">
                                                <strong>Đơn #{pi + 1}</strong>
                                                {presc.ngayTao && (
                                                    <span className="text-muted ms-2 small">
                                                        — {new Date(presc.ngayTao).toLocaleString("vi-VN")}
                                                    </span>
                                                )}
                                                {presc.note && <span className="ms-2 fst-italic text-muted">({presc.note})</span>}
                                            </Card.Header>
                                            <Table size="sm" responsive className="mb-0">
                                                <thead className="table-light">
                                                    <tr>
                                                        <th>#</th><th>Tên thuốc</th><th>Liều dùng</th>
                                                        <th>Số ngày</th><th>Số lượng</th><th>Hướng dẫn</th><th>Đơn giá</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {(presc.prescriptionItemsCollection || []).map((item, ii) => (
                                                        <tr key={item.id || ii}>
                                                            <td>{ii + 1}</td>
                                                            <td>{item.medicineId?.name || "—"}</td>
                                                            <td>{item.dosage}</td>
                                                            <td>{item.durationDays} ngày</td>
                                                            <td>{item.quantity} {item.medicineId?.unit || ""}</td>
                                                            <td>{item.instructions || "—"}</td>
                                                            <td>{Number(item.unitPrice || 0).toLocaleString("vi-VN")}đ</td>
                                                        </tr>
                                                    ))}
                                                </tbody>
                                            </Table>
                                        </Card>
                                    ))
                                )}
                            </Card.Body>
                        </Card>

                        <Modal show={showPrescModal} onHide={() => setShowPrescModal(false)} size="xl">
                            <Modal.Header closeButton>
                                <Modal.Title>Kê đơn thuốc mới</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ghi chú đơn thuốc</Form.Label>
                                    <Form.Control
                                        as="textarea" rows={1}
                                        value={prescNote}
                                        onChange={e => setPrescNote(e.target.value)}
                                        placeholder="VD: Uống đúng giờ, tái khám sau 1 tuần..."
                                    />
                                </Form.Group>
                                <Table bordered responsive size="sm">
                                    <thead className="table-light">
                                        <tr>
                                            <th style={{ minWidth: 200 }}>Thuốc *</th>
                                            <th style={{ minWidth: 160 }}>Liều dùng *</th>
                                            <th style={{ width: 80 }}>Số ngày</th>
                                            <th style={{ width: 200 }}>Số lượng</th>
                                            <th style={{ minWidth: 160 }}>Hướng dẫn</th>
                                            <th style={{ width: 120 }}>Đơn giá (đ)</th>
                                            <th style={{ width: 130 }}>Thành tiền (đ)</th>
                                            <th style={{ width: 40 }}></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {prescItems.map((item, idx) => (
                                            <tr key={idx}>
                                                <td>
                                                    <Form.Select
                                                        value={item.medicineId}
                                                        onChange={e => updatePrescItem(idx, "medicineId", e.target.value)}
                                                    >
                                                        <option value="">-- Chọn thuốc --</option>
                                                        {medicines.map(m => (
                                                            <option key={m.id} value={m.id}>
                                                                {m.name} ({m.unit}) — Tồn: {m.stockQuantity}
                                                            </option>
                                                        ))}
                                                    </Form.Select>
                                                </td>
                                                <td>
                                                    <Form.Control
                                                        placeholder="VD: 1 viên x 2 lần/ngày"
                                                        value={item.dosage}
                                                        onChange={e => updatePrescItem(idx, "dosage", e.target.value)}
                                                    />
                                                </td>
                                                <td>
                                                    <Form.Control type="number" min={1}
                                                        value={item.durationDays}
                                                        onChange={e => updatePrescItem(idx, "durationDays", e.target.value)}
                                                    />
                                                </td>
                                                <td>
                                                    <Form.Control type="number" min={1}
                                                        max={medicines.find(m => String(m.id) === String(item.medicineId))?.stockQuantity || undefined}
                                                        value={item.quantity}
                                                        onChange={e => updatePrescItem(idx, "quantity", e.target.value)}
                                                        isInvalid={
                                                            item.medicineId &&
                                                            Number(item.quantity) > (medicines.find(m => String(m.id) === String(item.medicineId))?.stockQuantity || Infinity)
                                                        }
                                                    />
                                                </td>
                                                <td>
                                                    <Form.Control
                                                        placeholder="VD: Uống sau ăn"
                                                        value={item.instructions}
                                                        onChange={e => updatePrescItem(idx, "instructions", e.target.value)}
                                                    />
                                                </td>
                                                <td className="align-middle text-end">
                                                    {Number(item.unitPrice || 0).toLocaleString("vi-VN")}đ
                                                </td>
                                                <td className="align-middle text-end fw-semibold text-primary">
                                                    {(Number(item.unitPrice || 0) * Number(item.quantity || 0)).toLocaleString("vi-VN")}đ
                                                </td>
                                                <td className="text-center">
                                                    {prescItems.length > 1 && (
                                                        <Button variant="outline-danger" size="sm"
                                                            onClick={() => removePrescItem(idx)}>✕</Button>
                                                    )}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                                <Button variant="outline-secondary" size="sm" onClick={addPrescItem}>
                                    + Thêm thuốc
                                </Button>
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => setShowPrescModal(false)}>Huỷ</Button>
                                <Button variant="primary" onClick={handleSavePresc} disabled={savingPresc}>
                                    {savingPresc
                                        ? <><Spinner size="sm" animation="border" className="me-2" />Đang lưu...</>
                                        : "Lưu đơn thuốc"}
                                </Button>
                            </Modal.Footer>
                        </Modal>
                    </Tab.Pane>

                    <Tab.Pane eventKey="lab">
                        <Card>
                            <Card.Header className="d-flex justify-content-between align-items-center">
                                <span className="fw-semibold">Danh sách kết quả xét nghiệm</span>
                                <Button size="sm" variant="success" onClick={() => openLabModal()}>
                                    + Thêm xét nghiệm
                                </Button>
                            </Card.Header>
                            <Card.Body>
                                {labResults.length === 0 && (
                                    <Alert variant="info" className="mb-0">Chưa có kết quả xét nghiệm nào.</Alert>
                                )}
                                {labResults.length > 0 && (
                                    <Table bordered responsive size="sm">
                                        <thead className="table-light">
                                            <tr>
                                                <th>#</th>
                                                <th>Tên xét nghiệm</th>
                                                <th>Mã XN</th>
                                                <th>Kết quả</th>
                                                <th>Đơn vị</th>
                                                <th>Đánh giá</th>
                                                <th>Thời gian TH</th>
                                                <th></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {labResults.map((lab, i) => (
                                                <tr key={lab.id}>
                                                    <td>{i + 1}</td>
                                                    <td>{lab.testName}</td>
                                                    <td>{lab.testCode || "—"}</td>
                                                    <td>{lab.result}</td>
                                                    <td>{lab.unit || "—"}</td>
                                                    <td>
                                                        {lab.isAbnormal
                                                            ? <Badge bg="danger">Bất thường</Badge>
                                                            : <Badge bg="success">Bình thường</Badge>}
                                                    </td>
                                                    <td>
                                                        {lab.performedAt
                                                            ? new Date(lab.performedAt).toLocaleString("vi-VN")
                                                            : "—"}
                                                    </td>
                                                    <td>
                                                        <Button variant="outline-primary" size="sm" onClick={() => openLabModal(lab)}>
                                                            Sửa
                                                        </Button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </Table>
                                )}
                            </Card.Body>
                        </Card>

                        <Modal show={showLabModal} onHide={() => setShowLabModal(false)}>
                            <Modal.Header closeButton>
                                <Modal.Title>{editingLab ? "Sửa kết quả xét nghiệm" : "Thêm kết quả xét nghiệm"}</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                <Form.Group className="mb-3">
                                    <Form.Label>Tên xét nghiệm <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        value={labForm.testName}
                                        onChange={e => setLabForm(p => ({ ...p, testName: e.target.value }))}
                                        placeholder="VD: Xét nghiệm máu tổng quát"
                                    />
                                </Form.Group>
                                <Row>
                                    <Col>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Mã xét nghiệm</Form.Label>
                                            <Form.Control
                                                value={labForm.testCode}
                                                onChange={e => setLabForm(p => ({ ...p, testCode: e.target.value }))}
                                                placeholder="VD: CBC"
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Đơn vị</Form.Label>
                                            <Form.Control
                                                value={labForm.unit}
                                                onChange={e => setLabForm(p => ({ ...p, unit: e.target.value }))}
                                                placeholder="VD: mg/dL"
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>
                                <Form.Group className="mb-3">
                                    <Form.Label>Kết quả <span className="text-danger">*</span></Form.Label>
                                    <Form.Control
                                        as="textarea" rows={2}
                                        value={labForm.result}
                                        onChange={e => setLabForm(p => ({ ...p, result: e.target.value }))}
                                        placeholder="Nhập kết quả xét nghiệm..."
                                    />
                                </Form.Group>
                                <Row>
                                    <Col>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Thời gian thực hiện</Form.Label>
                                            <Form.Control
                                                type="datetime-local"
                                                value={labForm.performedAt}
                                                onChange={e => setLabForm(p => ({ ...p, performedAt: e.target.value }))}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col className="d-flex align-items-end mb-3">
                                        <Form.Check
                                            type="checkbox"
                                            label="Kết quả bất thường"
                                            checked={labForm.isAbnormal}
                                            onChange={e => setLabForm(p => ({ ...p, isAbnormal: e.target.checked }))}
                                        />
                                    </Col>
                                </Row>
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => setShowLabModal(false)}>Huỷ</Button>
                                <Button variant="primary" onClick={handleSaveLab} disabled={savingLab}>
                                    {savingLab ? <><Spinner size="sm" animation="border" className="me-2" />Đang lưu...</>
                                        : (editingLab ? "Cập nhật" : "Thêm")}
                                </Button>
                            </Modal.Footer>
                        </Modal>
                    </Tab.Pane>

                </Tab.Content>
            </Tab.Container>
        </Container>
    );
};

export default AppointmentDetail;
