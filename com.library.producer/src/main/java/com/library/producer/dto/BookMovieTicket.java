package com.library.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookMovieTicket {
    private String city;
    private String movieTheaterName;
    private String movie;
    private String tickets;
}
