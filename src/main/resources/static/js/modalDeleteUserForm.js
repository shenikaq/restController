// Обработчик клика по кнопке Delete
function handleDeleteClick(event) {
    const button = event.currentTarget;
    // Заполняем данные в модальном окне
    document.getElementById('deleteId').value = button.dataset.id;
    document.getElementById('deleteUserName').value = button.dataset.username;
    document.getElementById('deleteLastName').value = button.dataset.lastname;
    document.getElementById('deleteAge').value = button.dataset.age;
    document.getElementById('deleteEmail').value = button.dataset.email;
    // Устанавливаем роли
    const roles = JSON.parse(button.dataset.roles);
    const rolesSelect = document.getElementById('deleteRoles');
    Array.from(rolesSelect.options).forEach(option => {
        option.selected = roles.includes(option.value);
    });
}

// Обработчик подтверждения удаления с авторизацией
document.getElementById('deleteUserForm').addEventListener('submit', async function(e) {
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
        alert('У вас недостаточно прав для удаления пользователей.');
        return;
    }
    const userId = document.getElementById('deleteId').value;
    const row = document.querySelector(`[data-id="${userId}"]`).closest('tr');
    try {
        const response = await fetch(`http://localhost:8080/admin/api/delete/${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            }
        });
        // Обработка ответа сервера
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('authToken');
                localStorage.removeItem('userInfo');
                window.location.href = '/login.html';
                return;
            }
            const error = await response.json();
            throw new Error(error.message || 'Ошибка удаления');
        }
        // Закрываем модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteUserModal'));
        modal.hide();
        // Удаляем строку из таблицы
        row.remove();
        // Обновляем таблицу (на всякий случай)
        await refreshUserTable();
    } catch (error) {
        console.error('Ошибка удаления:', error);
        alert(error.message || 'Ошибка при удалении пользователя');
        // Если проблема с авторизацией - перенаправляем на логин
        if (error.message.includes('401') || error.message.includes('403')) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userInfo');
            window.location.href = '/login.html';
        }
    }
});
