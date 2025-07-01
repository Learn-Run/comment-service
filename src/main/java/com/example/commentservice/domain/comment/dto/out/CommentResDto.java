package com.example.commentservice.domain.comment.dto.out;

import com.example.commentservice.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class CommentResDto {

    private String commentUuid;
    private String postUuid;
    private String memberUuid;
    private String content;
    private boolean blindStatus;
    private boolean deletedStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CommentResDto(
            String commentUuid, String postUuid, String memberUuid, String content, boolean blindStatus,
            boolean deletedStatus, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.commentUuid = commentUuid;
        this.postUuid = postUuid;
        this.memberUuid = memberUuid;
        this.content = content;
        this.blindStatus = blindStatus;
        this.deletedStatus = deletedStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static CommentResDto from(Comment comment) {
        return CommentResDto.builder()
                .commentUuid(comment.getCommentUuid())
                .postUuid(comment.getPostUuid())
                .memberUuid(comment.getMemberUuid())
                .content(comment.getContent())
                .blindStatus(comment.isBlindStatus())
                .deletedStatus(comment.isDeletedStatus())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
