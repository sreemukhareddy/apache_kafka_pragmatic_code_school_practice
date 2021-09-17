package com.library.consumer.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LibraryEvent {

    @Id
    @GeneratedValue
    private Integer libraryEventId;
    @OneToOne(mappedBy = "libraryEvent")
    @ToString.Exclude
    private Book book;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}
