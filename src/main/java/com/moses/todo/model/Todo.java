package com.moses.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Todo {
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Todo create(Todo todo) {
        LocalDateTime now = LocalDateTime.now();
        return Todo.builder()
                .id(todo.getId() == null ? UUID.randomUUID().toString() : todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Todo update(Todo existing, Todo updated) {
        return Todo.builder()
                .id(existing.getId())
                .title(updated.getTitle())
                .description(updated.getDescription())
                .completed(updated.isCompleted())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}