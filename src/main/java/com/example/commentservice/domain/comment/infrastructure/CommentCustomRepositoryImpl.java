package com.example.commentservice.domain.comment.infrastructure;

import com.example.commentservice.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Comment> findCommentByPostUuid(String postUuid, Pageable pageable) {
        // 삭제되지 않은 댓글만 조회하도록 필터링 추가
        Query baseQuery = new Query(Criteria.where("postUuid").is(postUuid)
                .and("deletedStatus").is(false));

        long total = mongoTemplate.count(baseQuery, Comment.class);
        List<Comment> comments = mongoTemplate.find(baseQuery.with(pageable), Comment.class);
        return new PageImpl<>(comments, pageable, total);
    }
}
