import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Apis, { authApis, endpoints } from "../../configs/Api";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import Swal from "sweetalert2";

const Profile = () => {
    const nav = useNavigate();
    const token = localStorage.getItem("polyclinic_token");
    const user = JSON.parse(localStorage.getItem("polyclinic_user") || "null");
    const [loading, setLoading] = useState(false);
    const [avatarPreview, setAvatarPreview] = useState(null);
    const [tab, setTab] = useState("info");


    const [info, setInfo] = useState({
        name: "",
        phone: "",
        email: "",
        address: "",
        gender: "",
        avatar: null
    });

    const [passwordForm, setPasswordForm] = useState({
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
    });


    useEffect(() => {
        if (!token) { nav("/login"); return; }
        authApis.get(endpoints["profile"])
            .then(res => {
                const u = res.data;
                setInfo({
                    name: u.name || "",
                    phone: u.phone || "",
                    email: u.email || "",
                    address: u.address || "",
                    gender: u.gender || "",
                    avatar: null
                });
                setAvatarPreview(u.avatar);
            })
            .catch(() => Swal.fire("Lỗi", "Không tải được thông tin", "error"));
    }, []);


    const changeInfo = (e) => {
        const { name, value, files } = e.target;
        if (name === "avatar") {
            setInfo({ ...info, avatar: files[0] });
            setAvatarPreview(URL.createObjectURL(files[0]));
        }
        setInfo({ ...info, [name]: value });
    };

    const changePw = (e) => {
        setPasswordForm({ ...passwordForm, [e.target.name]: e.target.value });
    };


    const updateInfo = async (e) => {
        e.preventDefault();

        setLoading(true);

        try {
            const form = new FormData();
            form.append("name", info.name);
            form.append("phone", info.phone);
            form.append("email", info.email);
            form.append("address", info.address);
            form.append("gender", info.gender);

            if (info.avatar)
                form.append("avatar", info.avatar);

            const res = await authApis.put(endpoints["update-profile"], form, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            const oldUser = JSON.parse(localStorage.getItem("polyclinic_user") || "{}");
            localStorage.setItem("polyclinic_user", JSON.stringify({ ...oldUser, ...res.data }));

            Swal.fire("Thành công!", "Cập nhật thông tin thành công!", "success");

        }
        catch (err) {
            Swal.fire("Lỗi", err.response?.data || "Cập nhật thất bại!", "error");
        }
        finally {
            setLoading(false);
        }
    };


    const changePassword = async (e) => {
        e.preventDefault();
        if (passwordForm.newPassword !== passwordForm.confirmNewPassword) {
            Swal.fire("Lỗi", "Mật khẩu xác nhận không khớp!", "warning");
            return;
        }
        setLoading(true);

        try {
            await authApis.put(endpoints["change-password"], passwordForm);
            Swal.fire("Thành công!", "Đổi mật khẩu thành công!", "success");
            setPasswordForm({ oldPassword: "", newPassword: "", confirmPassword: "" });
        }
        catch(err){
            Swal.fire("Lỗi", err.response?.data || "Đổi mật khẩu thất bại!", "error");
        }
        finally {
            setLoading(false);
        }

    }


    return (
        <>
            <Header/>
            <main className="profile-wrapper">
                <div className="profile-container">
                    
                </div>
            </main>
        

            <Footer/>
        </>
    );
};

export default Profile;