const stats = window.statsData || {};
const chartInstances = {};

function createChart(id, config) {
    if (chartInstances[id]) return;
    const canvas = document.getElementById(id);
    if (!canvas) return;
    chartInstances[id] = new Chart(canvas, config);
}

function buildPatientAgeChart() {
    const data = stats.patientByAgeStats || [];
    createChart('patientAgeChart', {
        type: 'bar',
        data: {
            labels: data.map(r => r[0]),
            datasets: [{ label: 'Số bệnh nhân', data: data.map(r => Number(r[1])) }]
        },
        options: {
            responsive: true,
            aspectRatio: 4,
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}

function buildPatientGenderChart() {
    const data = stats.patientByGenderStats || [];
    createChart('patientGenderChart', {
        type: 'pie',
        data: {
            labels: data.map(r => r[0]),
            datasets: [{ data: data.map(r => Number(r[1])) }]
        },
        options: { responsive: true, aspectRatio: 3 }
    });
}

function buildPatientSpecialtyChart() {
    const data = stats.patientBySpecialtyStats || [];
    createChart('patientSpecialtyChart', {
        type: 'bar',
        data: {
            labels: data.map(r => r[0]),
            datasets: [{ label: 'Số bệnh nhân', data: data.map(r => Number(r[1])) }]
        },
        options: {
            responsive: true,
            aspectRatio: 4,
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}

function buildServiceChart() {
    const data = stats.serviceUsageStats || [];
    createChart('serviceChart', {
        type: 'pie',
        data: {
            labels: data.map(r => r[0]),
            datasets: [{ data: data.map(r => Number(r[1])) }]
        },
        options: { responsive: true, aspectRatio: 3 }
    });
}

function buildDiseaseChart() {
    const data = stats.commonDiseaseStats || [];
    createChart('diseaseChart', {
        type: 'doughnut',
        data: {
            labels: data.map(r => r[0]),
            datasets: [{ data: data.map(r => Number(r[1])) }]
        },
        options: { responsive: true, aspectRatio: 3 }
    });
}

function buildRevenueSummaryChart() {
    const data = stats.revenueSummaryStats || [];
    createChart('revenueSummaryChart', {
        type: 'bar',
        data: {
            labels: data.map(r => r[0]),
            datasets: [{ label: 'Tổng tiền (VNĐ)', data: data.map(r => Number(r[2])) }]
        },
        options: {
            responsive: true,
            aspectRatio: 4,
            scales: { y: { beginAtZero: true } }
        }
    });
}

function buildRevenueDetailChart() {
    const data = stats.revenueDetailStats || [];
    createChart('revenueDetailChart', {
        type: 'line',
        data: {
            labels: data.map(r => r[1]),
            datasets: [{
                label: 'Số tiền (VNĐ)',
                data: data.map(r => Number(r[6])),
                tension: 0.3,
                fill: false
            }]
        },
        options: {
            responsive: true,
            aspectRatio: 4,
            scales: { y: { beginAtZero: true } }
        }
    });
}

const tabBuilders = {
    'patient-tab':         function() { buildPatientAgeChart(); buildPatientGenderChart(); buildPatientSpecialtyChart(); },
    'service-tab':         buildServiceChart,
    'disease-tab':         buildDiseaseChart,
    'revenue-summary-tab': buildRevenueSummaryChart,
    'revenue-detail-tab':  buildRevenueDetailChart,
};

document.addEventListener('DOMContentLoaded', function () {
    buildPatientAgeChart();
    buildPatientGenderChart();
    buildPatientSpecialtyChart();

    document.querySelectorAll('#statsTabs button[data-bs-toggle="tab"]').forEach(function(btn) {
        btn.addEventListener('shown.bs.tab', function() {
            var builder = tabBuilders[this.id];
            if (builder) builder();
        });
    });
});
