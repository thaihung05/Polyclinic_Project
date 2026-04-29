import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { authApis, endpoints } from "../../configs/Api";
import Swal from "sweetalert2";
import "./user.css";
import "../../styles/base.css";

const Login = () => {
    const nav = useNavigate();

    const [user, setUser] = useState({
        username: "",
        password: ""
    });

    const [loading, setLoading] = useState(false);

    const change = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const login = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const res = await Apis.post(endpoints.login, user);
            const token = res.data.token;

            const profileRes = await authApis(token).get(endpoints.profile);
            const profile = profileRes.data;

            localStorage.setItem('polyclinic_token', token);
            localStorage.setItem('polyclinic_user', JSON.stringify(profile));

            const role = profile.role;

            Swal.fire({
                icon: "success",
                title: "Đăng nhập thành công!"
            }).then(() => {
                if (role === "ROLE_DOCTOR") nav("/doctor/dashboard");
                else nav("/");
            });
        } catch (err) {
            console.error(err);

            Swal.fire({
                icon: "error",
                title: "Thất bại",
                text: err.response?.data?.message || "Sai tài khoản hoặc mật khẩu!"
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Header />
                <main className="login-wrapper d-flex justify-content-center align-items-center">
                    <div className="login-card">
                        <h3 className="text-center fw-bold mb-4">Đăng nhập</h3>

                        <form onSubmit={login}>
                            <div className="mb-3">
                                <label className="form-label">Tên đăng nhập</label>
                                <input
                                    type="text"
                                    name="username"
                                    className="form-control input-custom"
                                    placeholder="Nhập username"
                                    value={user.username}
                                    onChange={change}
                                    required
                                />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Mật khẩu</label>
                                <input
                                    type="password"
                                    name="password"
                                    className="form-control input-custom"
                                    placeholder="Nhập mật khẩu"
                                    value={user.password}
                                    onChange={change}
                                    required
                                />
                            </div>

                            <button className="btn btn-login w-100" disabled={loading}>
                                {loading ? "Đang đăng nhập..." : "Đăng nhập"}
                            </button>
                        </form>

                        <p className="text-center mt-3">
                            Chưa có tài khoản?
                            <Link to="/register" className="fw-bold text-decoration-none ms-1">
                                Đăng ký ngay!!!
                            </Link>
                        </p>
                    </div>
                </main>
            <Footer />
        </>
    );
};

export default Login;