
function toggleSpecialty() {
    const role = document.getElementById("roleSelect").value;
    const specialtyBlock = document.getElementById("specialtyBlock");

    if (role === "ROLE_DOCTOR") {
        specialtyBlock.style.display = "block";
    } else {
        specialtyBlock.style.display = "none";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    toggleSpecialty();
    document.getElementById("roleSelect").addEventListener("change", toggleSpecialty);
});