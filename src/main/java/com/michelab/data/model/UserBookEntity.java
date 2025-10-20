package com.michelab.data.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_book")
public class UserBookEntity {

    @Id
    private UUID id = UUID.randomUUID();

    public UserBookEntity() {}

    public UserBookEntity(BookEntity book, Long userId, LocalDateTime borrowedDate, LocalDateTime returnedDate) {
        this.book = book;
        this.userId = userId;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    //assuming we have a user microservice, we do not need a FK here
    // as we are only using this as a reference
    private Long userId;

    private LocalDateTime borrowedDate;

    private LocalDateTime returnedDate;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
