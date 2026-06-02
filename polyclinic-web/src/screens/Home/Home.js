import { useContext, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { endpoints } from "../../configs/Api";
import "./home.css";
import "../../styles/base.css";
import { MyUserContext } from "../../configs/Contexts";
import MySpinner from "../../components/MySpinner";

const SPECIALTY_ICONS = {
    "nội tổng quát": "bi-clipboard2-pulse",
    "nhi khoa": "bi-emoji-smile",
    "da liễu": "bi-droplet-half",
    "tim mạch": "bi-heart-pulse",
    "tai mũi họng": "bi-ear",
    "sản phụ": "bi-gender-female",
};


const Home = () => {
    const [specialties, setSpecialties] = useState([]);
    const [allDoctors, setAllDoctors] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [user,] = useContext(MyUserContext);
    const navigate = useNavigate();

    const kw = searchTerm.trim().toLowerCase();
    const suggestedSpecialties = kw ? specialties.filter(s => s.name.toLowerCase().includes(kw)) : [];
    const suggestedDoctors = kw ? allDoctors.filter(d => d.userId?.name?.toLowerCase().includes(kw)) : [];

    const goToAppointment = (state) => {
        if (user === null) navigate('/login?next=/appointment');
        else navigate('/appointment', { state });
    };

    const handleSelectSpecialty = (specialty) => {
        setSearchTerm(specialty.name);
        setShowSuggestions(false);
        goToAppointment({ specialty });
    };

    const handleSelectDoctor = (doctor) => {
        setSearchTerm(doctor.userId?.name);
        setShowSuggestions(false);
        goToAppointment({ doctor, specialty: doctor.specialtyId });
    };

    const handleSearch = (e) => {
        e.preventDefault();
        if (suggestedSpecialties.length > 0) handleSelectSpecialty(suggestedSpecialties[0]);
        else if (suggestedDoctors.length > 0) handleSelectDoctor(suggestedDoctors[0]);
        else goToAppointment({});
    };

    const loadSpecialties = async () => {
        try {
            setLoading(true);
            const res = await Apis.get(endpoints['specialties']);
            setSpecialties(res.data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const loadDoctors = async () => {
        try {
            setLoading(true);
            const res = await Apis.get(endpoints['doctors']);
            const sorted = [...res.data].sort((a, b) => (b.rating || 0) - (a.rating || 0));
            setAllDoctors(sorted);
            setDoctors(sorted.slice(0, 4));
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadSpecialties();
        loadDoctors();
    }, []);

    return (
        <>
            <Header />
            <section className="hero">
                <div className="container py-5">
                    <div className="row justify-content-center">
                        <div className="col-lg-7 col-md-10 text-center">
                            <span className="hero-badge">
                                <i className="bi bi-shield-check me-1"></i>Được cấp phép bởi Bộ Y tế
                            </span>
                            <h1 className="mt-2">Đặt Lịch Khám Bệnh<br />Nhanh Chóng & Tiện Lợi</h1>
                            <p className="mb-4">
                                Kết nối với hơn 50 bác sĩ chuyên khoa — đặt lịch, thanh toán và khám trực tuyến chỉ trong vài bước.
                            </p>
                            <div className="hero-search-wrap">
                                <form className="hero-search" onSubmit={handleSearch}>
                                    <i className="bi bi-search hero-search-icon"></i>
                                    <input
                                        type="text"
                                        placeholder="Tìm theo chuyên khoa hoặc tên bác sĩ..."
                                        value={searchTerm}
                                        onChange={e => { setSearchTerm(e.target.value); setShowSuggestions(true); }}
                                        onBlur={() => setTimeout(() => setShowSuggestions(false), 150)}
                                        onFocus={() => setShowSuggestions(true)}
                                        autoComplete="off"
                                    />
                                    <button type="submit">Tìm kiếm</button>
                                </form>
                                {showSuggestions && (suggestedSpecialties.length > 0 || suggestedDoctors.length > 0) && (
                                    <ul className="hero-suggestions">
                                        {suggestedSpecialties.length > 0 && (
                                            <>
                                                <li className="hero-suggestions-header">Chuyên khoa</li>
                                                {suggestedSpecialties.map(s => (
                                                    <li key={`spec-${s.id}`} onMouseDown={() => handleSelectSpecialty(s)}>
                                                        <i className="bi bi-hospital me-2 text-muted"></i>{s.name}
                                                    </li>
                                                ))}
                                            </>
                                        )}
                                        {suggestedDoctors.length > 0 && (
                                            <>
                                                <li className="hero-suggestions-header">Bác sĩ</li>
                                                {suggestedDoctors.slice(0, 5).map(d => (
                                                    <li key={`doc-${d.id}`} onMouseDown={() => handleSelectDoctor(d)}>
                                                        <i className="bi bi-person-circle me-2 text-muted"></i>
                                                        {d.userId?.name}
                                                        <span className="text-muted ms-1" style={{ fontSize: ".8rem" }}>— {d.specialtyId?.name}</span>
                                                    </li>
                                                ))}
                                            </>
                                        )}
                                    </ul>
                                )}
                            </div>
                            <div className="hero-quick-links mt-3">
                                {specialties.slice(0, 4).map(s => (
                                    <button key={s.id} className="hero-quick-tag" onClick={() => handleSelectSpecialty(s)}>
                                        {s.name}
                                    </button>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <div className="stats-bar">
                <div className="container">
                    <div className="row text-center g-0">
                        <div className="col-6 col-md-3 stat-item py-2">
                            <div className="stat-num">15<span>+</span></div>
                            <div className="stat-label">Năm kinh nghiệm</div>
                        </div>
                        <div className="col-6 col-md-3 stat-item py-2 stat-divider">
                            <div className="stat-num">50<span>+</span></div>
                            <div className="stat-label">Bác sĩ chuyên khoa</div>
                        </div>
                        <div className="col-6 col-md-3 stat-item py-2 stat-divider">
                            <div className="stat-num">200<span>k+</span></div>
                            <div className="stat-label">Bệnh nhân tin tưởng</div>
                        </div>
                        <div className="col-6 col-md-3 stat-item py-2 stat-divider">
                            <div className="stat-num">20<span>+</span></div>
                            <div className="stat-label">Chuyên khoa</div>
                        </div>
                    </div>
                </div>
            </div>

            <section className="py-5" style={{ background: "#f5f7fa" }}>
                <div className="container">
                    <div className="section-title">
                        <span className="badge-line"></span>
                        <span style={{ color: "#1a8ccc", fontWeight: 700, fontSize: ".85rem", textTransform: "uppercase", letterSpacing: ".08em" }}>
                            Chuyên Khoa
                        </span>
                        <span className="badge-line"></span>
                        <h2>Dịch Vụ Y Tế Nổi Bật</h2>
                        <p>Đội ngũ bác sĩ giỏi, trang thiết bị hiện đại — chúng tôi luôn sẵn sàng phục vụ sức khỏe của bạn.</p>
                    </div>

                    {loading ? (
                        <div className="text-center py-4"><MySpinner /></div>
                    ) : (
                        <>
                            <div className="row g-4">
                                {specialties.slice(0, 6).map(c => (
                                    <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6" key={c.id}>
                                        <div className="service-card">
                                            <div className="service-icon"><i className={`bi ${SPECIALTY_ICONS[c.name.toLowerCase()] || "bi-hospital"}`}></i></div>
                                            <h5>{c.name}</h5>
                                            <p>{c.description}</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                            <div className="text-center mt-4">
                                <Link to={user === null ? '/login?next=/appointment' : '/appointment'} className="btn btn-outline-primary px-4 rounded-pill">
                                    Đặt lịch khám <i className="bi bi-arrow-right ms-1"></i>
                                </Link>
                            </div>
                        </>
                    )}
                </div>
            </section>

            <section className="py-5" style={{ background: "#f8fbff" }}>
                <div className="container">
                    <div className="section-title">
                        <span className="badge-line"></span>
                        <span style={{ color: "#1a8ccc", fontWeight: 700, fontSize: ".85rem", textTransform: "uppercase", letterSpacing: ".08em" }}>
                            Đội ngũ y tế
                        </span>
                        <span className="badge-line"></span>
                        <h2>Bác Sĩ Nổi Bật</h2>
                        <p>Đội ngũ bác sĩ giàu kinh nghiệm, tận tâm với bệnh nhân.</p>
                    </div>
                    {loading ? (
                        <div className="text-center py-4"><MySpinner /></div>
                    ) : (
                        <div className="row g-4 justify-content-center">
                            {doctors.map(d => (
                                <div className="col-lg-3 col-md-6 col-sm-6" key={d.id}>
                                    <div className="featured-doctor-card">
                                        <div className="featured-doctor-avatar-wrap">
                                            <img
                                                src={d.userId?.avatar || "https://via.placeholder.com/120?text=BS"}
                                                alt={d.userId?.name}
                                                referrerPolicy="no-referrer"
                                                className="featured-doctor-avatar"
                                            />
                                        </div>
                                        <div className="featured-doctor-body">
                                            <div className="featured-doctor-name">{d.userId?.name}</div>
                                            <div className="featured-doctor-specialty">
                                                <i className="bi bi-heart-pulse me-1"></i>
                                                {d.specialtyId?.name}
                                            </div>
                                            {d.rating && (
                                                <div className="featured-doctor-rating">
                                                    {[1, 2, 3, 4, 5].map(star => (
                                                        <i key={star} className={`bi ${star <= Math.round(d.rating) ? "bi-star-fill" : "bi-star"}`}></i>
                                                    ))}
                                                    <span className="ms-1">{Number(d.rating).toFixed(1)}</span>
                                                </div>
                                            )}
                                            {d.consultationFee && (
                                                <div className="featured-doctor-fee">
                                                    <i className="bi bi-cash-coin me-1"></i>
                                                    {Number(d.consultationFee).toLocaleString("vi-VN")} VNĐ
                                                </div>
                                            )}
                                            <Link
                                                to={user === null ? '/login?next=/appointment' : '/appointment'}
                                                className="btn-book-doctor"
                                            >
                                                <i className="bi bi-calendar2-check me-1"></i>Đặt lịch
                                            </Link>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </section>

            <section className="cta-banner">
                <div className="container text-center">
                    <h2>Sẵn sàng chăm sóc sức khỏe của bạn?</h2>
                    <p>Đặt lịch ngay hôm nay — nhanh chóng, tiện lợi và an toàn.</p>
                    <Link
                        to={user === null ? '/login?next=/appointment' : '/appointment'}
                        className="cta-btn"
                    >
                        Đặt lịch khám ngay <i className="bi bi-arrow-right-circle-fill ms-2"></i>
                    </Link>
                </div>
            </section>

            <Footer />
        </>
    );
};

export default Home;