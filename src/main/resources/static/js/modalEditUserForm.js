document.getElementById('editUserForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    // Проверка аутентификации
    const authToken = localStorage.getItem('authToken');
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!authToken) {
        alert('Сессия истекла. Пожалуйста, войдите снова.');
        window.location.href = '/login.html';
        return;
    }
    if (!userInfo.roles.includes('ROLE_ADMIN')) {
        alert('У вас недостаточно прав для редактирования пользователей.');
        return;
    }

    try {
        const roles = Array.from(document.querySelectorAll('#editRoles option:checked'))
            .map(option => option.value);
        const userId = document.getElementById('editId').value;
        const formData = {
            id: userId,
            userName: document.getElementById('editUserName').value,
            lastName: document.getElementById('editLastName').value,
            age: parseInt(document.getElementById('editAge').value),
            email: document.getElementById('editEmail').value,
            password: document.getElementById('editPassword').value,
            roles: roles
        };
        const response = await fetch(`http://localhost:8080/admin/api/edit/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(formData)
        });
        if (!response.ok) {
            // Обработка ошибок авторизации
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('authToken');
                localStorage.removeItem('userInfo');
                window.location.href = '/login.html';
                return;
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Ошибка сервера: ${response.status}`);
        }
        const result = await response.json();
        console.log('Пользователь успешно обновлен:', result);
        const modal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
        modal.hide();
        await refreshUserTable();
    } catch (error) {
        console.error('Ошибка при обновлении пользователя:', error);
        alert(`Ошибка при обновлении пользователя: ${error.message}`);
    }
});

// Функция открытия модального окна редактирования 
function openEditModal(user) {
    document.getElementById('editId').value = user.id;
    document.getElementById('editUserName').value = user.userName;
    document.getElementById('editLastName').value = user.lastName;
    document.getElementById('editAge').value = user.age;
    document.getElementById('editEmail').value = user.email;
    document.getElementById('editPassword').value = '';
    const roleSelect = document.getElementById('editRoles');
    Array.from(roleSelect.options).forEach(option => {
        option.selected = user.role.includes(option.value);
    });
    const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
    modal.show();
}

// Обработчик клика по кнопке Edit 
function handleEdit(event) {
    const button = event.target.closest('.edit-btn');
    let rolesArray;
    try {
        rolesArray = JSON.parse(button.dataset.roles);
    } catch (e) {
        const rolesString = button.dataset.roles.replace(/[\[\]]/g, '');
        rolesArray = rolesString.split(',').map(role => role.trim());
    }
    const userData = {
        id: button.dataset.id,
        userName: button.dataset.username,
        lastName: button.dataset.lastname,
        age: parseInt(button.dataset.age),
        email: button.dataset.email,
        role: rolesArray
    };
    openEditModal(userData);
}