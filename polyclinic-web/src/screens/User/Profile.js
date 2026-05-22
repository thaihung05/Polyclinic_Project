import { useContext, useEffect, useRef, useState } from "react";

import { authApis, endpoints } from "../../configs/Api";
import cookies from 'react-cookies';
import { MyUserContext } from "../../configs/Contexts";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Swal from "sweetalert2";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";

const Profile = () => {

    const [, dispatch] = useContext(MyUserContext);
    const [loading, setLoading] = useState(false);
    const [avatarPreview, setAvatarPreview] = useState(null);
    const [tab, setTab] = useState("infoTab");
    const [info, setInfo] = useState({});
    const [error, setError] = useState('');
    const avatar = useRef();
    const [passwordForm, setPasswordForm] = useState({});

    const formatDateOfBirth = (rawDate) => {
        if (!rawDate) return "";
        const date = new Date(rawDate);
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        return `${day}-${month}-${year}`;
    };

    const loadProfile = async () => {
        try {
            const res = await authApis().get(endpoints["profile"]);
            const u = res.data;

            setInfo({
                name: u.name || "",
                phone: u.phone || "",
                email: u.email || "",
                address: u.address || "",
                gender: u.gender || "",
                dateOfBirth: formatDateOfBirth(u.dateOfBirth) || "",
                avatar: null
            });
            setAvatarPreview(u.avatar);
        } catch (err) {
            Swal.fire("Lỗi", "Không tải được thông tin", "error");
        }
    };



    useEffect(() => {
        loadProfile();
    }, []);


    const changeInfo = (e) => {
        setInfo({ ...info, [e.target.name]: e.target.value });
    };

    const changePw = (e) => {
        setPasswordForm({ ...passwordForm, [e.target.name]: e.target.value });
    };

    const validateInfo = () => {
        if (!info.name || info.name.trim() === '') {
            setError('Họ và tên không được để trống!');
            return false;
        }
        if (info.name.trim().length < 2) {
            setError('Họ và tên phải có ít nhất 2 ký tự!');
            return false;
        }

        if (!info.gender || info.gender === '') {
            setError('Vui lòng chọn giới tính!');
            return false;
        }

        if (!info.address || info.address.trim() === '') {
            setError('Địa chỉ không được để trống!');
            return false;
        }

        if (!info.email || info.email.trim() === '') {
            setError('Email không được để trống!');
            return false;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(info.email.trim())) {
            setError('Email không hợp lệ!');
            return false;
        }

        if (!info.phone || info.phone.trim() === '') {
            setError('Số điện thoại không được để trống!');
            return false;
        }
        if (!/^0[0-9]{9}$/.test(info.phone.trim())) {
            setError('Số điện thoại không hợp lệ (phải có 10 số)!');
            return false;
        }

        // if (!dobDate) {
        //     setError('Vui lòng chọn ngày sinh!');
        //     return false;
        // }
        return true;

    }

    const updateInfo = async (e) => {
        e.preventDefault();
        if (validateInfo()) {
            let form = new FormData();
            for (let key of Object.keys(info)) {
                form.append(key, info[key]);
            }
            if (avatar.current.files.length > 0)
                form.append("avatar", avatar.current.files[0]);
            try {
                setLoading(true);

                const res = await authApis().put(endpoints["update-profile"], form, {
                    headers: { "Content-Type": "multipart/form-data" }
                });

                const updated = { ...cookies.load('info'), ...res.data };
                cookies.save('info', updated);
                dispatch({ type: "LOGIN", payload: updated });

                Swal.fire("Thành công!", "Cập nhật thông tin thành công!", "success");
                loadProfile();
            }
            catch (err) {
                Swal.fire("Lỗi", err.response?.data || "Cập nhật thất bại!", "error");
            }
            finally {
                setLoading(false);
            }

        }

    };


    const validatePassword = () => {
        if (!passwordForm.oldPassword || passwordForm.oldPassword === '') {
            setError('Mật khẩu không được để trống!');
            return false;
        }
        if (passwordForm.oldPassword.length < 6) {
            setError('Mật khẩu phải có ít nhất 6 ký tự!');
            return false;
        }

        if (!passwordForm.newPassword || passwordForm.newPassword === '') {
            setError('Mật khẩu mới không được để trống!');
            return false;
        }
        if (passwordForm.newPassword.length < 6) {
            setError('Mật khẩu mới phải có ít nhất 6 ký tự!');
            return false;
        }

        if (!passwordForm.confirmNewPassword || passwordForm.confirmNewPassword === '') {
            setError('Mật khẩu xác nhận không được để trống!');
            return false;
        }

        if (passwordForm.newPassword === passwordForm.oldPassword) {
            setError('Mật khẩu mới trùng với mật khẩu cũ!');
            return false;
        }

        if (passwordForm.newPassword !== passwordForm.confirmNewPassword) {
            setError('Mật khẩu xác nhận không khớp!');
            return false;
        }

        return true;

    }


    const changePassword = async (e) => {
        e.preventDefault();
        if (validatePassword()) {
            let form = new FormData();
            for (let key of Object.keys(passwordForm)) {
                if (key !== 'confirmNewPassword' || key!=='oldPassword') {
                    form.append(key, passwordForm[key]);
                }
            }

            try {
                setLoading(true);
                await authApis().patch(endpoints["change-password"], passwordForm);
                Swal.fire("Thành công!", "Đổi mật khẩu thành công!", "success");
                setPasswordForm({ oldPassword: "", newPassword: "", confirmNewPassword: "" });
            }
            catch (err) {
                Swal.fire("Lỗi", err.response?.data || "Đổi mật khẩu thất bại!", "error");
            }
            finally {
                setLoading(false);
            }
        }
    }


    return (
        <>
            <Header />
            <main className="profile-wrapper">
                <div className="profile-container">
                    <div className="avatar-wrapper">
                        <img src={avatarPreview} alt="avatar" className="profile-avatar" />

                        <label className="avatar-change-btn" htmlFor="avatar-input">
                            <i className="bi bi-camera-fill me-1"></i>Đổi ảnh
                        </label>
                        <input id="avatar-input" type="file" name="avatar" accept="image/*" hidden ref={avatar} />
                    </div>
                    <h4 className="mt-3 fw-bold">{info.name}</h4>
                    <p className="text-muted">{info.email}</p>
                </div>

                <div className="tab-bar mb-4">
                    <Button className={`tab-btn ${tab === "infoTab" ? "active" : ""}`} onClick={() => {setTab("infoTab"); setError('');}}>
                        <i className="bi bi-person-fill me-2"></i>Thông tin cá nhân
                    </Button>

                    <Button className={`tab-btn ${tab === "changeInfoTab" ? "active" : ""}`} onClick={() => {setTab("changeInfoTab"); setError('');}}>
                        <i className="bi bi-person-fill me-2"></i>Thay đổi thông tin
                    </Button>

                    <Button className={`tab-btn ${tab === "changePasswordTab" ? "active" : ""}`} onClick={() => {setTab("changePasswordTab"); setError('');}}>
                        <i className="bi bi-lock-fill me-2"></i>Đổi mật khẩu
                    </Button>
                </div>
                {tab === "infoTab" && (
                    <div className="profile-card">
                        <Row className="mb-3">
                            <Col md={6}>
                                <strong>Họ và tên:</strong>
                                <p>{info.name || "Chưa cập nhật"}</p>
                            </Col>

                            <Col md={6}>
                                <strong>Giới tính:</strong>
                                <p>
                                    {info.gender === "MALE" ? "Nam" : info.gender === "FEMALE" ? "Nữ" : info.gender === "OTHER" ? "Khác" : "Chưa cập nhật"}
                                </p>
                            </Col>

                            <Col md={6}>
                                <strong>Email:</strong>
                                <p>{info.email || "Chưa cập nhật"}</p>
                            </Col>

                            <Col md={6}>
                                <strong>Số điện thoại:</strong>
                                <p>{info.phone || "Chưa cập nhật"}</p>
                            </Col>

                            <Col md={6}>
                                <strong>Ngày sinh:</strong>
                                <p>{info.dateOfBirth || "Chưa cập nhật"}</p>
                            </Col>

                            <Col md={12}>
                                <strong>Địa chỉ:</strong>
                                <p>{info.address || "Chưa cập nhật"}</p>
                            </Col>
                        </Row>
                    </div>
                )}
                {tab === "changeInfoTab" && (
                    
                    <Form onSubmit={updateInfo} className="profile-card">
                        {error && <Alert variant="danger">{error}</Alert>}
                        <Row>
                            <Col md={6}>
                                <Form.Label>Họ và tên</Form.Label>
                                <Form.Control size="lg" type="text" name="name" value={info.name} onChange={changeInfo}  />
                            </Col>
                            <Col md={6}>
                                <Form.Label>Giới tính</Form.Label>
                                <Form.Select size="lg" name="gender" value={info.gender} onChange={changeInfo}>
                                    <option value="">Chọn giới tính</option>
                                    <option value="MALE">Nam</option>
                                    <option value="FEMALE">Nữ</option>
                                    <option value="OTHER">Khác</option>
                                </Form.Select>
                            </Col>
                            <Col md={6}>
                                <Form.Label>Email</Form.Label>
                                <Form.Control size="lg" type="email" name="email" value={info.email} onChange={changeInfo} />
                            </Col>
                            <Col md={6}>
                                <Form.Label>Số điện thoại</Form.Label>
                                <Form.Control size="lg" type="text" name="phone" value={info.phone} onChange={changeInfo} />
                            </Col>
                            <Col md={12}>
                                <Form.Label>Địa chỉ</Form.Label>
                                <Form.Control size="lg" type="text" name="address" value={info.address} onChange={changeInfo} />
                            </Col>
                        </Row>

                        <Button type="submit" className="btn-profile-save mt-4 w-100" disabled={loading}>
                            {loading ? "Đang lưu..." : "Lưu thay đổi"}
                        </Button>
                    </Form>
                )}

                {tab === "changePasswordTab" && (
                    <Form onSubmit={changePassword} className="profile-card">
                        {error && <Alert variant="danger">{error}</Alert>}
                        <Form.Group className="mb-3">
                            <Form.Label>Mật khẩu hiện tại</Form.Label>
                            <Form.Control size="lg" type="password" name="oldPassword" value={passwordForm.oldPassword} onChange={changePw} />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Mật khẩu mới</Form.Label>
                            <Form.Control size="lg" type="password" name="newPassword" value={passwordForm.newPassword} onChange={changePw} />
                        </Form.Group>


                        <Form.Group className="mb-3">
                            <Form.Label>Nhập lại mật khẩu mới</Form.Label>
                            <Form.Control size="lg" type="password" name="confirmNewPassword" value={passwordForm.confirmNewPassword} onChange={changePw} />
                        </Form.Group>
                        <Button type="submit" className="btn-profile-save w-100" disabled={loading}>
                            {loading ? "Đang xử lý..." : "Đổi mật khẩu"}
                        </Button>
                    </Form>
                )}
            </main>


            <Footer />
        </>
    );
};

export default Profile;