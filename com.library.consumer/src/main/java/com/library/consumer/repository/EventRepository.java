package com.library.consumer.repository;

import com.library.consumer.entity.Book;
import com.library.consumer.entity.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<LibraryEvent, Integer> {
}
