import { useContext, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { authApis, endpoints } from "../../configs/Api";
import Swal from "sweetalert2";
import "./user.css";
import "../../styles/base.css";
import { MyUserContext } from "../../configs/Contexts";
import cookies from 'react-cookies';
import { Alert } from "react-bootstrap";

const Login = () => {
    const [, dispatch] = useContext(MyUserContext);
    const nav = useNavigate();

    const [user, setUser] = useState({});
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();
    const change = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };


    const validate = () => {
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
        return true;

    }

    const login = async (e) => {
        e.preventDefault();
        if (validate()) {
            try {
                setLoading(true);
                const res = await Apis.post(endpoints.login, user);
                const token = res.data.token;

                cookies.save('token', token);

                const profileRes = await authApis().get(endpoints.profile);
                const profile = profileRes.data;

                cookies.save('user', profile);
                dispatch({
                    type: "LOGIN", payload: profile
                });

                let next = q.get('next');

                Swal.fire({
                    icon: "success",
                    title: "Đăng nhập thành công!"
                })

                if (profile.role === "ROLE_DOCTOR") nav("/doctor/dashboard");
                else if (next) nav(next);
                else nav('/');

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

        }

    };

    return (
        <>
            <Header />
            <main className="login-wrapper d-flex justify-content-center align-items-center">
                <div className="login-card">
                    <h3 className="text-center fw-bold mb-4">Đăng nhập</h3>
                    {error && <Alert variant="danger">{error}</Alert>}
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