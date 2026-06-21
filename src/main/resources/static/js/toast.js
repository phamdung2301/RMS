// Custom Premium Toast Notification System

// Auto-inject mobile viewport meta tag immediately to prevent zooming
if (!document.querySelector('meta[name="viewport"]')) {
    const meta = document.createElement('meta');
    meta.name = 'viewport';
    meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';
    document.head.appendChild(meta);
}

document.addEventListener("DOMContentLoaded", () => {
    if (!document.getElementById("toast-container")) {
        const container = document.createElement("div");
        container.id = "toast-container";
        container.className = "toast-container";
        document.body.appendChild(container);
    }


});

window.showToast = function(message, type = 'info', duration = 3500) {
    let container = document.getElementById("toast-container");
    if (!container) {
        container = document.createElement("div");
        container.id = "toast-container";
        container.className = "toast-container";
        document.body.appendChild(container);
    }
    
    const toast = document.createElement("div");
    toast.className = `toast-card toast-${type}`;
    
    let icon = "🔔";
    if (type === 'success') icon = "✅";
    else if (type === 'error') icon = "❌";
    else if (type === 'warning') icon = "⚠️";
    else if (type === 'info') icon = "ℹ️";
    
    toast.innerHTML = `
        <span class="toast-icon">${icon}</span>
        <span class="toast-message">${message}</span>
        <button class="toast-close" onclick="this.parentElement.classList.remove('show'); setTimeout(() => this.parentElement.remove(), 400)">×</button>
    `;
    
    container.appendChild(toast);
    
    // Trigger transition
    setTimeout(() => {
        toast.classList.add("show");
    }, 10);
    
    // Auto remove
    setTimeout(() => {
        toast.classList.remove("show");
        setTimeout(() => {
            toast.remove();
        }, 400);
    }, duration);
};

// Override default window.alert to automatically use showToast
window.alert = function(message) {
    let type = 'info';
    const lowerMessage = message.toLowerCase();
    if (lowerMessage.includes("thành công") || lowerMessage.includes("hoàn thành") || lowerMessage.includes("thêm món") || lowerMessage.includes("bưng ra") || lowerMessage.includes("đã chuyển")) {
        type = 'success';
    } else if (lowerMessage.includes("cảnh báo") || lowerMessage.includes("chống") || lowerMessage.includes("đang hiển thị") || lowerMessage.includes("đang xuất") || lowerMessage.includes("lập phiếu")) {
        type = 'warning';
    } else if (lowerMessage.includes("lỗi") || lowerMessage.includes("thất bại") || lowerMessage.includes("yêu cầu") || lowerMessage.includes("không hợp lệ") || lowerMessage.includes("chặn") || lowerMessage.includes("nhập đầy đủ")) {
        type = 'error';
    }
    window.showToast(message, type);
};
