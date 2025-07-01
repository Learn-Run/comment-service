package com.example.commentservice.domain.comment.entity;

import com.example.commentservice.common.entity.BaseDocument;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document("comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseDocument {

    @Id
    private String id;
    private String commentUuid;
    private String postUuid;
    private String memberUuid;
    private String content;
    private boolean blindStatus;
    private boolean deletedStatus;
    private LocalDateTime deletedAt;


    @Builder
    public Comment(
            String commentUuid,
            String postUuid,
            String memberUuid,
            String content,
            boolean blindStatus,
            boolean deletedStatus,
            LocalDateTime deletedAt
    ) {
        this.commentUuid = commentUuid;
        this.postUuid = postUuid;
        this.memberUuid = memberUuid;
        this.content = content;
        this.blindStatus = blindStatus;
        this.deletedStatus = deletedStatus;
        this.deletedAt = deletedAt;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void softDelete() {
        this.deletedStatus = true;
        this.deletedAt = LocalDateTime.now();
    }
}
