/* global Swal */

document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const form = this;
    const formData = new FormData(form);

    const submitBtn = document.getElementById("registerBtn");
    const spinner = document.getElementById("registerSpinner");
    const btnText = submitBtn.querySelector(".btn-text");
    submitBtn.disabled = true;
    spinner.classList.remove("d-none");
    btnText.textContent = "Đang đăng ký...";

    try {
        const res = await fetch("/Polyclinic/api/register", {
            method: "POST",
            body: formData
        });

        const data = await res.json();

        if (res.status === 201) {
            Swal.fire({
                title: "Thành công!",
                text: data.message,
                icon: "success",
                showConfirmButton: false,
                timer: 1000,
                timerProgressBar: true
            }).then(() => {
                window.location.href = "/Polyclinic/login";
            });
        }

    } catch (error) {
        Swal.fire({
            title: "Lỗi!",
            text: "Không kết nối được server!",
            icon: "error"
        });
        console.error(error);
    } finally {
        submitBtn.disabled = false;
        spinner.classList.add("d-none");
        btnText.textContent = "Đăng ký";
    }
});