import { useCallback, useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Api";
import { Alert, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import cookies from "react-cookies";

const PharmacistHome = () => {
    const user = cookies.load('user');
    const navigate = useNavigate();

    const [medicines, setMedicines] = useState([]);
    const [lowStock, setLowStock] = useState([]);
    const [nearExpiry, setNearExpiry] = useState([]);
    const [loading, setLoading] = useState(false);

    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            const [medRes, lowRes, expiryRes] = await Promise.all([
                authApis().get(endpoints['medicines']),
                authApis().get(`${endpoints['medicines-low-stock']}?threshold=10`),
                authApis().get(`${endpoints['medicines-near-expiry']}?days=30`)
            ]);
            setMedicines(medRes.data || []);
            setLowStock(lowRes.data || []);
            setNearExpiry(expiryRes.data || []);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const cards = [
        { label: "Tổng số thuốc", value: medicines.length, icon: "bi bi-capsule", bg: "#e8f4f8" },
        { label: "Tồn kho thấp", value: lowStock.length, icon: "bi bi-exclamation-triangle", bg: "#fff3cd" },
        { label: "Sắp hết hạn", value: nearExpiry.length, icon: "bi bi-clock-history", bg: "#f8d7da" },
    ];

    return (
        <div>
            <div className="mb-4">
                <h4 className="fw-bold mb-1">
                    Xin chào, Dược sĩ {user.name}
                </h4>
                <p className="text-muted">
                    Hôm nay: {new Date().toLocaleString("vi-VN", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}
                </p>
            </div>

            {loading && <MySpinner />}

            {!loading && (
                <>
                    <div className="row g-3 mb-4">
                        {cards.map((c, i) => (
                            <div className="col-md-4" key={i}>
                                <div className="card border-0 shadow-sm h-100" style={{ background: c.bg }}>
                                    <div className="card-body d-flex align-items-center gap-3">
                                        <i className={`${c.icon} fs-3 text-secondary`}></i>
                                        <div>
                                            <div className="fs-4 fw-bold">{c.value}</div>
                                            <div>{c.label}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="card shadow-sm">
                        <div className="card-header fw-semibold bg-white d-flex justify-content-between align-items-center">
                            <span>
                                <i className="bi bi-exclamation-triangle me-2"></i>
                                Thuốc cần chú ý
                                <Badge bg="warning" text="dark" className="ms-2">
                                    {lowStock.length + nearExpiry.length}
                                </Badge>
                            </span>
                            <button className="btn btn-sm btn-outline-primary"
                                onClick={() => navigate("/pharmacist/dashboard/medicines")}>
                                Vào kho thuốc
                            </button>
                        </div>
                        <div className="card-body">
                            {lowStock.length === 0 && nearExpiry.length === 0 ? (
                                <Alert variant="success" className="mb-0">
                                    <i className="bi bi-check-circle me-2"></i>Kho thuốc ổn định, không có cảnh báo nào.
                                </Alert>
                            ) : (
                                <div className="d-flex flex-column gap-2">
                                    {lowStock.map(m => (
                                        <div key={m.id} className="d-flex justify-content-between align-items-center p-2 rounded bg-warning bg-opacity-10">
                                            <span><i className="bi bi-exclamation-triangle text-warning me-2"></i>{m.name}</span>
                                            <Badge bg="warning" text="dark">Tồn kho: {m.stockQuantity}</Badge>
                                        </div>
                                    ))}
                                    {nearExpiry.map(m => (
                                        <div key={m.id} className="d-flex justify-content-between align-items-center p-2 rounded bg-danger bg-opacity-10">
                                            <span><i className="bi bi-clock-history text-danger me-2"></i>{m.name}</span>
                                            <Badge bg="danger">Hết hạn: {new Date(m.expiryDate).toLocaleDateString("vi-VN")}</Badge>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default PharmacistHome;