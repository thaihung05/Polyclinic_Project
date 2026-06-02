import { useCallback, useContext, useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import { MyUserContext } from "../../../configs/Contexts";
import { Alert, Badge, Button, Form, Modal, Table } from "react-bootstrap";
import MySpinner from "../../../components/MySpinner";
import Swal from "sweetalert2";

const parseDate = (date) => (date ? date.slice(0, 10) : "");
const parseTime = (date) => (date ? date.slice(11, 16) : "");
const today = () => new Date().toISOString().slice(0, 10);
const emptyForm = { 
    date: today(), 
    startTime: "08:00", 
    endTime: "10:00", 
    isActive: true
};

const TABS = [
    { key: "ALL", label: "Tất cả" },
    { key: "ACTIVE", label: "Còn trống" },
    { key: "BOOKED", label: "Đã được đặt" }
];

const ScheduleManager = () => {
    const [user] = useContext(MyUserContext);
    const doctorId = user?.doctorId;

    const [filterTab, setFilterTab] = useState("ALL");
    const [loading, setLoading] = useState(false);
    const [schedules, setSchedules] = useState([]);
    const [page, setPage] = useState(1);
    const itemsPage = 10;

    const [showModal, setShowModal] = useState(false);
    const [editItem, setEditItem] = useState(null);
    const [form, setForm] = useState(emptyForm);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadSchedules = useCallback(async () => {
        if (!doctorId) return;
        try {
            setLoading(true);
            const res = await authApis().get(endpoints['schedules'](doctorId));
            const sorted = (res.data || []).sort((a, b) =>
                new Date(a.startTime) - new Date(b.startTime)
            );
            setSchedules(sorted);
        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    }, [doctorId]);

    useEffect(() => {
        loadSchedules();
    }, [loadSchedules]);

    useEffect(() => {
        setPage(1);
    }, [filterTab]);

    const change = (e) => {
        setForm({ ...form, [e.target.name]: e.target.type === "checkbox" ? e.target.checked : e.target.value });
    };

    const openAdd = () => {
        setEditItem(null);
        setForm(emptyForm);
        setError("");
        setShowModal(true);
    };

    const openEdit = (item) => {
        setEditItem(item);
        setForm({
            date: parseDate(item.startTime),
            startTime: parseTime(item.startTime),
            endTime: parseTime(item.endTime),
            isActive: item.isActive
        });
        setError("");
        setShowModal(true);
    };

    const validate = () => {
        if (!form.date) {
            setError("Vui lòng chọn ngày!");
            return false;
        }
        if (!form.startTime) {
            setError("Vui lòng chọn giờ bắt đầu!");
            return false;
        }
        if (!form.endTime) {
            setError("Vui lòng chọn giờ kết thúc!");
            return false;
        }
        if (form.startTime >= form.endTime) {
            setError("Giờ kết thúc phải sau giờ bắt đầu!");
            return false;
        }
        return true;
    };

    const buildBody = () => ({
        startTime: `${form.date} ${form.startTime}:00`,
        endTime: `${form.date} ${form.endTime}:00`,
        isActive: form.isActive
    });

    const addSchedule = async () => {
        if (!validate()) return;
        try {
            setSaving(true);
            await authApis().post(endpoints['doctor-schedules'](doctorId), buildBody());
            Swal.fire({ 
                icon: "success", 
                title: "Thêm ca thành công!", 
                showConfirmButton: false, 
                timer: 1000
            });
            setShowModal(false);
            loadSchedules();
        } catch (ex) {
            Swal.fire({ 
                icon: "error", 
                title: "Thêm thất bại!", 
                text: ex.response?.data || "Đã xảy ra lỗi!"
            });
        } finally {
            setSaving(false);
        }
    };

    const updateSchedule = async () => {
        if (!validate()) return;
        try {
            setSaving(true);
            await authApis().put(endpoints['doctor-schedule-item'](doctorId, editItem.id), buildBody());
            Swal.fire({ 
                icon: "success", 
                title: "Cập nhật thành công!", 
                showConfirmButton: false, 
                timer: 1000
            });
            setShowModal(false);
            loadSchedules();
        } catch (ex) {
            Swal.fire({ 
                icon: "error", 
                title: "Cập nhật thất bại!", 
                text: ex.response?.data?.message || "Đã xảy ra lỗi!" 
            });
        } finally {
            setSaving(false);
        }
    };

    const deleteSchedule = async (scheduleId) => {
        const confirm = await Swal.fire({
            title: "Xác nhận",
            text: "Bạn có chắc muốn xóa ca này không?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Xóa",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#dc3545"
        });
        if (!confirm.isConfirmed) return;
        try {
            await authApis().delete(endpoints['doctor-schedule-item'](doctorId, scheduleId));
            Swal.fire({ 
                icon: "success", 
                title: "Xóa thành công!", 
                showConfirmButton: false, 
                timer: 1000 
            });
            loadSchedules();
        } catch (ex) {
            Swal.fire({ 
                icon: "error", 
                title: "Đã xảy ra lỗi!", 
                text: ex.response?.data || "Không thể xóa ca làm việc!" 
            });
        }
    };

    const filtered = schedules.filter(s => {
        if (filterTab === "ACTIVE") return s.isActive;
        if (filterTab === "BOOKED") return !s.isActive;
        return true;
    });

    const indexOfLast = page * itemsPage;
    const indexOfFirst = indexOfLast - itemsPage;
    const currentSchedules = filtered.slice(indexOfFirst, indexOfLast);
    const totalPages = Math.ceil(filtered.length / itemsPage);

    if (!doctorId) {
        return <Alert variant="warning">Không tìm thấy thông tin bác sĩ!</Alert>;
    }

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h4 className="fw-bold mb-0">Lịch làm việc</h4>
                <Button variant="primary" size="sm" onClick={openAdd}>
                    <i className="bi bi-plus-lg me-1"></i>Thêm ca mới
                </Button>
            </div>

            <div className="d-flex gap-2 mb-4">
                {TABS.map(t => {
                    const count = schedules.filter(s =>
                        t.key === "ALL" ? true :
                        t.key === "ACTIVE" ? s.isActive : !s.isActive
                    ).length;
                    return (
                        <button
                            key={t.key}
                            className={`btn btn-sm ${filterTab === t.key ? "btn-primary" : "btn-outline-secondary"}`}
                            onClick={() => setFilterTab(t.key)}
                        >
                            {t.label} ({count})
                        </button>
                    );
                })}
            </div>

            {loading && <MySpinner />}

            {!loading && filtered.length === 0 && (
                <Alert variant="info"><i className="bi bi-info-circle me-2"></i>Bác sĩ chưa có ca làm việc nào!</Alert>
            )}

            {!loading && filtered.length > 0 && (
                <>
                    <Table bordered hover responsive className="bg-white rounded shadow-sm">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Ngày</th>
                                <th>Giờ bắt đầu</th>
                                <th>Giờ kết thúc</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentSchedules.map((s, i) => (
                                <tr key={s.id}>
                                    <td>{indexOfFirst + i + 1}</td>
                                    <td>{parseDate(s.startTime)}</td>
                                    <td>{parseTime(s.startTime)}</td>
                                    <td>{parseTime(s.endTime)}</td>
                                    <td>
                                        <Badge bg={s.isActive ? "success" : "secondary"}>
                                            {s.isActive ? "Còn trống" : "Đã được đặt"}
                                        </Badge>
                                    </td>
                                    <td>
                                        <Button variant="outline-primary" size="sm" className="me-2"
                                            onClick={() => openEdit(s)}
                                            disabled={!s.isActive}>
                                            Sửa
                                        </Button>
                                        <Button variant="outline-danger" size="sm"
                                            onClick={() => deleteSchedule(s.id)}
                                            disabled={!s.isActive}>
                                            Xóa
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    {totalPages > 1 && (
                        <div className="d-flex justify-content-center mt-3">
                            <Button size="sm" className="me-2" disabled={page === 1}
                                onClick={() => setPage(prev => prev - 1)}
                            >Trang trước</Button>
                            <span className="align-self-center">{page} / {totalPages}</span>
                            <Button size="sm" className="ms-2" disabled={page === totalPages}
                                onClick={() => setPage(prev => prev + 1)}
                            >Trang sau</Button>
                        </div>
                    )}
                </>
            )}

            <Modal show={showModal} onHide={() => setShowModal(false)} backdrop="static">
                <Modal.Header>
                    <Modal.Title>
                        <i className={`bi ${editItem ? "bi-pencil-square" : "bi-plus-circle"} me-2`}></i>
                        {editItem ? "Cập nhật ca làm việc" : "Thêm ca mới"}
                    </Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {error && <Alert variant="danger">{error}</Alert>}
                    <div className="row g-3">
                        <div className="col-12">
                            <Form.Label className="small fw-semibold">Ngày <span className="text-danger">*</span></Form.Label>
                            <Form.Control type="date" size="sm" name="date"
                                value={form.date} onChange={change}
                                min={today()} />
                        </div>
                        <div className="col-6">
                            <Form.Label className="small fw-semibold">
                                Giờ bắt đầu <span className="text-danger">*</span>
                            </Form.Label>
                            <Form.Control type="time" size="sm" name="startTime"
                                value={form.startTime} onChange={change} />
                        </div>
                        <div className="col-6">
                            <Form.Label className="small fw-semibold">
                                Giờ kết thúc <span className="text-danger">*</span>
                            </Form.Label>
                            <Form.Control type="time" size="sm" name="endTime"
                                value={form.endTime} onChange={change} />
                        </div>
                        {editItem && (
                            <div className="col-12">
                                <Form.Check type="switch" name="isActive"
                                    checked={form.isActive} onChange={change}
                                    label={<span className="small fw-semibold">Còn trống</span>} />
                            </div>
                        )}
                    </div>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="secondary" size="sm" onClick={() => setShowModal(false)} disabled={saving}>Hủy</Button>
                    <Button variant="primary" size="sm" onClick={editItem ? updateSchedule : addSchedule} disabled={saving}>
                        {saving ? <><span className="spinner-border spinner-border-sm me-1"></span>Đang lưu...</>
                                : <><i className={`bi ${editItem ? "bi-check-lg" : "bi-plus-lg"} me-1`}></i>{editItem ? "Cập nhật" : "Thêm ca"}</>
                        }
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default ScheduleManager;
