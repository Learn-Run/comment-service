package com.example.commentservice.domain.comment.application;

import com.example.commentservice.common.kafka.event.CommentCreatedEvent;
import com.example.commentservice.common.kafka.event.CommentDeletedEvent;
import com.example.commentservice.common.kafka.util.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventHandler {

    private final KafkaProducer kafkaProducer;

    /**
     * 댓글 생성 이벤트 처리
     * AFTER_COMMIT: 트랜잭션 커밋 후 실행 (데이터 일관성 보장)
     * fallbackExecution: 트랜잭션이 없는 경우에도 실행
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Async("commentEventExecutor")
    public void handleCommentCreated(CommentCreatedEvent event) {
        try {
            log.info("댓글 생성 이벤트 처리 시작: commentUuid={}", event.getCommentUuid());
            kafkaProducer.sendCommentCreatedEvent(event);
            log.info("댓글 생성 이벤트 처리 완료: commentUuid={}", event.getCommentUuid());
        } catch (Exception e) {
            log.error("댓글 생성 이벤트 처리 실패: commentUuid={}, error={}", 
                     event.getCommentUuid(), e.getMessage(), e);
            // TODO: Dead Letter Queue 또는 재시도 로직 구현
            throw new RuntimeException("댓글 생성 이벤트 처리 실패", e);
        }
    }

    /**
     * 댓글 삭제 이벤트 처리
     * AFTER_COMMIT: 트랜잭션 커밋 후 실행 (데이터 일관성 보장)
     * fallbackExecution: 트랜잭션이 없는 경우에도 실행
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Async("commentEventExecutor")
    public void handleCommentDeleted(CommentDeletedEvent event) {
        try {
            log.info("댓글 삭제 이벤트 처리 시작: commentUuid={}", event.getCommentUuid());
            kafkaProducer.sendCommentDeletedEvent(event);
            log.info("댓글 삭제 이벤트 처리 완료: commentUuid={}", event.getCommentUuid());
        } catch (Exception e) {
            log.error("댓글 삭제 이벤트 처리 실패: commentUuid={}, error={}", 
                     event.getCommentUuid(), e.getMessage(), e);
            // TODO: Dead Letter Queue 또는 재시도 로직 구현
            throw new RuntimeException("댓글 삭제 이벤트 처리 실패", e);
        }
    }

    /**
     * 트랜잭션 롤백 시 이벤트 처리 (선택적)
     * AFTER_ROLLBACK: 트랜잭션 롤백 후 실행
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleCommentEventRollback(Object event) {
        log.warn("트랜잭션 롤백으로 인한 이벤트 처리 취소: event={}", event.getClass().getSimpleName());
        // 롤백된 이벤트에 대한 로깅 또는 알림 처리
    }
} 