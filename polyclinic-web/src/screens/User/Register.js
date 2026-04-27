import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { endpoints } from "../../configs/Api";
import Swal from "sweetalert2";
import "./user.css";
import "../../styles/base.css";
import { Button, Form } from "react-bootstrap";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";

const Register = () => {
    const nav = useNavigate();

    const [user, setUser] = useState({
        name: "",
        phone: "",
        username: "",
        password: "",
        confirmPassword:"",
        email:"",
        gender:"",
        dateOfBirth:"",
        address:"",
        avatar: null
    });

    const [loading, setLoading] = useState(false);
    const [dobDate, setDobDate] = useState(null);
    const [showCalendar, setShowCalendar] = useState(false);

    const change = (e) => {
        const { name, value, files } = e.target;

        if (name === "avatar")
            setUser({ ...user, avatar: files[0] });
        else
            setUser({ ...user, [name]: value });
    };
    
    const onDobChange = (date) => {
        setDobDate(date);
        const d = String(date.getDate()).padStart(2, "0");
        const m = String(date.getMonth() + 1).padStart(2, "0");
        const y = date.getFullYear();
        setUser({ ...user, dateOfBirth: `${d}-${m}-${y}` });
        setShowCalendar(false);
    };

    const register = async (e) => {
        e.preventDefault();
        if (user.password !== user.confirmPassword) {
            Swal.fire({ icon: "warning", title: "Mật khẩu không khớp!" });
            return;
        }
        setLoading(true);

        try {
            let form = new FormData();
            form.append("name", user.name);
            form.append("phone", user.phone);
            form.append("username", user.username);
            form.append("password", user.password);
            form.append("confirmPassword", user.confirmPassword);
            form.append("email", user.email);
            form.append("gender", user.gender);
            form.append("address", user.address);
            form.append("dateOfBirth", user.dateOfBirth);

            if (user.avatar)
                form.append("avatar", user.avatar);

            const res = await Apis.post(endpoints.register, form, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            });

            Swal.fire({
                icon: "success",
                title: "Thành công!",
                text: res.data?.message || "Đăng ký thành công!"
            }).then(() => nav("/login"));

        } catch (err) {
            console.error(err);
            Swal.fire({
                icon: "error",
                title: "Lỗi!",
                text: err.response?.data?.message || "Đăng ký thất bại!"
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Header />
                <main className="register-wrapper d-flex justify-content-center align-items-center">
                    <div className="register-card mt-3 mb-3">
                        <h3 className="text-center fw-bold mb-4">Đăng ký tài khoản</h3>

                        <Form onSubmit={register}>
                            <div className="mb-3">
                                <label className="form-label">Họ và tên</label>
                                <input type="text" name="name" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Giới tính</label>
                                <Form.Select name="gender" onChange={change} required>
                                    <option value="">Chọn giới tính</option>
                                    <option value="MALE">Nam</option>
                                    <option value="FEMALE">Nữ</option>
                                    <option value="OTHER">Khác</option>
                                </Form.Select>
                            </div>


                            <div className="mb-3">
                                <label className="form-label">Địa chỉ</label>
                                <input type="text" name="address" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Email</label>
                                <input type="text" name="email" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Số điện thoại</label>
                                <input type="text" name="phone" className="form-control input-custom" required onChange={change} />
                            </div>

                            {/* <div className="mb-3">
                                <label className="form-label">Ngày sinh</label>
                                <div>
                                    <Calendar onChange={onDobChange} value={dobDate} maxDate={new Date()} />
                                </div>
                            </div> */}

                            <div className="mb-3" style={{ position: "relative" }}>
                                <label className="form-label">Ngày sinh</label>
                                <input
                                    type="text"
                                    readOnly
                                    className="form-control input-custom"
                                    placeholder="Chọn ngày sinh"
                                    value={dobDate ? dobDate.toLocaleDateString("vi-VN") : ""}
                                    onClick={() => setShowCalendar(!showCalendar)}
                                    style={{ cursor: "pointer" }}
                                />
                                {showCalendar && (
                                    <div style={{
                                        position: "absolute",
                                        zIndex: 999,
                                        top: "100%",
                                        left: 0,
                                        boxShadow: "0 4px 16px rgba(0,0,0,0.15)",
                                        borderRadius: "8px",
                                        overflow: "hidden",
                                        background: "#fff"
                                    }}>
                                        <Calendar
                                            onChange={onDobChange}
                                            value={dobDate}
                                            maxDate={new Date()}
                                        />
                                    </div>
                                )}
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Tên đăng nhập</label>
                                <input type="text" name="username" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Mật khẩu</label>
                                <input type="password" name="password" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Nhập lại mật khẩu</label>
                                <input type="password" name="confirmPassword" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Ảnh đại diện</label>
                                <input type="file" name="avatar" className="form-control input-custom" onChange={change} />
                            </div>

                            <Button type="submit" className="btn btn-register w-100" disabled={loading}>
                                {loading ? "Đang đăng ký..." : "Đăng ký"}
                            </Button>
                        </Form>

                        <p className="text-center mt-3">
                            Đã có tài khoản?
                            <Link to="/login" className="fw-bold text-decoration-none ms-1">
                                Đăng nhập
                            </Link>
                        </p>
                    </div>
                </main>
            <Footer />
        </>
    );
};

export default Register;