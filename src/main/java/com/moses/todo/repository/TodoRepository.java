package com.moses.todo.repository;

import com.moses.todo.model.Todo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Todo> todoTable;

    public TodoRepository(
            DynamoDbEnhancedClient enhancedClient,
            TableSchema<Todo> todoTableSchema,
            @Value("${aws.dynamodb.table-name}") String tableName) {
        this.enhancedClient = enhancedClient;
        this.todoTable = enhancedClient.table(tableName, todoTableSchema);
    }

    public Todo save(Todo todo) {
        todoTable.putItem(todo);
        return todo;
    }

    public List<Todo> findAll() {
        PageIterable<Todo> results = todoTable.scan();
        List<Todo> todos = new ArrayList<>();
        results.items().forEach(todos::add);
        return todos;
    }

    public Optional<Todo> findById(String id) {
        Todo todo = todoTable.getItem(Key.builder().partitionValue(id).build());
        return Optional.ofNullable(todo);
    }

    public void deleteById(String id) {
        todoTable.deleteItem(Key.builder().partitionValue(id).build());
    }
}