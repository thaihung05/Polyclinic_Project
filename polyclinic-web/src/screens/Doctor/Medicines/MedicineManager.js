import { Alert, Badge, Button, Table, Spinner, Modal, Form } from "react-bootstrap";
import Moment from "react-moment";
import "moment/locale/vi";
import { useCallback, useEffect, useState } from "react";
import MySpinner from "../../../components/MySpinner";
import Apis, { authApis, endpoints } from "../../../configs/Api";
import Swal from "sweetalert2";
import "./MedicineManager.css";


const CATEGORIES = ["Kháng sinh", "Giảm đau", "Hạ sốt", "Tim mạch", "Tiêu hóa", "Hô hấp", "Da liễu", "Thần kinh", "Nội tiết", "Vitamin", "Khác"];

const formatPrice = (price) => {
    if (!price && price !== 0) return '-';
    return Number(price).toLocaleString('vi-VN') + "VNĐ";
}

const expiryBadge = (dateStr) => {
    if (!dateStr)
        return <span className="text-muted">—</span>;

    const expireDate = new Date(dateStr);
    const today = new Date();
    const diffDays = Math.ceil((expireDate - today) / (1000 * 60 * 60 * 24));

    if (diffDays < 0)
        return <Badge bg="danger">Đã hết hạn</Badge>;

    if (diffDays <= 30) {
        return (
            <Badge bg="warning" text="dark">
                <Moment format="DD/MM/YYYY">{dateStr}</Moment>
            </Badge>
        );
    }
    return <Moment format="DD/MM/YYYY">{dateStr}</Moment>;
}


const TABS = [
    { key: "list", label: "Danh sách thuốc", icon: "bi-capsule" },
    { key: "low-stock", label: "Tồn kho thấp", icon: "bi-exclamation-triangle" },
    { key: "near-expiry", label: "Sắp hết hạn", icon: "bi-clock-history" }
];

const PAGE_SIZE = 10;

const MedicineManager = () => {
    const [medicines, setMedicines] = useState([]);
    const [loading, setLoading] = useState(false);
    const [tab, setTab] = useState('list');


    const [search, setSearch] = useState('');
    const [filterCategory, setFilterCategory] = useState("");
    const [page, setPage] = useState(1); const [showModal, setShowModal] = useState(false);

    const [alerts, setAlerts] = useState([]);
    const [editItem, setEditItem] = useState(null);
    const [form, setForm] = useState({ code: "", name: "", genericName: "", category: "", unit: "", concentration: "", manufacturer: "", stockQuantity: 0, expiryDate: "", price: "", isActive: true });
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const [threshold, setThreshold] = useState(10);
    const [days, setDays] = useState(30);
    const [debouncedThreshold, setDebouncedThreshold] = useState(10);
    const [debouncedDays, setDebouncedDays] = useState(30);

    const loadMedicines = useCallback(async () => {
        try {
            setLoading(true);
            const res = await Apis.get(endpoints['medicines']);

            setMedicines(res.data || []);
        }
        catch (err) {
            Swal.fire({ icon: "error", title: "Lỗi tải danh sách thuốc!", showConfirmButton: false, timer: 1000 });
        }
        finally {
            setLoading(false);
        }
    }, [])

    const loadAlerts = useCallback(async () => {
        try {
            setLoading(true);
            let res = null;
            if (tab === 'low-stock')
                res = await authApis().get(`${endpoints['medicines-low-stock']}?threshold=${debouncedThreshold}`);
            else
                res = await authApis().get(`${endpoints['medicines-near-expiry']}?days=${debouncedDays}`);

            setAlerts(res.data || []);
        }
        catch (err) {
            console.error(err);
        }
        finally {
            setLoading(false);
        }
    }, [tab, debouncedThreshold, debouncedDays]);

    useEffect(()=>{
        setAlerts([]);
        const time = setTimeout(() => {
            setDebouncedThreshold(threshold)
        },500);
        return () => clearTimeout(time);
    },[threshold]);

    useEffect(() =>{
        setAlerts([]);
        const time = setTimeout(() => {
            setDebouncedDays(days);
        },500);
        return () => clearTimeout(time);
    },[days]);

    useEffect(() => {
        if (tab === 'list') loadMedicines();
        else loadAlerts();
    }, [tab, loadMedicines, loadAlerts]);


    useEffect(() => {
        setPage(1);
    }, [search, filterCategory, tab]);


    const change = (e) => {
        setForm({ ...form, [e.target.name]: e.target.type === 'checkbox' ? e.target.checked : e.target.value });
    }

    const loadMore = () => {
        if (!loading) setPage(page + 1);
    };

    const openAdd = () => {
        setEditItem(null);
        setForm({ code: "", name: "", genericName: "", category: "", unit: "", concentration: "", manufacturer: "", stockQuantity: 0, expiryDate: "", price: "", isActive: true });
        setError("");
        setShowModal(true);
    };

    const openEdit = (item) => {
        setEditItem(item);
        setForm({
            code: item.code || "",
            name: item.name || "",
            genericName: item.genericName || "",
            category: item.category || "",
            unit: item.unit || "",
            concentration: item.concentration || "",
            manufacturer: item.manufacturer || "",
            stockQuantity: item.stockQuantity ?? 0,
            expiryDate: item.expiryDate ? item.expiryDate.slice(0, 10) : "",
            price: item.price ?? "",
            isActive: item.isActive ?? true
        });
        setError("");
        setShowModal(true);
    };

    const validate = () => {
        if (!form.code || form.code.trim() === "") {
            setError("Mã thuốc không được để trống!");
            return false;
        }
        if (!form.name || form.name.trim() === "") {
            setError("Tên thuốc không được để trống!");
            return false;
        }
        if (!form.unit || form.unit.trim() === "") {
            setError("Đơn vị không được để trống!");
            return false;
        }
        if (form.stockQuantity === "" || form.stockQuantity < 0) {
            setError("Số lượng tồn kho không hợp lệ!");
            return false;
        }
        if (form.price === "" || Number(form.price) < 0) {
            setError("Đơn giá không hợp lệ!");
            return false;
        }
        return true;
    };


    const buildBody = () => ({
        ...form,
        stockQuantity: Number(form.stockQuantity),
        price: Number(form.price),
        expiryDate: form.expiryDate ? form.expiryDate + " 00:00:00" : null
    });

    const addMedicine = async () => {
        if (!validate()) return;
        try {
            setSaving(true);
            await authApis().post(endpoints['medicines-secure'], buildBody());
            Swal.fire({ icon: "success", title: "Thêm thuốc thành công!", showConfirmButton: false, timer: 1000 });
            setShowModal(false);
            loadMedicines();
        } catch (err) {
            Swal.fire({ icon: "error", title: "Thêm thất bại!", text: "Đã xảy ra lỗi! Thêm thuốc thất bại!" });
        } finally {
            setSaving(false);
        }
    };

    const updateMedicine = async () => {
        if (!validate()) return;
        try {
            setSaving(true);
            await authApis().put(endpoints['medicine-update'](editItem.id), buildBody());
            Swal.fire({ icon: "success", title: "Cập nhật thành công!", showConfirmButton: false, timer: 1000 });
            setShowModal(false);
            loadMedicines();
        } catch (err) {
            Swal.fire({ icon: "error", title: "Cập nhật thất bại!", text:"Đã xảy ra lỗi! Cập nhật thuốc thất bại!" });
        } finally {
            setSaving(false);
        }
    };


    const deleteMedicines = async (item) => {
        const confirm = await Swal.fire({
            title: "Xác nhận xóa",
            text: `Bạn có chắc muốn xóa thuốc "${item.name}" không?`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Xóa",
            cancelButtonText: "Hủy",
            confirmButtonColor: "#dc3545"
        });

        if (confirm.isConfirmed) {
            try {
                setLoading(true);
                await authApis().delete(endpoints["medicine-delete"](item.id));
                Swal.fire({ icon: "success", title: "Đã xóa thuốc!", showConfirmButton: false, timer: 1000 });
                loadMedicines();
            } catch (err) {
                Swal.fire({
                    icon: "error",
                    title: "Xóa thất bại!",
                    text: err?.response?.data || "Thuốc đang được sử dụng hoặc đã xảy ra lỗi."
                });
            }
        }
    };

    const categories = [...new Set(medicines.map(m => m.category).filter(Boolean))].sort();

    const filtered = medicines.filter(m => {
        const q = search.toLowerCase();
        const matchSearch = !q || m.name?.toLowerCase().includes(q) || m.code?.toLowerCase().includes(q) || m.genericName?.toLowerCase().includes(q);
        const matchCategory = !filterCategory || m.category === filterCategory;
        return matchSearch && matchCategory;
    });

    const paged = filtered.slice(0, page * PAGE_SIZE);
    const hasMore = paged.length < filtered.length;

    const alertPaged = alerts.slice(0, page * PAGE_SIZE);
    const alertHasMore = alertPaged.length < alerts.length;

    return (
        <>
            <div>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h4 className="fw-bold mb-0"><i className="bi bi-capsule me-2"></i>Quản lý kho thuốc</h4>

                    {tab === 'list' && (
                        <Button variant="primary" size="sm" onClick={openAdd}>
                            <i className="bi bi-plus-lg me-1"></i>Thêm thuốc
                        </Button>
                    )}
                </div>

                <div className="d-flex gap-2 mb-4 flex-wrap p-1 rounded-3 mm-tab-bar">
                    {TABS.map(t => (
                        <button key={t.key}
                            className={`mm-tab-btn${tab === t.key ? " active" : ""}`}
                            onClick={() => { setTab(t.key); setPage(1); }}
                        >
                            <i className={`bi ${t.icon} me-1`}></i>{t.label}
                            {t.key === 'list' && (
                                <span className="mm-tab-badge">{medicines.length}</span>
                            )}
                        </button>
                    ))}
                </div>

                {tab === 'list' && (
                    <div className="d-flex gap-2 mb-3 flex-wrap align-items-center">
                        <input className="form-control form-control-sm"
                            style={{ maxWidth: 260 }}
                            placeholder="Tìm theo tên, mã, tên generic..."
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                        />

                        <select className="form-select form-select-sm"
                            style={{ maxWidth: 200 }}
                            value={filterCategory}
                            onChange={e => setFilterCategory(e.target.value)}
                        >
                            <option value="">Tất cả danh mục</option>
                            {categories.map(c => <option key={c} value={c}>{c}</option>)}

                        </select>

                        {(search || filterCategory) && (
                            <Button variant="outline-primary" size="sm"
                                onClick={() => { setSearch(""); setFilterCategory('') }}
                            >
                                <i className="bi bi-x-circle me-1"></i>Bỏ lọc
                            </Button>
                        )}

                        <span className="text-muted small ms-auto">Hiển thị {filtered.length} / {medicines.length} thuốc</span>
                    </div>
                )}

                {(tab === 'low-stock' || tab === 'near-expiry') && (

                    <div className="d-flex gap-3 mb-3 align-items-center flex-wrap">
                        {tab === 'low-stock' && (
                            <div className="d-flex align-items-center gap-2">
                                <label className="form-label mb-0 small fw-semibold">Ngưỡng tồn kho:</label>
                                <input type="number" className="form-control form-control-sm" style={{ width: 80 }}
                                    value={threshold} min={1}
                                    onChange={e => setThreshold(Number(e.target.value))} />
                            </div>
                        )}
                        {tab === 'near-expiry' && (
                            <div className="d-flex align-items-center gap-2">
                                <label className="form-label mb-0 small fw-semibold">Hết hạn trong:</label>
                                <input type="number" className="form-control form-control-sm" style={{ width: 80 }}
                                    value={days} min={1}
                                    onChange={e => setDays(Number(e.target.value))} />
                                <span className="small">ngày</span>
                            </div>
                        )}
                        <span className="text-muted small ms-auto">{alerts.length} thuốc cần chú ý</span>
                    </div>
                )}

                {loading && <MySpinner />}

                {!loading && tab === 'list' && (
                    <>
                        {filtered.length === 0 ? (
                            <Alert variant="info"><i className="bi bi-info-circle me-2"></i>Không có thuốc nào phù hợp.</Alert>
                        ) : (
                            <>
                                <div className="table-responsive">
                                    <Table bordered hover size="sm" className="bg-white rounded shadow-sm">
                                        <thead className="table-light">
                                            <tr>
                                                <th>STT</th>
                                                <th>Mã thuốc</th>
                                                <th>Tên thuốc</th>
                                                <th>Tên generic</th>
                                                <th>Danh mục</th>
                                                <th>Đơn vị</th>
                                                <th>Nồng độ</th>
                                                <th>Nhà sản xuất</th>
                                                <th>Tồn kho</th>
                                                <th>Hạn dùng</th>
                                                <th>Đơn giá</th>
                                                <th>Trạng thái</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {paged.map((m, i) => (
                                                <tr key={m.id}>
                                                    <td>{i + 1}</td>
                                                    <td><code>{m.code}</code></td>
                                                    <td className="fw-semibold">{m.name}</td>
                                                    <td className="text-muted">{m.genericName || "—"}</td>
                                                    <td>{m.category ? <Badge bg="info" text="dark">{m.category}</Badge> : '-'}</td>
                                                    <td>{m.unit}</td>
                                                    <td>{m.concentration || '-'}</td>
                                                    <td>{m.manufacturer || "—"}</td>
                                                    <td>{m.stockQuantity}</td>
                                                    <td>{expiryBadge(m.expiryDate)}</td>
                                                    <td className="text-center">{formatPrice(m.price)}</td>
                                                    <td className="text-center">
                                                            {m.isActive ? "Còn" : "Hết"}
                                                    </td>
                                                    <td>
                                                        <div className="d-flex gap-1">
                                                            <Button size="sm" variant="outline-primary" onClick={() => openEdit(m)}>
                                                                <i className="bi bi-pencil"></i>
                                                            </Button>
                                                            <Button size="sm" variant="outline-danger" onClick={() => deleteMedicines(m)}>
                                                                <i className="bi bi-trash"></i>
                                                            </Button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </Table>
                                </div>

                                {hasMore && (
                                    <div className="text-center mt-3">
                                        <Button variant="outline-primary" size="sm" onClick={loadMore}>
                                            Xem thêm
                                        </Button>
                                    </div>
                                )}
                            </>
                        )}
                    </>
                )}

                {!loading && (tab === 'low-stock' || tab === "near-expiry") && (
                    <>
                        {alerts.length === 0 ? (
                            <Alert variant="success">
                                <i className="bi bi-check-circle me-2"></i>
                                {tab === "low-stock" ? "Không có thuốc nào dưới ngưỡng tồn kho." : "Không có thuốc nào sắp hết hạn."}
                            </Alert>
                        ) : (
                            <>
                                <div className="table-responsive">
                                    <Table className="bg-white rounded shadow-sm" bordered hover size="sm">
                                        <thead className="table-light">
                                            <tr>
                                                <th>STT</th>
                                                <th>Mã thuốc</th>
                                                <th>Tên thuốc</th>
                                                <th>Danh mục</th>
                                                <th>Đơn vị</th>
                                                {tab === "low-stock" && <th>Tồn kho</th>}
                                                {tab === "near-expiry" && <th>Hạn dùng</th>}
                                                <th>Đơn giá</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {alertPaged.map((m, i) => (
                                                <tr key={m.id} className={tab === "low-stock" && m.stockQuantity <= 0 ? "table-danger" : "table-warning"}>
                                                    <td>{i + 1}</td>
                                                    <td><code>{m.code}</code></td>
                                                    <td className="fw-semibold">{m.name}</td>
                                                    <td>{m.category || "—"}</td>
                                                    <td>{m.unit}</td>
                                                    {tab === 'low-stock' && <td>{m.stockQuantity}</td>}
                                                    {tab === "near-expiry" && <td>{expiryBadge(m.expiryDate)}</td>}
                                                    <td>{formatPrice(m.price)}</td>
                                                    <td>
                                                        <Button size="sm" variant="outline-primary" onClick={() => { setTab("list"); openEdit(m); }}>
                                                            <i className="bi bi-pencil me-1"></i>Cập nhật
                                                        </Button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </Table>
                                </div>
                                {alertHasMore && (
                                    <div className="text-center mt-3">
                                        <Button variant="outline-primary" size="sm" onClick={loadMore}>
                                            Xem thêm
                                        </Button>
                                    </div>
                                )}
                            </>
                        )}
                    </>
                )}



                <Modal show={showModal}
                    onHide={() => { setShowModal(false) }}
                    size="lg" backdrop="static"
                >
                    <Modal.Header closeButton>
                        <Modal.Title>
                            <i className={`bi ${editItem ? "bi-pencil-square" : "bi-plus-circle"} me-2`}></i>
                            {editItem ? "Cập nhật thuốc" : "Thêm thuốc mới"}
                        </Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        {error && <Alert variant="danger">{error}</Alert>}
                        <div className="row g-3">
                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Mã thuốc <span className="text-danger">*</span></Form.Label>
                                <Form.Control size="sm" name="code" value={form.code} onChange={change} placeholder="VD: MED001" />
                            </div>
                            <div className="col-md-8">
                                <Form.Label className="small fw-semibold">Tên thuốc <span className="text-danger">*</span></Form.Label>
                                <Form.Control size="sm" name="name" value={form.name} onChange={change}
                                    placeholder="Tên thương mại" />
                            </div>

                            <div className="col-md-6">
                                <Form.Label className="small fw-semibold">Tên generic</Form.Label>
                                <Form.Control size="sm" name="genericName" value={form.genericName} onChange={change}
                                    placeholder="Tên hoạt chất" />
                            </div>

                            <div className="col-md-6">
                                <Form.Label className="small fw-semibold">Danh mục</Form.Label>
                                <Form.Select size="sm" name="category" value={form.category} onChange={change}>
                                    <option value="">-- Chọn danh mục --</option>
                                    {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                                </Form.Select>
                            </div>

                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Đơn vị <span className="text-danger">*</span></Form.Label>
                                <Form.Control size="sm" name="unit" value={form.unit} onChange={change}
                                    placeholder="VD: Viên, Chai, Lọ..." />
                            </div>

                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Nồng độ</Form.Label>
                                <Form.Control size="sm" name="concentration" value={form.concentration} onChange={change} placeholder="VD: 500mg, 10mg/5ml..." />
                            </div>

                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Nhà sản xuất</Form.Label>
                                <Form.Control size="sm" name="manufacturer" value={form.manufacturer} onChange={change} placeholder="Tên công ty" />
                            </div>

                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Số lượng tồn kho <span className="text-danger">*</span></Form.Label>
                                <Form.Control size="sm" type="number" name="stockQuantity" min={0}
                                    value={form.stockQuantity} onChange={change} />
                            </div>

                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Hạn sử dụng</Form.Label>
                                <Form.Control size="sm" type="date" name="expiryDate" value={form.expiryDate} onChange={change} />
                            </div>

                            <div className="col-md-4">
                                <Form.Label className="small fw-semibold">Đơn giá (VNĐ) <span className="text-danger">*</span></Form.Label>
                                <Form.Control size="sm" type="number" name="price" min={0} step={1000}
                                    value={form.price} onChange={change} placeholder="VD: 5000" />
                            </div>

                            {editItem && (
                                <div className="col-12">
                                    <Form.Check type="switch" name="isActive" checked={form.isActive}
                                        onChange={change}
                                        label={<span className="small fw-semibold">Đang hoạt động</span>} />
                                </div>
                            )}
                        </div>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="secondary" size="sm" onClick={() => setShowModal(false)} disabled={saving}>Hủy</Button>
                        <Button variant="primary" size="sm" onClick={editItem ? updateMedicine : addMedicine} disabled={saving}>
                            {saving
                                ? <><span className="spinner-border spinner-border-sm me-1"></span>Đang lưu...</>
                                : <><i className={`bi ${editItem ? "bi-check-lg" : "bi-plus-lg"} me-1`}></i>{editItem ? "Cập nhật" : "Thêm thuốc"}</>
                            }
                        </Button>
                    </Modal.Footer>

                </Modal>

            </div>
        </>
    );
}


export default MedicineManager;