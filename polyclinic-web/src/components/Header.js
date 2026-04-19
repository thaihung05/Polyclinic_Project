import { Link } from "react-router-dom";

const Header = () => {
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
                            <li className="nav-item"><Link className="nav-link" to="/login">Đăng Nhập</Link></li>
                            <li className="nav-item"><Link className="nav-link" to="/register">Đăng Ký</Link></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </>
    )
}

export default Header;