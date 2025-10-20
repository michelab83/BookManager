package com.michelab.service;

import com.michelab.common.dto.enums.TransactionType;
import com.michelab.common.dto.request.BookTransactionRequestDto;
import com.michelab.common.dto.request.CreateBookRequestDto;
import com.michelab.common.dto.response.BookResponseDto;
import com.michelab.common.dto.response.BooksResponseDto;
import com.michelab.common.mapper.Mapper;
import com.michelab.data.model.BookEntity;
import com.michelab.data.model.BookTransactionEntity;
import com.michelab.data.model.UserBookEntity;
import com.michelab.data.repository.BookRepository;
import com.michelab.data.repository.BookTransactionRepository;
import com.michelab.data.repository.UserBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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


    @Test
    void testProcessBookTransaction_BookNotFound() {
        UUID uuid = UUID.randomUUID();
        BookTransactionRequestDto dto1 = new BookTransactionRequestDto(UUID.randomUUID(), 22L);

        when(bookRepository.findById(uuid)).thenReturn(Optional.empty());

        BookResponseDto response = bookManagerService.processBookTransaction(dto1);

        assertEquals("ERROR", response.getErrorCode());
        assertEquals("Book not found", response.getErrorMessage());
    }

    @Test
    void testProcessBookTransaction_BorrowSuccess() {
        BookEntity book1 = new BookEntity("Book One", "Author A", "1234567890123",2022);
        UUID uuid = UUID.randomUUID();
        book1.setBookId(uuid);

        BookTransactionRequestDto requestDto = new BookTransactionRequestDto(uuid, 22L);
        requestDto.setTransactionType(TransactionType.BORROW);
        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book1));
        when(userBookRepository.countByUserIdAndReturnedDateIsNull(22L)).thenReturn(2L);

        when(mapper.toDto(book1)).thenReturn(  new BookResponseDto(requestDto.getBookId(),book1.getIsbn(),
            book1.getTitle(), book1.getAuthor(), false, book1.getPublicationYear().toString(), LocalDateTime.now(), LocalDateTime.now()));

        BookResponseDto response = bookManagerService.processBookTransaction(requestDto);

        verify(bookTransactionRepository).save(any(BookTransactionEntity.class));
        verify(userBookRepository).save(any(UserBookEntity.class));
        assertEquals(book1.getBookId(), response.getBookId());
        assertEquals(book1.getIsbn(), response.getISBN().replaceAll("-", ""));
        assertEquals(book1.getTitle(), response.getTitle());
        assertEquals(book1.getAuthor(), response.getAuthor());

        assertTrue(book1.isBorrowed());
    }

    @Test
    void testProcessBookTransaction_BorrowBookAlreadyBorrowed() {
        BookEntity book1 = new BookEntity("Book One", "Author A", "1234567890123",2022);
        UUID uuid = UUID.randomUUID();
        book1.setBookId(uuid);

        BookTransactionRequestDto requestDto = new BookTransactionRequestDto(uuid, 22L);        requestDto.setTransactionType(TransactionType.BORROW);
        book1.setBorrowed(true);
        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book1));

        BookResponseDto response = bookManagerService.processBookTransaction(requestDto);

        assertEquals("ERROR", response.getErrorCode());
        assertEquals("Book has been borrowed", response.getErrorMessage());

    }

    @Test
    void testProcessBookTransaction_BorrowUserExceededLimit() {
        BookEntity book1 = new BookEntity("Book One", "Author A", "1234567890123",2022);
        UUID uuid = UUID.randomUUID();
        book1.setBookId(uuid);

        BookTransactionRequestDto requestDto = new BookTransactionRequestDto(uuid, 22L);
        requestDto.setTransactionType(TransactionType.BORROW);
        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book1));

        when(userBookRepository.countByUserIdAndReturnedDateIsNull(22L)).thenReturn(3L);

        BookResponseDto response = bookManagerService.processBookTransaction(requestDto);

        assertEquals("ERROR", response.getErrorCode());
        assertTrue(response.getErrorMessage().contains("Already 3 books are borrowed"));
    }

    @Test
    void testProcessBookTransaction_ReturnSuccess() {
        BookEntity book1 = new BookEntity("Book One", "Author A", "1234567890123",2022);
        BookEntity book2 = new BookEntity("Book Two", "Author A", "1234567890123",2022);
        book2.setBookId(UUID.randomUUID());
        UUID uuid = UUID.randomUUID();
        book1.setBookId(uuid);
        Long userId = 1L;

        BookTransactionRequestDto requestDto = new BookTransactionRequestDto(uuid, userId);
        requestDto.setTransactionType(TransactionType.RETURN);
        book1.setBorrowed(true);

        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book1));

        when(userBookRepository.findByUserIdAndReturnedDateIsNullOrderByBorrowedDateDesc(userId))
            .thenReturn(List.of(new UserBookEntity(book1, userId, LocalDateTime.now(), null),
                new UserBookEntity(book2, userId, LocalDateTime.now(), null)));

        when(mapper.toDto(book1)).thenReturn(  new BookResponseDto(requestDto.getBookId(),book1.getIsbn(),
            book1.getTitle(), book1.getAuthor(), false, book1.getPublicationYear().toString(), LocalDateTime.now(), LocalDateTime.now()));

        BookResponseDto response = bookManagerService.processBookTransaction(requestDto);

        assertEquals(book1.getBookId(), response.getBookId());
        assertEquals(book1.getIsbn(), response.getISBN().replaceAll("-", ""));
        assertEquals(book1.getTitle(), response.getTitle());
        assertEquals(book1.getAuthor(), response.getAuthor());

        assertFalse(book1.isBorrowed());

        verify(bookTransactionRepository).save(any(BookTransactionEntity.class));
        assertFalse(book1.isBorrowed());
    }

    @Test
    void testProcessBookTransaction_ReturnBookNotBorrowedByUser() {
        BookEntity book1 = new BookEntity("Book One", "Author A", "1234567890123",2022);
        UUID uuid = UUID.randomUUID();
        book1.setBookId(uuid);
        BookEntity book2 = new BookEntity("Book Two", "Author A", "1234567890123",2022);
        book2.setBookId(UUID.randomUUID());
        BookTransactionRequestDto requestDto = new BookTransactionRequestDto(uuid, 22L);
        requestDto.setTransactionType(TransactionType.RETURN);

        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book1));
        when(userBookRepository.findByUserIdAndReturnedDateIsNullOrderByBorrowedDateDesc(22L))
            .thenReturn(List.of(new UserBookEntity(book2, 22L, LocalDateTime.now(), null)));

        BookResponseDto response = bookManagerService.processBookTransaction(requestDto);

        assertEquals("ERROR", response.getErrorCode());
        String message = String.format("User %d has not borrowed book %s.", 22L, book1.getBookId());

        assertEquals(message, response.getErrorMessage());
    }
}
