import { useEffect, useState } from "react";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { endpoints } from "../../configs/Api";
import "../../styles/base.css";
import "./appointment.css";

const Appointment = () => {
    const [specialties, setSpecialties] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [selectedSpecialty, setSelectedSpecialty] = useState(null);
    const [selectedDoctor, setSelectedDoctor] = useState(null);

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

    const chooseSpecialty = async (specialty) => {
        setSelectedSpecialty(specialty);
        setSelectedDoctor(null);

        try {
            const res = await Apis.get(`${endpoints.doctors}?specialtyId=${specialty.id}`);
            setDoctors(res.data);
        } catch (err) {
            console.error(err);
            setDoctors([]);
        }
    };

    return (
        <>
            <Header />
            <section className="appt-main py-5">
                <div className="container">
                    <div className="row g-4">
                        <div className="col-lg-8">
                            <div className="booking-card">
                                <div className="step-header mb-4">
                                    <h4>Chọn Chuyên Khoa</h4>
                                    <p>Chọn chuyên khoa và bác sĩ bạn muốn khám</p>
                                </div>

                                <div className="specialty-grid mb-4">
                                    {specialties.map(s => (
                                        <div
                                            key={s.id}
                                            className={`specialty-tile ${selectedSpecialty?.id === s.id ? "selected" : ""}`}
                                            onClick={() => chooseSpecialty(s)}
                                        >
                                            <div className="sp-icon">
                                                <i className="bi bi-hospital-fill"></i>
                                            </div>
                                            <div className="sp-name">{s.name}</div>
                                            <div className="sp-desc">{s.description}</div>
                                        </div>
                                    ))}
                                </div>

                                <div className="doctor-list">
                                    {doctors.length > 0 ? doctors.map(d => (
                                        <div
                                            key={d.id}
                                            className={`doctor-card ${selectedDoctor?.id === d.id ? "selected" : ""}`}
                                            onClick={() => setSelectedDoctor(d)}
                                        >
                                            <div className="dc-avatar">
                                                <img src={d.avatar} alt={d.name} />
                                            </div>
                                            <div className="dc-info">
                                                <div className="dc-name">{d.name}</div>
                                                <div className="dc-title">Bác sĩ chuyên khoa</div>
                                            </div>
                                        </div>
                                    )) : (
                                        <div className="text-muted">Vui lòng chọn chuyên khoa để xem bác sĩ.</div>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="col-lg-4">
                            <div className="summary-card">
                                <div className="sc-header">
                                    <i className="bi bi-clipboard2-check-fill me-2"></i>
                                    Tóm Tắt Lịch Hẹn
                                </div>

                                <div className="sc-body">
                                    <div className="sc-row">
                                        <div className="sc-label">Chuyên khoa</div>
                                        <div className="sc-value">{selectedSpecialty?.name || "Chưa chọn"}</div>
                                    </div>

                                    <div className="sc-row">
                                        <div className="sc-label">Bác sĩ</div>
                                        <div className="sc-value">{selectedDoctor?.name || "Chưa chọn"}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <Footer />
        </>
    );
};

export default Appointment;