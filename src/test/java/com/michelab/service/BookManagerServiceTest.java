package com.michelab.service;

import com.michelab.common.dto.request.CreateBookRequestDto;
import com.michelab.common.dto.response.BookResponseDto;
import com.michelab.common.dto.response.BooksResponseDto;
import com.michelab.common.mapper.Mapper;
import com.michelab.data.model.BookEntity;
import com.michelab.data.repository.BookRepository;
import com.michelab.data.repository.BookTransactionRepository;
import com.michelab.data.repository.UserBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookManagerServiceTest {

    private BookManagerService bookManagerService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private BookTransactionRepository bookTransactionRepository;

    @Mock
    private Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookManagerService = new BookManagerService(bookRepository, bookTransactionRepository, userBookRepository, mapper);
    }

    @Test
    void shouldReturnErrorWhenYearIsNotNumeric() {
        CreateBookRequestDto req = new CreateBookRequestDto("Title", "Author", "1234567890123", "NotAYear");

        BooksResponseDto response = bookManagerService.createBooks(List.of(req));

        assertEquals(1, response.getBooks().size());
        BookResponseDto bookResponse = response.getBooks().get(0);
        assertEquals("ERROR", bookResponse.getErrorCode());
        assertEquals("Year not valid", bookResponse.getErrorMessage());

        verifyNoInteractions(bookRepository, mapper);
    }

    @Test
    void shouldCreateBookWhenYearIsValid() {
        int currentYear = LocalDateTime.now().getYear();
        CreateBookRequestDto req = new CreateBookRequestDto("Good Title", "Good Author", "9999999999", String.valueOf(currentYear));

        BookEntity savedEntity = new BookEntity("Good Title", "Good Author", "9999999999", currentYear);
        BookResponseDto mappedDto = new BookResponseDto(UUID.randomUUID(),req.getIsbn(),
            req.getTitle(), req.getAuthor(), false, req.getPublicationYear(), LocalDateTime.now(), LocalDateTime.now());

        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(mappedDto);

        BooksResponseDto response = bookManagerService.createBooks(List.of(req));

        assertEquals(1, response.getBooks().size());
        assertTrue(response.getBooks().get(0).isSuccess());


        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());
        assertEquals("Good Title", captor.getValue().getTitle());
        assertEquals(currentYear, captor.getValue().getPublicationYear());
        verify(mapper).toDto(savedEntity);
    }

    @Test
    void shouldHandleMultipleRequests() {
        int currentYear = LocalDateTime.now().getYear();
        CreateBookRequestDto valid = new CreateBookRequestDto("A", "B", "1", String.valueOf(currentYear));
        CreateBookRequestDto invalid = new CreateBookRequestDto("C", "D", "2", "1200");

        BookEntity savedEntity = new BookEntity("A", "B", "1", currentYear);
        BookResponseDto okDto = new BookResponseDto(UUID.randomUUID(),valid.getIsbn(),
            valid.getTitle(), valid.getAuthor(), false, valid.getPublicationYear(), LocalDateTime.now(), LocalDateTime.now());

        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(okDto);

        BooksResponseDto response = bookManagerService.createBooks(List.of(valid, invalid));

        assertEquals(2, response.getBooks().size());
        assertTrue( response.getBooks().get(0).isSuccess());
        assertEquals("ERROR", response.getBooks().get(1).getErrorCode());
    }

    @Test
    void testSearchBooks() {
        // given
        BookEntity book1 = new BookEntity("Book One", "Author A", "1234567890123",2022);
        BookEntity book2 = new BookEntity("Book Two", "Author B", "9876543210123", 1999);

        BookResponseDto dto1 = new BookResponseDto(UUID.randomUUID(),book1.getIsbn(),
            book1.getTitle(), book1.getAuthor(), false, book1.getPublicationYear().toString(), LocalDateTime.now(), LocalDateTime.now());

        BookResponseDto dto2 = new BookResponseDto(UUID.randomUUID(),book2.getIsbn(),
            book2.getTitle(), book2.getAuthor(), false, book2.getPublicationYear().toString(), LocalDateTime.now(), LocalDateTime.now());


        when(bookRepository.searchBooks("Book", null, null))
            .thenReturn(List.of(book1, book2));
        when(mapper.toDto(book1)).thenReturn(dto1);
        when(mapper.toDto(book2)).thenReturn(dto2);

        // when
        BooksResponseDto response = bookManagerService.searchBooks("Book", null, null);

        // then
        assertEquals(2, response.getBooks().size());
        assertEquals("Book One", response.getBooks().get(0).getTitle());
        assertEquals("Book Two", response.getBooks().get(1).getTitle());

        verify(bookRepository, times(1)).searchBooks("Book", null, null);
        verify(mapper, times(1)).toDto(book1);
        verify(mapper, times(1)).toDto(book2);
    }
}
