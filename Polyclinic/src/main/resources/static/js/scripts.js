document.getElementById("registerForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const form = this;
    const formData = new FormData(form);

    try {
        const res = await fetch("/Polyclinic/api/register", {
            method: "POST",
            body: formData
        });

        if (res.status === 201) {
            alert("Đăng ký thành công! 🎉");
            window.location.href = "/Polyclinic/login";
        } else {
            const err = await res.text();
            alert("Lỗi: " + err);
        }

    } catch (error) {
        alert("Không kết nối được server!");
        console.error(error);
    }
});
