import { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Api";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import { Button, Spinner, Tab, Table, Tabs } from "react-bootstrap";
import Swal from "sweetalert2";


const MedicalHistory = () => {

    const [medicalRecords, setMedicalRecords] = useState([]);
    const [labResults, setLabResults] = useState([]);
    const [loading, setLoading] = useState(false);

    const formatDateTime = (dateStr) => {
        if (!dateStr) return "-";

        const date = new Date(dateStr);
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
    }

    const loadHistory = async () => {
        try {
            setLoading(true);
            const [recordRes, labRes] = await Promise.all([
                authApis().get(endpoints['medical-records']),
                authApis().get(endpoints['lab-results'])
            ]);
            setLabResults(labRes.data);
            setMedicalRecords(recordRes.data);
        }
        catch (err) {
            Swal.fire("Lỗi", "Không tải được lịch sử khám bệnh", "error");
        }
        finally {
            setLoading(false)
        }
    };

    useEffect(() => {
        loadHistory();
    }, []);



    if (loading) {
        return (
            <>
                <Header />
                <div className="text-center py-5"><Spinner animation="border" /></div>
                <Footer />
            </>
        );
    }
    else {
        return (
            <>
                <Header />
                <main className="container my-4">
                    <h4 className="fw-bold mb-4">Lịch sử khám bệnh</h4>
                    <Tabs defaultActiveKey="records" className="mb-3">
                        <Tab eventKey="records" title="Hồ sơ bệnh án">
                            {medicalRecords.length === 0 ? (
                                <div className="text-center text-muted py-5">Chưa có hồ sơ bệnh án nào.</div>
                            ) : (
                                <Table bordered hover responsive>
                                    <thead className="table-light">
                                        <tr>
                                            <th>Số</th>
                                            <th>Bác sĩ</th>
                                            <th>Chuyên khoa</th>
                                            <th>Ngày khám</th>
                                            <th>Triệu chứng chính</th>
                                            <th>Chẩn đoán</th>
                                            <th>Kế hoạch điều trị</th>
                                            <th>Ngày tái khám</th>
                                            <th>Ghi chú</th>
                                            <th>Đơn thuốc</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {(medicalRecords || []).map((rec, idx) => (
                                            <tr key={rec.id}>
                                                <td>{idx + 1}</td>
                                                <td>{rec.appointmentId?.doctorId?.userId?.name || "—"}</td>
                                                <td>{rec.appointmentId?.doctorId?.specialtyId?.name || "—"}</td>
                                                <td>{formatDateTime(rec.appointmentId?.scheduledAt) || "—"}</td>
                                                <td>{rec.chiefComplaint || "—"}</td>
                                                <td>{rec.diagnosis || "—"}</td>
                                                <td>{rec.treatmentPlan}</td>
                                                <td>{formatDateTime(rec.followUpDate) || "—"}</td>
                                                <td>{rec.notes || "—"}</td>
                                                <td><Button>Click me</Button></td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </Tab>
                        <Tab eventKey="lab" title="Hồ sơ xét nghiệm">
                            {labResults.length === 0 ? (
                                <div className="text-center text-muted py-5">Chưa có hồ sơ xét nghiệm nào.</div>
                            ) : (
                                <Table bordered hover responsive>
                                    <thead className="table-light">
                                        <tr>
                                            <th>Số</th>
                                            <th>Tên xét nghiệm</th>
                                            <th>Mã xét nghiệm</th>
                                            <th>Kết quả</th>
                                            <th>Đơn vị</th>
                                            <th>Ngày thực hiện</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {(labResults || []).map((lab, idx) => (
                                            <tr key={lab.id}>
                                                <td>{idx + 1}</td>
                                                <td>{lab.testName || "-"}</td>
                                                <td>{lab.testCode || "—"}</td>
                                                <td>{lab.result || "—"}</td>
                                                <td>{lab.unit || "—"}</td>
                                                <td>{formatDateTime(lab.performedAt) || "—"}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </Tab>
                    </Tabs>
                </main>
                <Footer />
            </>
        );
    }

};

export default MedicalHistory;