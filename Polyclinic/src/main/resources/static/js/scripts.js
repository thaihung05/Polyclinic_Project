
function toggleRoleBlocks() {
    const role = document.getElementById("roleSelect").value;
    const specialtyBlock = document.getElementById("specialtyBlock");
    const doctorBlock = document.getElementById("doctorBlock");
    const patientBlock = document.getElementById("patientBlock");

    if (specialtyBlock) specialtyBlock.style.display = role === "ROLE_DOCTOR" ? "block" : "none";
    if (doctorBlock) doctorBlock.style.display = role === "ROLE_DOCTOR" ? "block" : "none";
    if (patientBlock) patientBlock.style.display = role === "ROLE_PATIENT" ? "block" : "none";
}

document.addEventListener("DOMContentLoaded", function () {
    toggleRoleBlocks();
    const roleSelect = document.getElementById("roleSelect");
    if (roleSelect) roleSelect.addEventListener("change", toggleRoleBlocks);
});
