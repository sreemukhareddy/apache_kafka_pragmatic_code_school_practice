package com.library.producer;

import com.library.producer.producer.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {
    private Integer libraryEventId;
    private Book book;
    private EventType eventType;
}
