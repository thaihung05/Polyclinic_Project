
function setBlockDisabled(block, disabled) {
    if (!block) return;
    block.querySelectorAll('input, select, textarea').forEach(function(el) {
        el.disabled = disabled;
    });
}

function toggleRoleBlocks() {
    const role = document.getElementById("roleSelect").value;
    const specialtyBlock = document.getElementById("specialtyBlock");
    const doctorBlock = document.getElementById("doctorBlock");
    const patientBlock = document.getElementById("patientBlock");

    const isDoctor  = role === "ROLE_DOCTOR";
    const isPatient = role === "ROLE_PATIENT";

    if (specialtyBlock) {
        specialtyBlock.style.display = isDoctor ? "block" : "none";
        setBlockDisabled(specialtyBlock, !isDoctor);
    }
    if (doctorBlock) {
        doctorBlock.style.display = isDoctor ? "block" : "none";
        setBlockDisabled(doctorBlock, !isDoctor);
    }
    if (patientBlock) {
        patientBlock.style.display = isPatient ? "block" : "none";
        setBlockDisabled(patientBlock, !isPatient);
    }
}

document.addEventListener("DOMContentLoaded", function () {
    toggleRoleBlocks();
    const roleSelect = document.getElementById("roleSelect");
    if (roleSelect) roleSelect.addEventListener("change", toggleRoleBlocks);
});
