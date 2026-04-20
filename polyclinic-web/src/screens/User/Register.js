import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { endpoints } from "../../configs/Api";
import Swal from "sweetalert2";
import "./user.css";
import "../../styles/base.css";

const Register = () => {
    const nav = useNavigate();

    const [user, setUser] = useState({
        name: "",
        phone: "",
        username: "",
        password: "",
        avatar: null
    });

    const [loading, setLoading] = useState(false);

    const change = (e) => {
        const { name, value, files } = e.target;

        if (name === "avatar")
            setUser({ ...user, avatar: files[0] });
        else
            setUser({ ...user, [name]: value });
    };

    const register = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            let form = new FormData();
            form.append("name", user.name);
            form.append("phone", user.phone);
            form.append("username", user.username);
            form.append("password", user.password);

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
                    <div className="register-card">
                        <h3 className="text-center fw-bold mb-4">Đăng ký tài khoản</h3>

                        <form onSubmit={register}>
                            <div className="mb-3">
                                <label className="form-label">Họ và tên</label>
                                <input type="text" name="name" className="form-control input-custom" required onChange={change} />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Số điện thoại</label>
                                <input type="text" name="phone" className="form-control input-custom" required onChange={change} />
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
                                <label className="form-label">Ảnh đại diện</label>
                                <input type="file" name="avatar" className="form-control input-custom" onChange={change} />
                            </div>

                            <button type="submit" className="btn btn-register w-100" disabled={loading}>
                                {loading ? "Đang đăng ký..." : "Đăng ký"}
                            </button>
                        </form>

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