package com.example.commentservice.common.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
public abstract class BaseDocument {

    @CreatedDate
    @Field("created_at")
    public LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    public LocalDateTime updatedAt;
}
