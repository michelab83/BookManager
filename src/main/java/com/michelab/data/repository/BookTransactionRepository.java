package com.michelab.data.repository;

import com.michelab.data.model.BookEntity;
import com.michelab.data.model.BookTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookTransactionRepository extends JpaRepository<BookTransactionEntity, UUID> {

    List<BookTransactionEntity> findByUserIdOrderByCreateAtDesc(Long userId);

    List<BookTransactionEntity> findByBookOrderByCreateAtDesc(BookEntity book);

}
