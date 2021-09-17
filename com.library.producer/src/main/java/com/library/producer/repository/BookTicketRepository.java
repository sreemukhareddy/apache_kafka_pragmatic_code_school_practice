package com.library.producer.repository;

import com.library.producer.dto.BookMovieTicket;
import com.library.producer.dto.Location;
import com.library.producer.dto.MovieTheater;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface BookTicketRepository {
    public Flux<Location> getMoviesByCity(Optional<String> city);
    public Mono<MovieTheater> bookTickets(BookMovieTicket bookmovieTicket);
}
