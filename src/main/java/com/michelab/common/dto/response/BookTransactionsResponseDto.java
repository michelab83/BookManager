package com.michelab.common.dto.response;

import java.util.List;

public class BookTransactionsResponseDto extends BaseResponse {

    public BookTransactionsResponseDto(List<BookTransactionResponseDto> bookTransactionResponseDtos) {
        super();
        this.bookTransactionResponseDtoList = bookTransactionResponseDtos;
    }

    public BookTransactionsResponseDto(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    private List<BookTransactionResponseDto> bookTransactionResponseDtoList;

    public List<BookTransactionResponseDto> getBookTransactionResponseDtoList() {
        return bookTransactionResponseDtoList;
    }
    public void setBookTransactionResponseDtoList(List<BookTransactionResponseDto> bookTransactionResponseDtoList) {
        this.bookTransactionResponseDtoList = bookTransactionResponseDtoList;
    }
}
