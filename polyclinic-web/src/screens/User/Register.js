import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { endpoints } from "../../configs/Api";
import Swal from "sweetalert2";
import "./user.css";
import "../../styles/base.css";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";

const Register = () => {
    const nav = useNavigate();

    const [user, setUser] = useState({});
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [dobDate, setDobDate] = useState(null);
    const [showCalendar, setShowCalendar] = useState(false);
    const avatar = useRef();

    const change = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const onDobChange = (date) => {
        setDobDate(date);
        const d = String(date.getDate()).padStart(2, "0");
        const m = String(date.getMonth() + 1).padStart(2, "0");
        const y = date.getFullYear();
        setUser({ ...user, dateOfBirth: `${d}-${m}-${y}` });
        setShowCalendar(false);
    };

    const validate = () => {
        if (!user.name || user.name.trim() === '') {
            setError('Họ và tên không được để trống!');
            return false;
        }
        if (user.name.trim().length < 2) {
            setError('Họ và tên phải có ít nhất 2 ký tự!');
            return false;
        }

        if (!user.gender || user.gender === '') {
            setError('Vui lòng chọn giới tính!');
            return false;
        }

        if (!user.address || user.address.trim() === '') {
            setError('Địa chỉ không được để trống!');
            return false;
        }

        if (!user.email || user.email.trim() === '') {
            setError('Email không được để trống!');
            return false;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(user.email.trim())) {
            setError('Email không hợp lệ!');
            return false;
        }

        if (!user.phone || user.phone.trim() === '') {
            setError('Số điện thoại không được để trống!');
            return false;
        }
        if (!/^0[0-9]{9}$/.test(user.phone.trim())) {
            setError('Số điện thoại không hợp lệ (phải có 10 số)!');
            return false;
        }

        if (!dobDate) {
            setError('Vui lòng chọn ngày sinh!');
            return false;
        }

        if (!user.username || user.username.trim() === '') {
            setError('Tên đăng nhập không được để trống!');
            return false;
        }
        if (user.username.trim().length < 6) {
            setError('Tên đăng nhập phải có ít nhất 6 ký tự!');
            return false;
        }
        if (!/^[a-zA-Z0-9_]+$/.test(user.username.trim())) {
            setError('Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới (_)!');
            return false;
        }

        if (!user.password || user.password === '') {
            setError('Mật khẩu không được để trống!');
            return false;
        }
        if (user.password.length < 6) {
            setError('Mật khẩu phải có ít nhất 6 ký tự!');
            return false;
        }

        if (!user.confirmPassword || user.confirmPassword === '') {
            setError('Mật khẩu xác nhận không được để trống!');
            return false;
        }
        if (user.password !== user.confirmPassword) {
            setError('Mật khẩu không khớp!');
            return false;
        }

        return true;

    }

    const register = async (e) => {
        e.preventDefault();
        
        if (validate()) {
            
            let form = new FormData();
            for (let key of Object.keys(user)) {
                if (key !== 'confirmPassword') {
                    form.append(key, user[key]);
                }
            }
            if (avatar.current.files.length > 0)
                form.append("avatar", avatar.current.files[0]);

            try {
                setLoading(true);
                const res = await Apis.post(endpoints.register, form, {
                    headers: {
                        "Content-Type": "multipart/form-data"
                    }
                });
                if (res.status === 201) {
                    Swal.fire({
                        icon: "success",
                        title: "Thành công!",
                        text: "Đăng ký thành công!"
                    }).then(() => nav("/login"));
                }
                else {
                    Swal.fire({
                        icon: "error",
                        title: "Lỗi!",
                        text: 'Hệ thống bị lỗi!'
                    });
                }


            } catch (err) {

                Swal.fire({
                    icon: "error",
                    title: "Lỗi!",
                    text: err.response?.data?.message || "Đăng ký thất bại!"
                });


            } finally {
                setLoading(false);
            }

        }


    };

    return (
        <>
            <Header />
            <main className="register-wrapper d-flex justify-content-center align-items-center">
                <div className="register-card mt-3 mb-3">
                    <h3 className="text-center fw-bold mb-4">Đăng ký tài khoản</h3>
                    {error && <Alert variant="danger">{error}</Alert>}
                    <Form onSubmit={register}>
                        <Row>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Họ và tên</label>
                                    <input type="text" name="name" className="form-control input-custom"  onChange={change} />
                                </div>
                            </Col>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Giới tính</label>
                                    <Form.Select name="gender" onChange={change}>
                                        <option value="">Chọn giới tính</option>
                                        <option value="MALE">Nam</option>
                                        <option value="FEMALE">Nữ</option>
                                        <option value="OTHER">Khác</option>
                                    </Form.Select>
                                </div>
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Địa chỉ</label>
                                    <input type="text" name="address" className="form-control input-custom" onChange={change} />
                                </div>
                            </Col>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Email</label>
                                    <input type="text" name="email" className="form-control input-custom" onChange={change} />
                                </div>
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Số điện thoại</label>
                                    <input type="text" name="phone" className="form-control input-custom" onChange={change} />
                                </div>
                            </Col>

                            <Col>
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
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Tên đăng nhập</label>
                                    <input type="text" name="username" className="form-control input-custom" onChange={change} />
                                </div>
                            </Col>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Mật khẩu</label>
                                    <input type="password" name="password" className="form-control input-custom" onChange={change} />
                                </div>
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Ảnh đại diện</label>
                                    <input type="file" name="avatar" className="form-control input-custom" ref={avatar} />
                                </div>
                            </Col>
                            <Col>
                                <div className="mb-3">
                                    <label className="form-label">Nhập lại mật khẩu</label>
                                    <input type="password" name="confirmPassword" className="form-control input-custom" onChange={change} />
                                </div>

                            </Col>
                        </Row>

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