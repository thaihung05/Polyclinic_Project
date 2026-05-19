import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { endpoints } from "../../configs/Api";
import "./home.css";
import "../../styles/base.css";
import { MyUserContext } from "../../configs/Contexts";

const Home = () => {
    const [specialties, setSpecialties] = useState([]);
    const [user,] = useContext(MyUserContext);
    useEffect(() => {
        const loadSpecialties = async () => {
            try {
                const res = await Apis.get(endpoints.specialties);
                setSpecialties(res.data);
            } catch (err) {
                console.error(err);
            }
        };

        loadSpecialties();
    }, []);

    return (
        <>
            <Header />

            <section className="hero">
                <div className="container py-5">
                    <div className="row justify-content-end">
                        <div className="col-lg-6 col-md-8">
                            <div className="hero-content">
                                <span className="hero-badge">
                                    <i className="bi bi-star-fill me-1"></i>Khai trương chi nhánh mới
                                </span>
                                <h1>KHÁM CHỮA BỆNH MIỄN PHÍ</h1>
                                <p>
                                    Tưng bừng khai trương chi nhánh, ưu đãi khách hàng có bảo hiểm y tế —
                                    tiết kiệm đến 100% chi phí khám ban đầu.
                                </p>
                                <Link to={user === null ? '/login?next=/appointment' : '/appointment'} className="btn-hero">
                                    XEM THÊM <i className="bi bi-arrow-right-circle-fill"></i>
                                </Link>
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

            <section className="py-5">
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

                    <div className="row g-4">
                        {specialties.slice(0, 6).map(c => (
                            <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6" key={c.id}>
                                <div className="service-card">
                                    <div className="service-icon"><i className="bi bi-hospital"></i></div>
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
                </div>
            </section>

            <Footer />
        </>
    );
};

export default Home;