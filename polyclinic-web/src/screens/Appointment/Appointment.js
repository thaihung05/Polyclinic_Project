import { useEffect, useRef, useState } from "react";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Apis, { authApis, endpoints } from "../../configs/Api";
import "../../styles/base.css";
import "./appointment.css";
import Swal from "sweetalert2";
import MySpinner from "../../components/MySpinner";
import { Button, Form, Modal } from "react-bootstrap";
import Moment from "react-moment";


const BANK_CONFIG = {
    BANKING: { code: "TCB", account: "1234567890" },
    MOMO: { code: "MOMO", account: "0123456789" },
};
const ACCOUNT_NAME = "PHONG KHAM TH VL";

const Appointment = () => {
    const [specialties, setSpecialties] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [schedules, setSchedules] = useState([]);
    const [selectedSpecialty, setSelectedSpecialty] = useState(null);
    const [selectedDoctor, setSelectedDoctor] = useState(null);
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedSchedule, setSelectedSchedule] = useState(null);
    const [symptoms, setSymptoms] = useState("");
    const [step, setStep] = useState(0);

    const [paymentMethod, setPaymentMethod] = useState("BANKING");
    const [showPaymentModal, setShowPaymentModal] = useState(false);


    const [paymentConfirming, setPaymentConfirming] = useState(false);

    const [loading, setLoading] = useState(false);
    const slideRefs = useRef([]);
    const [wrapperHeight, setWrapperHeight] = useState("auto");

    const loadSpecialties = async () => {
        try {
            setLoading(true);
            const res = await Apis.get(endpoints['specialties']);
            setSpecialties(res.data);
        } catch (err) {
            Swal.fire("Lỗi", "Không tải được các chuyên khoa", "error");
        }
        finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadSpecialties();
    }, []);


    useEffect(() => {
        const active = slideRefs.current[step];
        if (!active) return;
        const id = setTimeout(() => {
            setWrapperHeight(active.scrollHeight + "px");
        }, 0);
        return () => clearTimeout(id);
    }, [step, selectedDate, selectedSchedule, doctors, specialties, schedules]);

    const chooseSpecialty = async (specialty) => {
        try {
            setSelectedDoctor(null);
            setSchedules([]);
            setSelectedDate(null);
            setSelectedSchedule(null);
            const res = await Apis.get(`${endpoints.doctors}?specialtyId=${specialty.id}`);
            setDoctors(res.data);
            setSelectedSpecialty(specialty);
            setStep(1);
        } catch (err) {
            Swal.fire("Lỗi", "Không tải được các bác sĩ", "error");
        }

    };

    const chooseDoctor = async (doctor) => {
        try {

            const res = await Apis.get(endpoints.schedules(doctor.id));
            setSelectedDoctor(doctor);
            setSchedules(res.data.filter(s => s.isActive && new Date(s.startTime) > new Date()));
            setStep(2);
        }
        catch (err) {
            Swal.fire('Lỗi', 'Không tải được lịch làm của các bác sĩ', 'error');
        }
    }

    const groupByDate = (schedules) => {
        const groups = {};
        schedules.forEach(s => {
            const date = s.startTime.split(" ")[0];
            if (!groups[date]) groups[date] = [];
            groups[date].push(s);
        })
        return groups;
    }

    const buildPreviewQrUrl = () => {
        if (!selectedDoctor?.consultationFee) return null;
        const { code, account } = BANK_CONFIG[paymentMethod] || BANK_CONFIG.BANKING;
        const amount = selectedDoctor.consultationFee;
        const desc = encodeURIComponent("Thanh toan kham benh");
        const name = encodeURIComponent(ACCOUNT_NAME);
        return `https://img.vietqr.io/image/${code}-${account}-compact2.png?amount=${amount}&addInfo=${desc}&accountName=${name}`;
    };

    const resetAll = () => {
        setSelectedSpecialty(null);
        setSelectedDoctor(null);
        setSchedules([]);
        setSelectedDate(null);
        setSelectedSchedule(null);
        setSymptoms("");
        setDoctors([]);
        setPaymentMethod("BANKING");
        setStep(0);
    }

    const handleOpenPaymentModal = () => {
        if (!selectedDoctor) {
            Swal.fire("Thông báo", "Vui lòng chọn bác sĩ", "warning");
            return;
        }
        if (!selectedSchedule) {
            Swal.fire("Thông báo", "Vui lòng chọn ca khám", "warning");
            return;
        }
        setShowPaymentModal(true);
    };



    const confirmPayment = async () => {
        try {
            setLoading(true);
            const appointmentRes = await authApis().post(endpoints['book-appointment'], {
                doctorId: selectedDoctor.id,
                scheduleId: selectedSchedule.id,
                symptoms: symptoms.trim() || null
            });
            const appointmentId = appointmentRes.data.id;

            await authApis().post(endpoints['payment-create'], {
                appointmentId,
                method: paymentMethod
            });

            await authApis().post(endpoints['payment-confirm'], {
                appointmentId
            });
            setShowPaymentModal(false);
            resetAll();
            Swal.fire("Thành công!", "Đặt lịch và thanh toán thành công!", "success");
        } catch (err) {
            Swal.fire("Lỗi", err?.response?.data || "Thanh toán thất bại", "error");
        } finally {
            setPaymentConfirming(false);
            setLoading(false);
        }
    };

    const groupedSchedules = groupByDate(schedules);
    const availableDate = Object.keys(groupedSchedules).sort();
    const schedulesOfSelectedDate = selectedDate ? groupedSchedules[selectedDate] : [];

    if (loading) {
        return (
            <>
                <Header />
                <div className="text-center py-5"><MySpinner /></div>
                <Footer />
            </>
        );
    }
    else {
        return (
            <>
                <Header />
                <section className="appt-main py-5">
                    <div className="container">
                        <div className="row g-4">
                            <div className="col-lg-8">
                                <div className="booking-card">
                                    <div className="slides-wrapper" style={{ height: wrapperHeight }}>
                                        <div className="slides-track" style={{ transform: `translateX(-${step * 100}%)` }}>

                                            <div className="slide" ref={el => slideRefs.current[0] = el}>
                                                <div className="step-header mb-4">
                                                    <h4>Chọn Chuyên Khoa</h4>
                                                    <p>Chọn chuyên khoa muốn khám</p>
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
                                            </div>

                                            <div className="slide" ref={el => slideRefs.current[1] = el}>
                                                <div className="step-header mb-4 d-flex align-items-center gap-2">
                                                    <button className="btn-back" onClick={() => {
                                                        setStep(0);
                                                        setSelectedDoctor(null);
                                                        setSchedules([]);
                                                        setSelectedDate(null);
                                                        setSelectedSchedule(null);
                                                    }}>
                                                        <i className="bi bi-arrow-left"></i>
                                                    </button>
                                                    <h4 className="mb-0">Chọn bác sĩ</h4>
                                                </div>
                                                <div className="doctor-list mb-4">
                                                    {doctors.length === 0 ? (
                                                        <div className="text-muted">Không có bác sĩ nào trong chuyên khoa này.</div>
                                                    ) : doctors.map(d => (
                                                        <div key={d.id} onClick={() => chooseDoctor(d)}
                                                            className={`doctor-card ${selectedDoctor?.id === d.id ? "selected" : ""}`}>
                                                            <div className="dc-avatar">
                                                                <img src={d.userId?.avatar} alt={d.userId?.name} />
                                                            </div>
                                                            <div className="dc-info">
                                                                <div className="dc-name">{d.userId?.name}</div>
                                                                <div className="dc-title">Bác sĩ chuyên khoa {d.specialtyId?.name}</div>
                                                                {d.consultationFee && (
                                                                    <div className="text-danger fw-semibold small mt-1">
                                                                        {Number(d.consultationFee).toLocaleString("vi-VN")} VNĐ
                                                                    </div>
                                                                )}
                                                                {d.rating && (
                                                                    <div className="small mt-1" style={{ color: "#f59e0b" }}>
                                                                        <i className="bi bi-star-fill me-1"></i>
                                                                        {Number(d.rating).toFixed(1)} / 5.0
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>

                                            <div className="slide" ref={el => slideRefs.current[2] = el}>
                                                <div className="step-header mb-4 d-flex align-items-center gap-2">
                                                    <button className="btn-back" onClick={() => {
                                                        setStep(1);
                                                        setSelectedDate(null);
                                                        setSelectedSchedule(null);
                                                    }}>
                                                        <i className="bi bi-arrow-left"></i>
                                                    </button>
                                                    <h4 className="mb-0">Chọn ngày khám</h4>
                                                </div>
                                                <div className="date-grid mb-4">
                                                    {availableDate.map(date => (
                                                        <div key={date}
                                                            className={`date-tile ${selectedDate === date ? "selected" : ""}`}
                                                            onClick={() => {
                                                                setSelectedDate(date);
                                                                setSelectedSchedule(null);
                                                            }}
                                                        >
                                                            <Moment format="DD/MM/YYYY">{date}</Moment>
                                                        </div>
                                                    ))}
                                                </div>

                                                {selectedDate && (
                                                    <>
                                                        <hr className="my-3" />
                                                        <div className="step-header mb-4">
                                                            <h4>Chọn ca khám</h4>
                                                        </div>
                                                        <div className="schedule-grid mb-4">
                                                            {schedulesOfSelectedDate.map(s => (
                                                                <div key={s.id}
                                                                    className={`schedule-tile ${selectedSchedule?.id === s.id ? "selected" : ""}`}
                                                                    onClick={() => setSelectedSchedule(s)}
                                                                >
                                                                    <i className="bi bi-clock me-2"></i>
                                                                    <Moment format="HH:mm">{s.startTime}</Moment>
                                                                    {" - "}
                                                                    <Moment format="HH:mm">{s.endTime}</Moment>
                                                                </div>
                                                            ))}
                                                        </div>
                                                    </>
                                                )}
                                            </div>

                                        </div>
                                    </div>

                                    {selectedSchedule && (
                                        <>
                                            <hr className="my-3" />
                                            <div className="step-header mb-3">
                                                <h4>Mô tả triệu chứng</h4>
                                            </div>
                                            <Form.Group className="mb-4">
                                                <Form.Control
                                                    as="textarea"
                                                    rows={3}
                                                    placeholder="Mô tả triệu chứng của bạn (không bắt buộc)..."
                                                    value={symptoms}
                                                    onChange={e => setSymptoms(e.target.value)}
                                                />
                                            </Form.Group>
                                        </>
                                    )}
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
                                            <div className="sc-value">{selectedDoctor?.userId?.name || "Chưa chọn"}</div>
                                        </div>

                                        <div className="sc-row">
                                            <div className="sc-label">Ngày khám</div>
                                            <div className="sc-value">{selectedDate ? <Moment format="DD/MM/YYYY">{selectedDate}</Moment> : "Chưa chọn"}</div>
                                        </div>

                                        <div className="sc-row">
                                            <div className="sc-label">Ca khám</div>
                                            <div className="sc-value">
                                                {selectedSchedule
                                                    ?
                                                    <>
                                                        <Moment format="HH:mm">{selectedSchedule.startTime}</Moment>
                                                        {" - "}
                                                        <Moment format="HH:mm">{selectedSchedule.endTime}</Moment>
                                                    </>
                                                    : "Chưa chọn"}
                                            </div>
                                        </div>

                                        <div className="sc-row">
                                            <div className="sc-label">Triệu chứng</div>
                                            <div className="sc-value">{symptoms || "Chưa nhập"}</div>
                                        </div>

                                        <div className="sc-row">
                                            <div className="sc-label">Phí khám</div>
                                            <div className="sc-value text-danger fw-semibold">
                                                {selectedDoctor?.consultationFee
                                                    ? `${Number(selectedDoctor.consultationFee).toLocaleString("vi-VN")} VNĐ`
                                                    : "—"}
                                            </div>
                                        </div>
                                    </div>

                                    {selectedSchedule && (
                                        <div className="p-3">
                                            <Button variant="primary" className="w-100" onClick={handleOpenPaymentModal}>
                                                Thanh toán
                                            </Button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </section>



                <Modal show={showPaymentModal} onHide={() => setShowPaymentModal(false)} centered>
                    <Modal.Header closeButton>
                        <Modal.Title>Thanh toán lịch khám</Modal.Title>
                    </Modal.Header>


                    <Modal.Body className="text-center">

                        <p className="text-muted mb-1">
                            Thanh toán khám bệnh — Bác sĩ <strong>{selectedDoctor?.userId?.name}</strong>
                        </p>
                        <h5 className="fw-bold text-danger mb-3">
                            {selectedDoctor?.consultationFee
                                ? `${Number(selectedDoctor.consultationFee).toLocaleString("vi-VN")} VNĐ`
                                : ""}
                        </h5>
                        {buildPreviewQrUrl() && (
                            <img
                                src={buildPreviewQrUrl()}
                                alt="QR Thanh toán"
                                style={{ maxWidth: "220px", borderRadius: "12px", border: "1px solid #dbe8f3" }}
                            />
                        )}
                        <p className="mt-3 text-muted small">
                            Quét mã QR để chuyển khoản, sau đó bấm <strong>Xác nhận thanh toán</strong>.<br />
                            Sau khi thanh toán, sẽ không hoàn trả tiền vì bất kể lí do nào.
                        </p>


                    </Modal.Body>

                    <Modal.Footer>
                        <Button variant="outline-secondary" onClick={() => setShowPaymentModal(false)}>
                            Đóng
                        </Button>
                        <Button variant="success" onClick={confirmPayment} disabled={paymentConfirming}>
                            {paymentConfirming ? <MySpinner /> : "Xác nhận thanh toán"}
                        </Button>
                    </Modal.Footer>
                </Modal>

                <Footer />
            </>
        );
    }



};

export default Appointment;