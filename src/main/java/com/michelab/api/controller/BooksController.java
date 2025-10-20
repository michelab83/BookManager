package com.michelab.api.controller;

import com.michelab.common.dto.enums.TransactionType;
import com.michelab.common.dto.request.BookTransactionRequestDto;
import com.michelab.common.dto.request.CreateBookRequestDto;
import com.michelab.common.dto.response.BookResponseDto;
import com.michelab.common.dto.response.BookStatusResponseDto;
import com.michelab.common.dto.response.BookTransactionsResponseDto;
import com.michelab.common.dto.response.BooksResponseDto;
import com.michelab.service.BookManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    private final BookManagerService bookManagerService;

    public BooksController(BookManagerService bookManagerService) {
        this.bookManagerService = bookManagerService;
    }


    @GetMapping("/{uuid}")
    public ResponseEntity<BookResponseDto> getBook(@PathVariable UUID uuid) {
        return new ResponseEntity<>(bookManagerService.getBookById(uuid), HttpStatus.OK);

    }

    @GetMapping("/search")
    public ResponseEntity<BooksResponseDto> searchBooks(@RequestParam(required = false) String title,
                                                        @RequestParam(required = false) String author,
                                                        @RequestParam(required = false) String isbn){
        return new ResponseEntity<>(bookManagerService.searchBooks(title, author, isbn), HttpStatus.OK);
    }

    @GetMapping("/{uuid}/status")
    public ResponseEntity<BookStatusResponseDto> getBookStatus(@PathVariable UUID uuid) {
        return new ResponseEntity<>(bookManagerService.getBookStatus(uuid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BooksResponseDto> addBooks(@RequestBody @Valid List<@Valid CreateBookRequestDto> request) {
        return new ResponseEntity<>(bookManagerService.createBooks(request), HttpStatus.OK);
    }



    @PostMapping("/borrow")
    public ResponseEntity<BookResponseDto> borrowBook(@RequestBody BookTransactionRequestDto request) {
        request.setTransactionType(TransactionType.BORROW);
        return  new ResponseEntity<>(bookManagerService.processBookTransaction(request), HttpStatus.OK);
    }

    @PostMapping("/return")
    public ResponseEntity<BookResponseDto> returnBook(@RequestBody BookTransactionRequestDto request) {
        request.setTransactionType(TransactionType.RETURN);
        return  new ResponseEntity<>(bookManagerService.processBookTransaction(request), HttpStatus.OK);
    }


    @GetMapping("/{uuid}/transactions")
    public ResponseEntity<BookTransactionsResponseDto> getUserTransactions(@PathVariable UUID uuid) {
        return new ResponseEntity<>(bookManagerService.getBookTransactions(uuid), HttpStatus.OK);
    }

}
