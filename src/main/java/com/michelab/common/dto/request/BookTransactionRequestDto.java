package com.michelab.common.dto.request;

import com.michelab.common.dto.enums.TransactionType;

import java.util.UUID;

public class BookTransactionRequestDto {

    public BookTransactionRequestDto(UUID bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    private UUID bookId;

    private Long userId;

    private TransactionType transactionType;

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
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
}
