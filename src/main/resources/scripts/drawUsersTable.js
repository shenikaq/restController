function populateTable(users) {
    const tbody = document.getElementById('usersBody');
    tbody.innerHTML = '';
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${escapeHTML(user.id.toString())}</td>
            <td>${escapeHTML(user.userName)}</td>
            <td>${escapeHTML(user.lastName)}</td>
            <td>${escapeHTML(user.age.toString())}</td>
            <td>${escapeHTML(user.email)}</td>
            <td>${escapeHTML(user.role.join(', '))}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-primary edit-btn" 
                            data-id="${user.id}"
                            data-username="${escapeHTML(user.userName)}"
                            data-lastname="${escapeHTML(user.lastName)}"
                            data-age="${user.age}"
                            data-email="${escapeHTML(user.email)}"
                            data-roles='${JSON.stringify(user.role)}'>
                        Edit
                    </button>
                    <button class="btn btn-danger delete-btn"
                            data-bs-toggle="modal" 
                            data-bs-target="#deleteUserModal"
                            data-id="${user.id}"
                            data-username="${escapeHTML(user.userName)}"
                            data-lastname="${escapeHTML(user.lastName)}"
                            data-age="${user.age}"
                            data-email="${escapeHTML(user.email)}"
                            data-roles='${JSON.stringify(user.role)}'>
                        Delete
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
    // Добавляем обработчики событий
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', handleEdit);
    });
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', handleDeleteClick);
    });
}

// Функция экранирования HTML
function escapeHTML(str) {
    return str.replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// Функция для получения данных с авторизацией
async function fetchWithAuth(url, options = {}) {
    const authToken = localStorage.getItem('authToken');
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!authToken) {
        alert('Вы не авторизованы. Пожалуйста, войдите в систему.');
        window.location.href = '/login.html';
        throw new Error('Not authenticated');
    }
    if (!userInfo.roles.includes('ROLE_ADMIN')) {
        alert('У вас нет прав администратора');
        throw new Error('Unauthorized');
    }
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authToken}`,
        ...options.headers
    };
    return fetch(url, { ...options, headers });
}

// Функция обновления таблицы
async function refreshUserTable() {
    try {
        const response = await fetchWithAuth('http://localhost:8080/admin/api/users');
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('authToken');
                localStorage.removeItem('userInfo');
                window.location.href = '/login.html';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const users = await response.json();
        populateTable(users);
    } catch (error) {
        console.error('Ошибка загрузки данных:', error);
        alert('Ошибка загрузки данных: ' + error.message);
    }
}

// Функция для получения списка пользователей
async function fetchUsers() {
    const response = await fetchWithAuth('http://localhost:8080/admin/api/users');
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
}

// Основная функция
async function loadData() {
    try {
        const data = await fetchUsers();
        populateTable(data);
    } catch (error) {
        console.error('Error loading data:', error);
        alert('Failed to load data: ' + error.message);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Проверяем аутентификацию при загрузке страницы
        const authToken = localStorage.getItem('authToken');
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        if (!authToken || !userInfo || !userInfo.roles.includes('ROLE_ADMIN')) {
            alert('Доступ запрещен. Требуются права администратора.');
            window.location.href = '/login.html';
            return;
        }
        // Инициализация данных
        await loadData();
    } catch (error) {
        console.error('Initialization error:', error);
        alert('Ошибка инициализации страницы: ' + error.message);
    }
});
