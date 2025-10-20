package com.michelab.common.mapper;

import com.michelab.common.dto.response.BookResponseDto;
import com.michelab.common.dto.response.BookStatusResponseDto;
import com.michelab.common.dto.response.BookTransactionResponseDto;
import com.michelab.data.model.BookEntity;
import com.michelab.data.model.BookTransactionEntity;
import com.michelab.data.model.UserBookEntity;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public BookResponseDto toDto(BookEntity bookEntity) {
        return new BookResponseDto(
            bookEntity.getBookId(),
            bookEntity.getIsbn(),
            bookEntity.getTitle(),
            bookEntity.getAuthor(),
            bookEntity.isBorrowed(),
            bookEntity.getPublicationYear().toString(),
            bookEntity.getCreatedAt(),
            bookEntity.getUpdatedAt()
        );
    }

    public BookStatusResponseDto toDto(UserBookEntity userBookEntity){
        return new BookStatusResponseDto(
            toDto(userBookEntity.getBook()),
            userBookEntity.getUserId(),
            userBookEntity.getBorrowedDate(),
            userBookEntity.getReturnedDate()
        );
    }

    public BookTransactionResponseDto toDto(BookTransactionEntity entity) {
        return new BookTransactionResponseDto(
            entity.getBook().getBookId(),
            entity.getBook().getTitle(),
            entity.getBook().getIsbn(),
            entity.getUserId(),
            entity.getTransactionType(),
            entity.getCreateAt()

        );
    }
}
