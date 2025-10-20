package com.michelab.data.repository;

import com.michelab.data.model.UserBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBookRepository  extends JpaRepository<UserBookEntity, UUID> {

    List<UserBookEntity> findByUserIdAndReturnedDateIsNullOrderByBorrowedDateDesc(Long userId);
    List<UserBookEntity> findByUserIdAndReturnedDateIsNotNullOrderByReturnedDateDesc(Long userId);
    List<UserBookEntity> findByUserId(Long userId);

    @Query("""
        SELECT ub FROM UserBookEntity ub
        WHERE ub.book.bookId = :bookId
        ORDER BY ub.borrowedDate DESC
        """)
    List<UserBookEntity> findByBookIdOrderByBorrowedDateDesc(@Param("bookId") UUID bookId);

}
