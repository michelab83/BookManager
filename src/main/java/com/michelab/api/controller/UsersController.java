package com.michelab.api.controller;

import com.michelab.common.dto.response.BookTransactionsResponseDto;
import com.michelab.common.dto.response.BooksResponseDto;
import com.michelab.common.dto.enums.BorrowedStatus;
import com.michelab.service.BookManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final BookManagerService bookManagerService;

    public UsersController(BookManagerService bookManagerService) {
        this.bookManagerService = bookManagerService;
    }

    @GetMapping("/{userId}/books")
    public ResponseEntity<BooksResponseDto> getUserBooks(
        @PathVariable Long userId,
        @RequestParam(defaultValue ="ALL") BorrowedStatus status
    ) {
        return new ResponseEntity<>(bookManagerService.getUserBooks(userId, status), HttpStatus.OK);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<BookTransactionsResponseDto> getUserTransactions(@PathVariable Long userId) {
        return new ResponseEntity<>(bookManagerService.getUserTransactions(userId), HttpStatus.OK);
    }
}
