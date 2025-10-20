package com.michelab.common.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Valid
public class CreateBookRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 13, message = "ISBN must be max 13 characters long")
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must contain either 10 or 13 digits")
    private String isbn;

    @NotBlank(message = "Publication year is required")
    private String publicationYear;


    public CreateBookRequestDto(String title, String author, String isbn, String publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    @Override
    public String toString() {
        return "CreateBookRequest{" +
            "title='" + title + '\'' +
            ", author='" + author + '\'' +
            ", ISBN='" + isbn + '\'' +
            ", publicationYear='" + publicationYear + '\'' +
            '}';
    }
}
