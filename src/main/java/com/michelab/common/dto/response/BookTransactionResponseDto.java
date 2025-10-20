package com.michelab.common.dto.response;

import com.michelab.common.dto.enums.TransactionType;


import java.time.LocalDateTime;
import java.util.UUID;

public class BookTransactionResponseDto extends BaseResponse {

    private UUID bookId;
    private String title;
    private String isbn;
    private Long userId;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;

    public BookTransactionResponseDto(UUID bookId, String title, String isbn, Long userId, TransactionType transactionType, LocalDateTime transactionDate) {
        super();
        this.bookId = bookId;
        this.title = title;
        this.userId = userId;
        this.isbn = isbn;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;

    }

    public BookTransactionResponseDto(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

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

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
