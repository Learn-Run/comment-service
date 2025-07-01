package com.example.commentservice.domain.comment.application;

import com.example.commentservice.client.post.PostServiceClient;
import com.example.commentservice.client.post.dto.out.ExistsPostResDto;
import com.example.commentservice.common.entity.BaseResponseEntity;
import com.example.commentservice.common.exception.BaseException;
import com.example.commentservice.common.kafka.event.CommentCreatedEvent;
import com.example.commentservice.common.kafka.event.CommentDeletedEvent;
import com.example.commentservice.common.kafka.util.KafkaProducer;
import com.example.commentservice.common.response.BaseResponseStatus;
import com.example.commentservice.domain.comment.dto.in.CommentCreateReqDto;
import com.example.commentservice.domain.comment.dto.in.CommentDeleteReqDto;
import com.example.commentservice.domain.comment.dto.in.CommentUpdateReqDto;
import com.example.commentservice.domain.comment.dto.out.CommentListPageResDto;
import com.example.commentservice.domain.comment.dto.out.CommentResDto;
import com.example.commentservice.domain.comment.entity.Comment;
import com.example.commentservice.domain.comment.entity.CommentSortType;
import com.example.commentservice.domain.comment.infrastructure.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostServiceClient postServiceClient;
    private final KafkaProducer kafkaProducer;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public void createComment(CommentCreateReqDto commentCreateReqDto) {
        // 외부 API 호출을 트랜잭션 외부에서 수행
        validatePostExists(commentCreateReqDto);
        createCommentInternal(commentCreateReqDto);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createCommentInternal(CommentCreateReqDto commentCreateReqDto) {
        Comment comment = commentRepository.save(commentCreateReqDto.toEntity());
        // 트랜잭션 커밋 후 이벤트 발행
        applicationEventPublisher.publishEvent(CommentCreatedEvent.from(comment));
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateComment(CommentUpdateReqDto commentUpdateReqDto) {
        Comment comment = commentRepository.findByCommentUuidAndDeletedStatusFalse(commentUpdateReqDto.getCommentUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));

        validateCommentOwner(comment, commentUpdateReqDto.getMemberUuid());

        comment.updateContent(commentUpdateReqDto.getContent());
        commentRepository.save(comment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteComment(CommentDeleteReqDto commentDeleteReqDto) {
        Comment comment = commentRepository.findByCommentUuidAndDeletedStatusFalse(commentDeleteReqDto.getCommentUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));
        validateCommentOwner(comment, commentDeleteReqDto.getMemberUuid());
        comment.softDelete();
        commentRepository.save(comment);
        // 트랜잭션 커밋 후 이벤트 발행
        applicationEventPublisher.publishEvent(CommentDeletedEvent.from(comment));
    }



    @Override
    public CommentResDto getCommentByCommentUuid(String commentUuid) {
        Comment comment = commentRepository.findByCommentUuidAndDeletedStatusFalse(commentUuid)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT));
        return CommentResDto.from(comment);
    }

    @Override
    public CommentListPageResDto getCommentsByPostUuid(String postUuid, int page, CommentSortType commentSortType) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, commentSortType.getSort());
        Page<Comment> resultPage = commentRepository.findCommentByPostUuid(postUuid, pageable);

        List<CommentResDto> comments = resultPage.getContent().stream()
                .map(CommentResDto::from)
                .toList();

        return new CommentListPageResDto(
                comments, page, resultPage.getSize(), resultPage.hasNext(), resultPage.getTotalPages(),
                resultPage.getTotalElements()
        );
    }

    private static void validateCommentOwner(Comment comment, String memberUuid) {
        if (comment == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_COMMENT);
        }
        if (!comment.getMemberUuid().equals(memberUuid)) {
            throw new BaseException(BaseResponseStatus.INVALID_USER_ROLE);
        }
    }

    @Async("externalApiExecutor")
    public void validatePostExists(CommentCreateReqDto commentCreateReqDto) {
        try {
            BaseResponseEntity<ExistsPostResDto> response = postServiceClient.existsPost(commentCreateReqDto.getPostUuid());
            if (!response.result().isExistsPost()) {
                throw new BaseException(BaseResponseStatus.POST_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("게시글 존재 여부 확인 실패: postUuid={}, error={}", 
                     commentCreateReqDto.getPostUuid(), e.getMessage(), e);
            throw new BaseException(BaseResponseStatus.POST_NOT_FOUND);
        }
    }
}
