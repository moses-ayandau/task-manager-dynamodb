package com.moses.todo.service;

import com.moses.todo.model.Todo;
import com.moses.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Optional<Todo> getTodoById(String id) {
        return todoRepository.findById(id);
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(Todo.create(todo));
    }

    public Optional<Todo> updateTodo(String id, Todo updatedTodo) {
        return todoRepository.findById(id)
                .map(existingTodo -> {
                    Todo updated = Todo.update(existingTodo, updatedTodo);
                    return todoRepository.save(updated);
                });
    }

    public void deleteTodo(String id) {
        todoRepository.deleteById(id);
    }
}