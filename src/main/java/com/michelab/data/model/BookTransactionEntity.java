package com.michelab.data.model;


import com.michelab.common.dto.enums.TransactionType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "book_transaction")
public class BookTransactionEntity {

    @Id
    private UUID id = UUID.randomUUID();

    public BookTransactionEntity(BookEntity bookEntity, Long userId, TransactionType transactionType) {
        this.book = bookEntity;
        this.userId = userId;
        this.transactionType = transactionType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    //assuming we have a user microservice, we do not need a FK here
    // as we are only using this as a reference
    private Long userId;


    private TransactionType transactionType;

    @CreationTimestamp
    private LocalDateTime createAt;


    public BookTransactionEntity() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
