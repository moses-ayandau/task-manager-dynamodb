package com.moses.todo.config;

import com.moses.todo.model.Todo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;



import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.dynamodb.endpoint:}")
    private String endpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder builder =
                software.amazon.awssdk.services.dynamodb.DynamoDbClient.builder()
                        .region(Region.of(region))
                        .credentialsProvider(DefaultCredentialsProvider.create());

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }


    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public TableSchema<Todo> todoTableSchema() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return StaticTableSchema.builder(Todo.class)
                .newItemSupplier(Todo::new)
                .addAttribute(String.class, a -> a.name("id")
                        .getter(Todo::getId)
                        .setter(Todo::setId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("title")
                        .getter(Todo::getTitle)
                        .setter(Todo::setTitle))
                .addAttribute(String.class, a -> a.name("description")
                        .getter(Todo::getDescription)
                        .setter(Todo::setDescription))
                .addAttribute(Boolean.class, a -> a.name("completed")
                        .getter(Todo::isCompleted)
                        .setter(Todo::setCompleted))
                .addAttribute(String.class, a -> a.name("createdAt")
                        .getter(t -> t.getCreatedAt() != null ? t.getCreatedAt().format(formatter) : null)
                        .setter((t, s) -> t.setCreatedAt(s != null ? LocalDateTime.parse(s, formatter) : null)))
                .addAttribute(String.class, a -> a.name("updatedAt")
                        .getter(t -> t.getUpdatedAt() != null ? t.getUpdatedAt().format(formatter) : null)
                        .setter((t, s) -> t.setUpdatedAt(s != null ? LocalDateTime.parse(s, formatter) : null)))
                .build();
    }
}