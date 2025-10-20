package com.michelab.controller;

import com.michelab.api.controller.UsersController;
import com.michelab.common.dto.enums.BorrowedStatus;
import com.michelab.common.dto.enums.TransactionType;
import com.michelab.common.dto.response.BookResponseDto;
import com.michelab.common.dto.response.BookTransactionResponseDto;
import com.michelab.common.dto.response.BookTransactionsResponseDto;
import com.michelab.common.dto.response.BooksResponseDto;
import com.michelab.service.BookManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsersController.class)
class UsersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookManagerService bookManagerService;


    @Test
    void shouldReturnUserBooksWithDefaultStatus() throws Exception {
        Long userId = 1L;

        BookResponseDto book = new BookResponseDto(
            UUID.randomUUID(),
            "9783161484100",
            "Effective Java",
            "Joshua Bloch",
            false,
            "2018",
            LocalDateTime.of(2021, 1, 1, 12, 0),
            LocalDateTime.of(2021, 6, 1, 12, 0)
        );

        BooksResponseDto responseDto = new BooksResponseDto(List.of(book));

        when(bookManagerService.getUserBooks(userId, BorrowedStatus.ALL))
            .thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{userId}/books", userId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books[0].isbn").value("9783161484100"))
            .andExpect(jsonPath("$.books[0].title").value("Effective Java"))
            .andExpect(jsonPath("$.books[0].author").value("Joshua Bloch"))
            .andExpect(jsonPath("$.books[0].isBorrowed").value(false))
            .andExpect(jsonPath("$.books[0].year").value("2018"));
    }

    @Test
    void shouldReturnBorrowedBooks() throws Exception {
        Long userId = 2L;

        BookResponseDto borrowedBook = new BookResponseDto(
            UUID.randomUUID(),
            "978-0-13-468599-1",
            "Clean Architecture",
            "Robert C. Martin",
            true,
            "2017",
            LocalDateTime.of(2022, 1, 1, 10, 0),
            LocalDateTime.of(2023, 1, 1, 10, 0)
        );

        BooksResponseDto responseDto = new BooksResponseDto(List.of(borrowedBook));

        when(bookManagerService.getUserBooks(userId, BorrowedStatus.BORROWED))
            .thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{userId}/books", userId)
                .param("status", "BORROWED")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books[0].title").value("Clean Architecture"))
            .andExpect(jsonPath("$.books[0].isBorrowed").value(true))
            .andExpect(jsonPath("$.books[0].year").value("2017"));
    }
}
