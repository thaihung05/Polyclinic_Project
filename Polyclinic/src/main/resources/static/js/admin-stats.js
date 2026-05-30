const stats = window.statsData || {};

document.addEventListener('DOMContentLoaded', function () {
    const patientStats = stats.patientStats || [];
    new Chart(document.getElementById('patientChart'), {
        type: 'bar',
        data: {
            labels: patientStats.map(r => r[0] + ' | ' + r[1] + ' | ' + r[2]),
            datasets: [{
                label: 'Số lượt',
                data: patientStats.map(r => Number(r[3]))
            }]
        },
        options: {
            responsive: true,
            aspectRatio: 4,
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });

    const serviceUsageStats = stats.serviceUsageStats || [];
    new Chart(document.getElementById('serviceChart'), {
        type: 'pie',
        data: {
            labels: serviceUsageStats.map(r => r[0]),
            datasets: [{
                data: serviceUsageStats.map(r => Number(r[1]))
            }]
        },
        options: { responsive: true, aspectRatio: 3 }
    });

    const commonDiseaseStats = stats.commonDiseaseStats || [];
    new Chart(document.getElementById('diseaseChart'), {
        type: 'doughnut',
        data: {
            labels: commonDiseaseStats.map(r => r[0]),
            datasets: [{
                data: commonDiseaseStats.map(r => Number(r[1]))
            }]
        },
        options: { responsive: true, aspectRatio: 3 }
    });

    const revenueSummaryStats = stats.revenueSummaryStats || [];
    new Chart(document.getElementById('revenueSummaryChart'), {
        type: 'bar',
        data: {
            labels: revenueSummaryStats.map(r => r[0]),
            datasets: [{
                label: 'Tổng tiền (VNĐ)',
                data: revenueSummaryStats.map(r => Number(r[2]))
            }]
        },
        options: {
            responsive: true,
            aspectRatio: 4,
            scales: { y: { beginAtZero: true } }
        }
    });

    const revenueDetailStats = stats.revenueDetailStats || [];
    new Chart(document.getElementById('revenueDetailChart'), {
        type: 'line',
        data: {
            labels: revenueDetailStats.map(r => r[1]),
            datasets: [{
                label: 'Số tiền (VNĐ)',
                data: revenueDetailStats.map(r => Number(r[6])),
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

});
