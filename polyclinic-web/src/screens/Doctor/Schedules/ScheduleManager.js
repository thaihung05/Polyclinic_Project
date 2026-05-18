import { useCallback, useEffect, useState } from "react";
import { authApis, endpoints } from "../../../configs/Api";
import { Alert, Badge, Button, Table } from "react-bootstrap";
import MySpinner from "../../../components/MySpinner";

const parseDate = (date) => (date ? date.slice(0, 10) : "");
const parseTime = (date) => (date ? date.slice(11, 16) : "");
const today = () => new Date().toISOString().slice(0, 10);
const emptyForm = { date: today(), startTime: "08:00", endTime: "10:00", isActive: true };

const ScheduleManager = () => {
    const token = localStorage.getItem("polyclinic_token");
    const user = JSON.parse(localStorage.getItem("polyclinic_user"));
    const doctorId = user.doctorId;

    const [filterTab, setFilterTab] = useState("ALL");
    const [loading, setLoading] = useState(false);
    const [schedules, setSchedules] = useState([]);
    const [page, setPage] = useState(1);
    const itemsPage = 10;
    const [showModal, setShowModal] = useState(false);
    const [editItem, setEditItem] = useState(null);
    const [form, setForm] = useState(emptyForm);
    const [saving, setSaving] = useState(false);

    const loadSchedules = useCallback(async () =>{
        if (!doctorId) return;
        try{
            setLoading(true);
            const res = await authApis(token).get(endpoints['schedules'](doctorId));
            const sorted = (res.data || []).sort((a, b)=>
                new Date(a.startTime) - new Date(b.startTime)
            );
            setSchedules(sorted);
        } catch(ex){
            console.error(ex);
        } finally{
            setLoading(false);
        }
    },[token, doctorId]);

    useEffect(()=>{
        loadSchedules();
    }, [loadSchedules]);

    const openAdd = () => {
        setEditItem(null);
        setForm(emptyForm);
        setShowModal(true);
    }

    const openEdit = (item) => {
        setEditItem(item);
        setForm({
            date: parseDate(item.startTime),
            startTime: parseTime(item.startTime),
            endTime: parseTime(item.endTime),
            isActive: item.isActive
        });
        setShowModal(true);
    }

    const TABS = [
        {key: "ALL", label: "Tất cả"},
        {key: "ACTIVE", label: "Còn trống"},
        {key: "BOOKED", label: "Đã được đặt"}
    ];

    const filtered = schedules.filter(s => {
        if (filterTab === "ACTIVE") return s.isActive;
        if (filterTab === "BOOKED") return !s.isActive;
        return true;
    })

    const indexOfLast = page * itemsPage;
    const indexOfFirst = indexOfLast - itemsPage;
    const currentSchedules = filtered.slice(indexOfFirst, indexOfLast);
    const totalPages = Math.ceil(filtered.length / itemsPage);

    useEffect(() => {
        setPage(1);
    }, [filterTab]);

    if(!doctorId){
        return(
            <Alert variant="warning">Không tìm thấy thông tin bác sĩ!</Alert>
        )
    }

    return(
        <div>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h4 className="fw-bold mb-0">Lịch làm việc</h4>
                <button variant="primary" size="sm" onClick={openAdd}>
                    <i className="bi bi-plus-lg me-1"></i>Thêm ca mới
                </button>
            </div>

            <div className="d-flex gap-2 mb-4">
                {TABS.map(t => {
                    const count = schedules.filter(s=>
                        t.key === "ALL" ? true :
                        t.key === "ACTIVE" ? s.isActive : !s.isActive
                    ).length;
                    return (
                        <button
                            key={t.key}
                            className={`btn btn-sm ${filterTab === t.key ? "btn-primary": "btn-outline-secondary"}`}
                            onClick={() => setFilterTab(t.key)}
                        >
                            {t.label} ({count})
                        </button>
                    )
                })}
            </div>

            {loading && <MySpinner />}

            {!loading && filtered.length === 0 && (
                <Alert variant="info"><i className="bi bi-info-circle me-2"></i>Bác sĩ chưa có ca làm việc nào!</Alert>
            )}

            {!loading && filtered.length > 0 && (
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
                        {currentSchedules.map((s,i)=>(
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
                                        onClick={() => openEdit(s)}
                                        disabled={!s.isActive}>
                                        Xóa
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}
            <div className="d-flex justify-content-center mt-3">
                <Button size="sm" className="me-2" disabled={page === 1}
                    onClick={() => setPage(prev => prev - 1)}
                >Trang trước</Button>
                <span className="align-self-center">{page} / {totalPages}</span>
                <Button size="sm" className="ms-2" disabled={page === totalPages}
                    onClick={() => setPage(prev => prev + 1)}
                >Trang trước</Button>
            </div>
        </div>
    )

}

export default ScheduleManager;