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

// Функция выхода (вынесена в глобальную область видимости)
function logout() {
    fetch('/logout', { method: 'POST' })
        .then(() => {
            // Очищаем хранилище
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            // Редирект на логин
            window.location.href = './login.html';
        })
        .catch(error => {
            console.error('Logout error:', error);
            // Все равно перенаправляем, даже если запрос не удался
            window.location.href = './login.html';
        });
}

// Функция обновления активного состояния
function updateActiveState() {
    const currentPath = window.location.pathname;
    // Для всех навбаров
    document.querySelectorAll('.nav-link').forEach(link => {
        const linkPath = new URL(link.href).pathname;
        link.classList.toggle('active', linkPath === currentPath);
    });
}

// Основной обработчик загрузки страницы
document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Загрузка всех навбаров параллельно
        await Promise.all([
            loadNavbar('../templates/navbarHead.html', 'navbar-container-head'),
            loadNavbar('../templates/navbarSecurityAdmin.html', 'navbar-container-admin'),
            loadNavbar('../templates/navbarAdminPanel.html', 'navbar-container')
        ]);
        console.log('DOM загружен'); 
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        if(userInfo) {
            document.getElementById('auth-name').innerHTML = userInfo.username;
            // Проверяем наличие массива ролей и преобразуем в строку
            const rolesElement = document.getElementById('auth-roles');
            if(userInfo.roles && Array.isArray(userInfo.roles)) {
                rolesElement.textContent = userInfo.roles.join(', ');
            } else {
                rolesElement.textContent = 'No roles assigned';
            }
            // Назначаем обработчик для кнопки выхода
            const logoutBtn = document.getElementById('logout-btn');
            if(logoutBtn) {
                logoutBtn.addEventListener('click', logout);
            }
        } else {
            console.warn('Данные пользователя не найдены');
            window.location.href = './login.html';
        }
        // Обновление активных ссылок
        updateActiveState();
    } catch (error) {
        console.error('Initialization error:', error);
        alert('Failed to initialize page');
    }
});
