
// Утилиты для работы с JWT
function isTokenExpired(token) {
    if (!token) return true;
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.exp * 1000 < Date.now();
    } catch {
        return true;
    }
}

function isValidToken(token) {
    if (!token) return false;
    return token.split('.').length === 3;
}

// Функция обновления токена
async function refreshToken() {
    try {
        const response = await fetch('/auth/refresh', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            }
        });
        if (response.ok) {
            const { token } = await response.json();
            localStorage.setItem('authToken', token);
            return token;
        }
    } catch (error) {
        console.error('Не удалось обновить токен:', error);
    }
    return null;
}

// Универсальная функция загрузки навбаров
async function loadNavbar(url, containerId) {
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP error ${response.status}`);
        document.getElementById(containerId).innerHTML = await response.text();
    } catch (error) {
        console.error(`Failed to load ${url}:`, error);
        document.getElementById(containerId).innerHTML = 
            `<div class="alert alert-danger">Error loading navigation: ${error.message}</div>`;
    }
}

// Функция экранирования HTML
function escapeHTML(str) {
    if (!str) return '';
    return str.toString()
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// Функция для отображения информации об админе
function displayAdminInfo(adminData) {
    const tbody = document.getElementById('usersBody');
    tbody.innerHTML = '';
    const row = document.createElement('tr');
    row.innerHTML = `
        <td>${escapeHTML(adminData.id?.toString())}</td>
        <td>${escapeHTML(adminData.username)}</td>
        <td>${escapeHTML(adminData.lastName)}</td>
        <td>${escapeHTML(adminData.age?.toString())}</td>
        <td>${escapeHTML(adminData.email)}</td>
        <td>${Array.isArray(adminData.roles) ? 
              escapeHTML(adminData.roles.join(', ')) : 
              escapeHTML(adminData.authorities?.join(', '))}</td>
    `;
    tbody.appendChild(row);
}

// Функция загрузки данных администратора с обработкой токена
async function fetchAdminData() {
    let token = localStorage.getItem('authToken');
    // Проверка и обновление токена при необходимости
    if (isTokenExpired(token)) {
        token = await refreshToken();
        if (!token) throw new Error('Токен недействителен или истек');
    }
    const response = await fetch('http://localhost:8080/user', {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
    if (response.status === 401) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userInfo');
        window.location.href = './login.html';
        return;
    }
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
}

// Основная функция загрузки данных с улучшенной обработкой ошибок
async function loadAdminData() {
    try {
        const adminData = await fetchAdminData();
        const formattedData = {
            id: adminData.id,
            username: adminData.userName,
            lastName: adminData.lastName,
            age: adminData.age,
            email: adminData.email,
            roles: adminData.authorities || adminData.role
        };
        displayAdminInfo(formattedData);
    } catch (error) {
        console.error('Error loading admin data:', error);
        if (error.message.includes('expired') || error.message.includes('invalid')) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            window.location.href = './login.html';
            return;
        }
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        if (userInfo) {
            displayAdminInfo({
                id: 'N/A',
                username: userInfo.username,
                lastName: 'N/A',
                age: 'N/A',
                email: 'N/A',
                roles: userInfo.roles
            });
        } else {
            alert('Session expired. Please login again.');
            window.location.href = './login.html';
        }
    }
}

// Функция выхода
function logout() {
    fetch('/logout', { method: 'POST' })
        .then(() => {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            window.location.href = './login.html';
        });
}

// Функция обновления активного состояния
function updateActiveState() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        const linkPath = new URL(link.href).pathname;
        link.classList.toggle('active', linkPath === currentPath);
    });
}

// Основной обработчик загрузки страницы
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        const authToken = localStorage.getItem('authToken');
        // Улучшенная проверка авторизации
        if (!userInfo || !authToken || isTokenExpired(authToken)) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            window.location.href = './login.html';
            return;
        }
        // Параллельная загрузка навбаров
        await Promise.all([
            loadNavbar('../templates/navbarHead.html', 'navbar-container-head'),
            loadNavbar('../templates/navbarSecurityUser.html', 'navbar-container-user')
        ]);
        // Обновление информации пользователя
        document.getElementById('auth-name').textContent = userInfo.username;
        const rolesElement = document.getElementById('auth-roles');
        // console.info(userInfo.roles);
        rolesElement.textContent = Array.isArray(userInfo.roles) ? userInfo.roles.join(', ') : 'No roles assigned';
        // Загрузка данных и обновление интерфейса
        await loadAdminData();
        updateActiveState();
    } catch (error) {
        console.error('Initialization error:', error);
        localStorage.removeItem('authToken');
        localStorage.removeItem('userInfo');
        window.location.href = './login.html';
    }
});
