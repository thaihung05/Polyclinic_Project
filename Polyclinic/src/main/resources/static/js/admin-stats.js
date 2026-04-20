function createChart(canvasId, config) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return null;
    return new Chart(canvas, config);
}

function toNumber(value) {
    if (value === null || value === undefined || value === '') return 0;
    return Number(value);
}

document.addEventListener('DOMContentLoaded', function () {
    const stats = window.statsData || {};
    const patientStats = stats.patientStats || [];
    const patientLabels = patientStats.map(item => `${item[0]} | ${item[1]} | ${item[2]}`);
    const patientData = patientStats.map(item => toNumber(item[3]));

    createChart('patientChart', {
        type: 'bar',
        data: {
            labels: patientLabels,
            datasets: [{
                label: 'Số lượng bệnh nhân',
                data: patientData,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        precision: 0
                    }
                }
            }
        }
    });

    const serviceUsageStats = stats.serviceUsageStats || [];
    const serviceLabels = serviceUsageStats.map(item => item[0]);
    const serviceData = serviceUsageStats.map(item => toNumber(item[1]));

    createChart('serviceChart', {
        type: 'pie',
        data: {
            labels: serviceLabels,
            datasets: [{
                label: 'Số lượt sử dụng',
                data: serviceData,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });

    const commonDiseaseStats = stats.commonDiseaseStats || [];
    const diseaseLabels = commonDiseaseStats.map(item => item[0]);
    const diseaseData = commonDiseaseStats.map(item => toNumber(item[1]));

    createChart('diseaseChart', {
        type: 'doughnut',
        data: {
            labels: diseaseLabels,
            datasets: [{
                label: 'Số ca bệnh',
                data: diseaseData,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });

    const revenueSummaryStats = stats.revenueSummaryStats || [];
    const revenueSummaryLabels = revenueSummaryStats.map(item => item[0]);
    const revenueSummaryData = revenueSummaryStats.map(item => toNumber(item[2]));

    createChart('revenueSummaryChart', {
        type: 'bar',
        data: {
            labels: revenueSummaryLabels,
            datasets: [{
                label: 'Tổng doanh thu',
                data: revenueSummaryData,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
    const revenueDetailStats = stats.revenueDetailStats || [];
    const revenueDetailLabels = revenueDetailStats.map(item => `Pay-${item[0]}`);
    const revenueDetailData = revenueDetailStats.map(item => toNumber(item[6]));

    createChart('revenueDetailChart', {
        type: 'line',
        data: {
            labels: revenueDetailLabels,
            datasets: [{
                label: 'Doanh thu từng giao dịch',
                data: revenueDetailData,
                tension: 0.3,
                fill: false,
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
});
