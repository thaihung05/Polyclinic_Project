import { useEffect, useState } from "react";
import Swal from "sweetalert2";
import { authApis, endpoints } from "../../../configs/Api";
import { Button } from "react-bootstrap";



const EMPTY_FORM = {
    code: "",
    name: "",
    genericName: "",
    category: "",
    unit: "",
    concentration: "",
    manufacturer: "",
    stockQuantity: 0,
    expiryDate: "",
    price: "",
    isActive: true,
}

const LOW_STOCK_THRESHOLD = 10;
const NEAR_EXPIRY_DAYS = 30;


const toInputDate = () => {

}


const MedicineManager = () => {

    const [medicines, setMedicines] = useState([]);
    const [loading, setLoading] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);
    const [save, setSave] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [tab, setTab] = useState(null);



    const loadMedicines = async () => {
        try {
            setLoading(true);
            const res = await authApis().get(endpoints['medicines']);
            setMedicines(res.data);
        }
        catch (err) {
            Swal.fire("Lỗi!", err?.response?.data || "Tải dữ liệu thuốc thất bại.", "error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadMedicines();
    }, []);


    const displayList = () => {
        if (tab === "low-stock")
            return medicines.filter(m => m.stockQuantity <= LOW_STOCK_THRESHOLD);
        if (tab === "near-expiry") {
            const cutoff = new Date();
            cutoff.setDate(cutoff.getDate() + NEAR_EXPIRY_DAYS);
            return medicines.filter(m => m.expiryDate && new Date(m.expiryDate) <= cutoff);
        }
        return medicines;
    };

    const openCreate = () => {
        setEditingId(null);
        setForm(EMPTY_FORM);
        setShowModal(true);
    };


    const openEdit = (medicine) => {
        setEditingId(medicine.id);
        setForm({
            code: medicine.code || "",
            name: medicine.name || "",
            genericName: medicine.genericName || "",
            category: medicine.category || "",
            unit: medicine.unit || "",
            concentration: medicine.concentration || "",
            manufacturer: medicine.manufacturer || "",
            stockQuantity: medicine.stockQuantity ?? 0,
            expiryDate: toInputDate(medicine.expiryDate),
            price: medicine.price ?? "",
            isActive: medicine.isActive ?? true,
        });
        setShowModal(true);
    }

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
    };

    const handleSave = async () => {
        if (!form.code.trim())
            return Swal.fire("Lỗi!", "Mã thuốc không được trống.", "error");

        if (!form.name.trim())
            return Swal.fire("Lỗi!", "Tên thuốc không được trống.", "error");

        if (!form.unit.trim())
            return Swal.fire("Lỗi!", "Đơn vị không được trống.", "error");

        if (form.price === "" || isNaN(Number(form.price)))
            return Swal.fire("Lỗi!", "Giá không hợp lệ.", "error");

        const payload = {
            ...form,
            stockQuantity: parseInt(form.stockQuantity) || 0,
            price: parseFloat(form.price),
            expiryDate: form.expiryDate
        };

        try {
            setSave(true);
            if (editingId) {
                await authApis().put(endpoints["medicine-update"](editingId), payload);
            } else {
                await authApis().post(endpoints["medicines-secure"], payload);
            }
            setShowModal(false);
            await loadMedicines();
            Swal.fire({
                icon: "success",
                title: editingId ? "Cập nhật thành công!" : "Thêm thuốc thành công!",
                showConfirmButton: false, timer: 1000
            });
        }
        catch (err) {
            Swal.fire("Lỗi!", err?.response?.data || "Không thể lưu thuốc.", "error");
        } finally {
            setSave(false);
        };
    };


    const handleDelete = async (medicine) => {
        const confirm = await Swal.fire({
            title: `Xóa thuốc "${medicine.name}"?`,
            text: "Hành động này không thể hoàn tác.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Xóa",
            cancelButtonText: "Đóng",
            confirmButtonColor: "#d33",
        });

        if (!confirm.isConfirmed) return;

        try {
            await authApis().delete(endpoints['medicine-delete'](medicine.id));
            await loadMedicines();
            Swal.fire({
                icon: "success",
                title: "Đã xóa!",
                showConfirmButton: false, timer: 1000
            });

        } catch (err) {
            Swal.fire("Lỗi!", err?.response?.data || "Không thể xóa thuốc.", "error");
        }

    };
    const lowStockCount = medicines.filter(m => m.stockQuantity <= LOW_STOCK_THRESHOLD).length;

    const nearExpiryCount = (() => {
        const cutoff = new Date();
        cutoff.setDate(cutoff.getDate() + NEAR_EXPIRY_DAYS);
        return medicines.filter(m => m.expiryDate && new Date(m.expiryDate) <= cutoff).length;
    })();

    const list = displayList();

    return (
        <>
            <div>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h4 className="fw-bold mb-0">Quản lý kho thuốc</h4>
                    <Button variant="primary" size="sm" onClick={openCreate}>
                        <i className="bi bi-plus-lg me-1"></i>Thêm thuốc
                    </Button>
                </div>

                <div className="mb-3 d-flex gap-2 flex-wrap">
                    <Button className={`btn btn-sm ${tab === "all" ? "btn-primary" : "btn-outline-secondary"}`} onClick={() =>{setTab("all")}}>

                    </Button>
                </div>

            </div>
        </>
    );
};

export default MedicineManager;