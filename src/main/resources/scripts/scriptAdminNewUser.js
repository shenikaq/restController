// validateForm.js
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form[action*="/admin/process-register"]');
    const roleSelect = document.getElementById('role');
    
    // Инициализация Select2 для мультиселекта ролей (требует подключения библиотеки)
    const initSelect2 = () => {
        if (typeof $.fn.select2 !== 'undefined' && roleSelect) {
            $(roleSelect).select2({
                placeholder: "Select roles",
                width: '100%',
                closeOnSelect: false
            });
        }
    };

    // Валидация email
    const validateEmail = (email) => {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(String(email).toLowerCase());
    };

    // Отображение ошибок
    const showError = (fieldId, message) => {
        const field = document.getElementById(fieldId);
        if (!field) return;
        let errorElement = field.parentNode.querySelector('.error-message');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'error-message text-danger mt-1 small';
            field.parentNode.appendChild(errorElement);
        }
        errorElement.textContent = message;
    };

    // Очистка ошибок
    const clearErrors = () => {
        document.querySelectorAll('.error-message').forEach(element => {
            element.remove();
        });
    };

    // Основная валидация формы
    const validateForm = (formData) => {
        let isValid = true;
        // Валидация имени
        if (!formData.userName.trim()) {
            showError('userName', 'First name is required');
            isValid = false;
        }
        // Валидация фамилии
        if (!formData.lastName.trim()) {
            showError('lastName', 'Last name is required');
            isValid = false;
        }
        // Валидация возраста
        if (isNaN(formData.age) || formData.age < 1) {
            showError('age', 'Please enter valid age (minimum 1)');
            isValid = false;
        }
        // Валидация email
        if (!validateEmail(formData.email)) {
            showError('email', 'Please enter valid email address');
            isValid = false;
        }
        // Валидация пароля
        if (formData.password.length < 6) {
            showError('password', 'Password must be at least 6 characters');
            isValid = false;
        }
        // Валидация ролей
        if (formData.roles.length === 0) {
            showError('role', 'At least one role must be selected');
            isValid = false;
        }
        return isValid;
    };

    // Обработчик отправки формы
    const handleSubmit = (e) => {
        e.preventDefault();
        clearErrors();
        const formData = {
            userName: document.getElementById('userName').value,
            lastName: document.getElementById('lastName').value,
            age: parseInt(document.getElementById('age').value),
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            role: Array.from(roleSelect.selectedOptions).map(option => option.value)
        };
        if (validateForm(formData)) {
            form.submit();
        }
    };

    // Инициализация
    if (form) {
        initSelect2();
        form.addEventListener('submit', handleSubmit);
    }
});
