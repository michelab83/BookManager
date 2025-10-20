package com.michelab.data.repository;

import com.michelab.data.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository  extends JpaRepository<BookEntity, UUID> {

    @Query("""
        SELECT b FROM BookEntity b
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
          AND (:isbn IS NULL OR b.isbn = :isbn)
        """)
    List<BookEntity> searchBooks(
        @Param("title") String title,
        @Param("author") String author,
        @Param("isbn") String isbn
    );

}
