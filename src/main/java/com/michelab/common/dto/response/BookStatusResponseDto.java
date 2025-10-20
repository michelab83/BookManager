package com.michelab.common.dto.response;

import java.time.LocalDateTime;

public class BookStatusResponseDto extends BaseResponse{
    private BookResponseDto bookResponse;
    private Long userId;

    private LocalDateTime borrowedDate;

    private LocalDateTime returnedDate;

    public BookStatusResponseDto(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public BookStatusResponseDto(BookResponseDto bookResponse, Long userId, LocalDateTime borrowedDate, LocalDateTime returnedDate) {
        super();
        this.bookResponse = bookResponse;
        this.userId = userId;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;

    }

    public BookResponseDto getBookResponse() {
        return bookResponse;
    }

    public void setBookResponse(BookResponseDto bookResponse) {
        this.bookResponse = bookResponse;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }
}
