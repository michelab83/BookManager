package com.michelab.service;

import com.michelab.common.dto.enums.BorrowedStatus;
import com.michelab.common.dto.enums.TransactionType;
import com.michelab.common.dto.request.BookTransactionRequestDto;
import com.michelab.common.dto.request.CreateBookRequestDto;
import com.michelab.common.dto.response.BookResponseDto;
import com.michelab.common.dto.response.BookStatusResponseDto;
import com.michelab.common.dto.response.BookTransactionsResponseDto;
import com.michelab.common.dto.response.BooksResponseDto;
import com.michelab.common.mapper.Mapper;
import com.michelab.data.model.BookEntity;
import com.michelab.data.model.BookTransactionEntity;
import com.michelab.data.model.UserBookEntity;
import com.michelab.data.repository.BookRepository;
import com.michelab.data.repository.BookTransactionRepository;
import com.michelab.data.repository.UserBookRepository;
import com.michelab.service.exceptions.AllowedLoansExceededException;
import com.michelab.service.exceptions.BookBorrowedException;
import com.michelab.service.exceptions.NotBorrowedException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookManagerService {

    Logger logger = LoggerFactory.getLogger(BookManagerService.class);

    public static long ALLOWED_LOANS = 3;

    private final BookRepository bookRepository;

    private final UserBookRepository userBookRepository;

    private final BookTransactionRepository bookTransactionRepository;

    private final Mapper mapper;

    public BookManagerService(BookRepository bookRepository, BookTransactionRepository bookTransactionRepository, UserBookRepository userBookRepository, Mapper mapper) {
        this.bookRepository = bookRepository;
        this.bookTransactionRepository = bookTransactionRepository;
        this.userBookRepository = userBookRepository;
        this.mapper = mapper;
    }

    public BooksResponseDto createBooks(List<CreateBookRequestDto> request) {
        List<BookResponseDto> books = request.stream()
            .map(this::createBook).toList();
        return new BooksResponseDto(books);
    }

    private BookResponseDto createBook(CreateBookRequestDto createBookRequestDto) {
        int year;
        try {
            year = Integer.parseInt(createBookRequestDto.getPublicationYear());
            if (year < 1500 || year > LocalDateTime.now().getYear() + 1) {
                return new BookResponseDto("ERROR", "Year not valid");
            }
        } catch (NumberFormatException e) {
            return new BookResponseDto("ERROR", "Year not valid");
        }
        return mapper.toDto(
            bookRepository.save(new BookEntity(
                createBookRequestDto.getTitle(),
                createBookRequestDto.getAuthor(),
                createBookRequestDto.getIsbn(),
                year))
        );
    }

    public BookResponseDto getBookById(UUID id) {
        Optional<BookEntity> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return mapper.toDto(book.get());
        } else return new BookResponseDto("ERROR", "Book not found");
    }

    public BooksResponseDto getUserBooks(Long userId, BorrowedStatus borrowedStatus) {

        try {
            return switch (borrowedStatus) {
                case BORROWED -> new BooksResponseDto(userBookRepository.findByUserIdAndReturnedDateIsNullOrderByBorrowedDateDesc(userId)
                    .stream().map(be -> mapper.toDto(be.getBook()))
                    .collect(Collectors.toList()));
                case RETURNED -> new BooksResponseDto(userBookRepository.findByUserIdAndReturnedDateIsNotNullOrderByReturnedDateDesc(userId)
                    .stream().map(be -> mapper.toDto(be.getBook()))
                    .collect(Collectors.toList()));
                default -> new BooksResponseDto(userBookRepository.findByUserId(userId)
                    .stream().map(be -> mapper.toDto(be.getBook()))
                    .collect(Collectors.toList()));
            };
        } catch(Exception e) {
            logger.error(e.getMessage());
            return new BooksResponseDto("ERROR", "An error occurred while retrieving books");
        }

    }


    @Transactional
    public BookResponseDto processBookTransaction(BookTransactionRequestDto bookTransactionRequestDto) {

        Optional<BookEntity> bookEntityOptional = bookRepository.findById(bookTransactionRequestDto.getBookId());

        if (bookEntityOptional.isEmpty()) {
            return new BookResponseDto("ERROR", "Book not found");
        }
        BookEntity bookEntity = bookEntityOptional.get();
        if (bookTransactionRequestDto.getTransactionType().equals(TransactionType.BORROW)) {
            try {
                borrowBook(bookEntity, bookTransactionRequestDto.getUserId());
            } catch (BookBorrowedException | AllowedLoansExceededException e) {
                return new BookResponseDto("ERROR", e.getMessage());
            }
            bookEntity.setBorrowed(true);
        } else {
            try {
                returnBook(bookEntity, bookTransactionRequestDto.getUserId());
            } catch (NotBorrowedException e) {
                return new BookResponseDto("ERROR", e.getMessage());
            }
            bookEntity.setBorrowed(false);
        }

        bookTransactionRepository.save(new BookTransactionEntity(
            bookEntity,
            bookTransactionRequestDto.getUserId(),
            bookTransactionRequestDto.getTransactionType()
        ));

        return mapper.toDto(bookEntity);
    }

    private void borrowBook(BookEntity book, Long userId) {

        if (book.isBorrowed()) {
            String.format("User %d has not borrowed book %s.", userId, book.getBookId());
            logger.error("Book has been borrowed");
            throw new BookBorrowedException("Book has been borrowed");
        }
        if (userCurrentBooks(userId) >= ALLOWED_LOANS) {
            logger.error("Already 3 books are borrowed by user " + userId);
            throw new AllowedLoansExceededException("Already 3 books are borrowed by user " + userId);
        }

        userBookRepository.save(new UserBookEntity(
            book,
            userId,
            LocalDateTime.now(),
            null
        ));

    }

    private void returnBook(BookEntity book, Long userId) {
        List<UserBookEntity> userBooks = userBookRepository.findByUserIdAndReturnedDateIsNullOrderByBorrowedDateDesc(userId);
        if (userBooks.stream().anyMatch(ub -> ub.getBook().equals(book))) {
            Optional<UserBookEntity> optionalUserBook = userBooks.stream().filter(ub -> ub.getBook().equals(book)).findFirst();
            optionalUserBook.ifPresent(
                userBookEntity -> {
                    userBookEntity.setReturnedDate(LocalDateTime.now());

                }

            );

        } else {
            String message = String.format("User %d has not borrowed book %s.", userId, book.getBookId());
            logger.error(message);
            throw new NotBorrowedException(message);        }

    }


   private long userCurrentBooks(Long userId) {
        return userBookRepository.countByUserIdAndReturnedDateIsNull(userId);
    }

    public BooksResponseDto searchBooks(String title, String author, String isbn) {
        return new BooksResponseDto(
            bookRepository.searchBooks(title, author, isbn)
                .stream().map(mapper::toDto)
                .collect(Collectors.toList())
        );
    }

    public BookStatusResponseDto getBookStatus(UUID uuid) {

        Optional<UserBookEntity> optionalUserBook = userBookRepository.findByBookIdOrderByBorrowedDateDesc(uuid).stream().findFirst();
        if (optionalUserBook.isPresent()) {
            return mapper.toDto(optionalUserBook.get());
        } else {
            return new BookStatusResponseDto("ERROR", "Book not found");
        }

    }

    public BookTransactionsResponseDto getUserTransactions(Long userId) {
       return new BookTransactionsResponseDto(
           bookTransactionRepository.findByUserIdOrderByCreateAtDesc(userId)
            .stream().map(mapper::toDto)
            .toList()
       );
    }

    public BookTransactionsResponseDto getBookTransactions(UUID uuid) {

        Optional<BookEntity> bookEntityOptional = bookRepository.findById(uuid);

        return bookEntityOptional.map(bookEntity -> new BookTransactionsResponseDto(
            bookTransactionRepository.findByBookOrderByCreateAtDesc(bookEntity)
                .stream().map(mapper::toDto)
                .toList()
        )).orElseGet(() -> new BookTransactionsResponseDto("ERROR", "Book not found"));
    }
}
