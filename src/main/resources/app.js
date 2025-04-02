document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const todoList = document.getElementById('todo-list');
    const modal = document.getElementById('todo-modal');
    const addTodoBtn = document.getElementById('add-todo-btn');
    const closeModal = document.querySelector('.close-modal');
    const todoForm = document.querySelector('.todo-form');
    const todoId = document.getElementById('todo-id');
    const todoTitle = document.getElementById('todo-title');
    const todoDescription = document.getElementById('todo-description');
    const saveBtn = document.getElementById('save-btn');
    const cancelBtn = document.getElementById('cancel-btn');
    const modalTitle = document.getElementById('modal-title');
    const todoItemTemplate = document.getElementById('todo-item-template');

    const API_URL = '/api/v1/todos';

    // Load todos on page load
    loadTodos();

    // Modal controls
    addTodoBtn.addEventListener('click', () => {
        resetForm();
        modal.style.display = 'block';
    });

    closeModal.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    cancelBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });

    // Form submission
    todoForm.addEventListener('submit', (e) => {
        e.preventDefault();
        if (!todoTitle.value.trim()) {
            alert('Title is required');
            return;
        }

        const todo = {
            title: todoTitle.value.trim(),
            description: todoDescription.value.trim(),
            completed: false
        };

        if (todoId.value) {
            updateTodo(todoId.value, {
                ...todo,
                completed: document.querySelector(`[data-id="${todoId.value}"] .todo-checkbox`).checked
            });
        } else {
            createTodo(todo);
        }
    });

    // Todo list interactions
    todoList.addEventListener('click', function(e) {
        const todoItem = e.target.closest('.todo-item');
        if (!todoItem) return;

        const id = todoItem.dataset.id;

        if (e.target.closest('.edit-btn')) {
            const title = todoItem.querySelector('.todo-title').textContent;
            const description = todoItem.querySelector('.todo-description').textContent;

            todoId.value = id;
            todoTitle.value = title;
            todoDescription.value = description;
            modalTitle.textContent = 'Edit Todo';
            saveBtn.textContent = 'Update Todo';
            modal.style.display = 'block';
        }

        if (e.target.closest('.delete-btn')) {
            if (confirm('Are you sure you want to delete this todo?')) {
                deleteTodo(id);
            }
        }

        if (e.target.classList.contains('todo-checkbox')) {
            const isCompleted = e.target.checked;
            const titleEl = todoItem.querySelector('.todo-title');
            titleEl.classList.toggle('completed', isCompleted);

            const title = titleEl.textContent;
            const description = todoItem.querySelector('.todo-description').textContent;

            updateTodo(id, { title, description, completed: isCompleted });
        }
    });

    // API Functions (unchanged from your original)
    async function loadTodos() {
        try {
            const response = await fetch(API_URL);
            if (!response.ok) throw new Error('Failed to fetch todos');
            const todos = await response.json();
            renderTodos(todos);
        } catch (error) {
            console.error('Error loading todos:', error);
            showError('Failed to load todos. Please try again later.');
        }
    }

    async function createTodo(todo) {
        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(todo)
            });
            if (!response.ok) throw new Error('Failed to create todo');
            const newTodo = await response.json();
            addTodoToList(newTodo);
            modal.style.display = 'none';
            resetForm();
        } catch (error) {
            console.error('Error creating todo:', error);
            showError('Failed to create todo. Please try again.');
        }
    }

    async function updateTodo(id, todo) {
        try {
            const response = await fetch(`${API_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(todo)
            });
            if (!response.ok) throw new Error('Failed to update todo');
            const updatedTodo = await response.json();
            updateTodoInList(updatedTodo);
            modal.style.display = 'none';
            resetForm();
        } catch (error) {
            console.error('Error updating todo:', error);
            showError('Failed to update todo. Please try again.');
        }
    }

    async function deleteTodo(id) {
        try {
            const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('Failed to delete todo');
            removeTodoFromList(id);
        } catch (error) {
            console.error('Error deleting todo:', error);
            showError('Failed to delete todo. Please try again.');
        }
    }

    // UI Functions (mostly unchanged)
    function renderTodos(todos) {
        todoList.innerHTML = todos.length === 0
            ? '<div class="empty-list">No todos found. Add a new one!</div>'
            : '';
        todos.forEach(addTodoToList);
    }

    function addTodoToList(todo) {
        const emptyList = document.querySelector('.empty-list');
        if (emptyList) emptyList.remove();

        const todoEl = document.importNode(todoItemTemplate.content, true);
        const todoItem = todoEl.querySelector('.todo-item');

        todoItem.dataset.id = todo.id;
        todoItem.querySelector('.todo-title').textContent = todo.title;
        todoItem.querySelector('.todo-description').textContent = todo.description || 'No description';

        const checkbox = todoItem.querySelector('.todo-checkbox');
        checkbox.checked = todo.completed;
        if (todo.completed) {
            todoItem.querySelector('.todo-title').classList.add('completed');
        }

        if (todo.createdAt) {
            todoItem.querySelector('.created-date').textContent = `Created: ${formatDate(todo.createdAt)}`;
        }
        if (todo.updatedAt) {
            todoItem.querySelector('.updated-date').textContent = `Updated: ${formatDate(todo.updatedAt)}`;
        }

        todoList.appendChild(todoItem);
    }

    function updateTodoInList(todo) {
        const todoItem = document.querySelector(`.todo-item[data-id="${todo.id}"]`);
        if (!todoItem) return;

        todoItem.querySelector('.todo-title').textContent = todo.title;
        todoItem.querySelector('.todo-description').textContent = todo.description || 'No description';
        const titleEl = todoItem.querySelector('.todo-title');
        titleEl.classList.toggle('completed', todo.completed);
        todoItem.querySelector('.todo-checkbox').checked = todo.completed;

        if (todo.updatedAt) {
            todoItem.querySelector('.updated-date').textContent = раннее `Updated: ${formatDate(todo.updatedAt)}`;
        }
    }

    function removeTodoFromList(id) {
        const todoItem = document.querySelector(`.todo-item[data-id="${id}"]`);
        if (todoItem) {
            todoItem.remove();
            if (!todoList.children.length) {
                todoList.innerHTML = '<div class="empty-list">No todos found. Add a new one!</div>';
            }
        }
    }

    function resetForm() {
        todoId.value = '';
        todoTitle.value = '';
        todoDescription.value = '';
        saveBtn.textContent = 'Add Todo';
        modalTitle.textContent = 'Add New Todo';
    }

    function showError(message) {
        alert(message);
    }

    function formatDate(dateString) {
        return new Date(dateString).toLocaleString();
    }
});