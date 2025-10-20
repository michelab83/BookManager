package com.michelab.common.dto.response;

import java.util.List;

public class BooksResponseDto extends BaseResponse {

    public BooksResponseDto(List<BookResponseDto> books) {
        super();
        this.books = books;
    }

    public BooksResponseDto(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
    private List<BookResponseDto> books;

    public List<BookResponseDto> getBooks() {
        return books;
    }

    public void setBooks(List<BookResponseDto> books) {
        this.books = books;
    }
}
