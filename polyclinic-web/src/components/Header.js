import { Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { useContext, useEffect, useRef, useState } from "react";
import { MyUserContext } from "../configs/Contexts";

const Header = () => {

    const [user, dispatch] = useContext(MyUserContext);
    const nav = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target))
                setShowDropdown(false);
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);


    const logout = () => {
        Swal.fire({
            title: "Đăng xuất?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Đăng xuất",
            cancelButtonText: "Hủy"
        }).then((result) => {
            if (result.isConfirmed) {
                dispatch({ type: "LOGOUT" });
                nav("/");
            }
        })
    };

    return (
        <>
            <div className="topbar">
                <div className="container d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <div className="d-flex flex-wrap gap-3">
                        <a href="tel:+84000022212">
                            <i className="bi bi-telephone-fill me-1" style={{ color: "var(--primary)" }}></i>
                            (+84) 000 022 212
                        </a>
                        <a href="mailto:thaihung.me05@gmail.com">
                            <i className="bi bi-envelope-fill me-1" style={{ color: "var(--primary)" }}></i>
                            thaihung.me05@gmail.com
                        </a>
                    </div>
                    <Link to="/appointment" className="btn-appointment">
                        <i className="bi bi-calendar2-check-fill"></i>ĐẶT LỊCH KHÁM
                    </Link>
                </div>
            </div>

            <nav className="navbar navbar-expand-lg main-nav">
                <div className="container">
                    <div className="collapse navbar-collapse show">
                        <ul className="navbar-nav me-auto">
                            <li className="nav-item"><Link className="nav-link" to="/">Trang Chủ</Link></li>
                            <li className="nav-item"><Link className="nav-link" to="/appointment">Đặt Lịch</Link></li>
                        </ul>
                        <ul className="navbar-nav ms-auto align-items-center gap-2">
                            {user ? (
                                <>
                                    <li className="nav-item">
                                        <Link className="nav-link" to="/notifications">
                                            <i className="bi bi-bell-fill"></i>
                                        </Link>
                                    </li>

                                    <li className="nav-item dropdown" ref={dropdownRef}>
                                        <a className="nav-link d-flex align-items-center gap-2" href="#"
                                            onClick={(e) => { e.preventDefault(); setShowDropdown(!showDropdown); }}>
                                            <img
                                                src={user.avatar}
                                                alt="avatar"
                                                style={{ width: 40, height: 40, borderRadius: "50%", objectFit: "cover" }}
                                            />
                                            <span className="fw-bold">{user.name}</span>
                                            <i className="bi bi-chevron-down" style={{ fontSize: 12 }}></i>
                                        </a>
                                        {showDropdown && (
                                            <ul className={`dropdown-menu dropdown-menu-end ${showDropdown ? "show" : ""}`}>
                                                <li>
                                                    <Link className="dropdown-item" to="/profile">
                                                        <i className="bi bi-person-fill me-2"></i>Hồ sơ
                                                    </Link>
                                                </li>

                                                {user.role === "ROLE_PATIENT" && (
                                                    <>
                                                        <li>
                                                            <Link className="dropdown-item" to="/patient/appointments">
                                                                <i className="bi bi-calendar-check me-2"></i>Lịch hẹn của tôi
                                                            </Link>
                                                        </li>

                                                        <li>
                                                            <Link className="dropdown-item" to="/patient/medical-history">
                                                                <i className="bi bi-file-medical me-2"></i>Lịch sử khám
                                                            </Link>
                                                        </li>
                                                    </>
                                                )}

                                                {user.role === "ROLE_DOCTOR" && (
                                                    <>
                                                        <li>
                                                            <Link className="dropdown-item" to="/doctor/dashboard">
                                                                <i className="bi bi-speedometer2 me-2"></i>Dashboard
                                                            </Link>
                                                        </li>
                                                    </>
                                                )}

                                                <li><hr className="dropdown-divider" /></li>
                                                <li>
                                                    <button className="dropdown-item text-danger" onClick={logout}>
                                                        <i className="bi bi-box-arrow-right me-2"></i>Đăng xuất
                                                    </button>
                                                </li>
                                            </ul>
                                        )}
                                    </li>
                                </>
                            ) : (
                                <>
                                    <li className="nav-item"><Link className="nav-link" to="/login">Đăng Nhập</Link></li>
                                    <li className="nav-item"><Link className="nav-link" to="/register">Đăng Ký</Link></li>
                                </>
                            )}
                        </ul>
                    </div>
                </div>
            </nav>
        </>
    )
}

export default Header;