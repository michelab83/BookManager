package com.michelab.common.dto.response;


import java.time.LocalDateTime;
import java.util.UUID;

public class BookResponseDto extends BaseResponse {

    public BookResponseDto(UUID bookId, String ISBN, String title, String author, boolean isBorrowed, String year, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.ISBN = formatIsbn(ISBN);
        this.publicationYear = year;
        this.borrowed = isBorrowed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private String formatIsbn(String isbn) {
        String digits = isbn.replaceAll("\\D", "");

        if (digits.length() == 13) {
            return digits.substring(0, 3) + "-" +
                digits.charAt(3) + "-" +
                digits.substring(4, 6) + "-" +
                digits.substring(6, 12) + "-" +
                digits.substring(12);
        } else if (digits.length() == 10) {
            return digits.charAt(0) + "-" +
                digits.substring(1, 4) + "-" +
                digits.substring(4, 9) + "-" +
                digits.substring(9);
        } else {
            return isbn;
        }

    }

    public BookResponseDto(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }


    private UUID bookId;

    private String title;

    private String author;

    private String ISBN;

    private String publicationYear;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean borrowed;

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedOn(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }
}
