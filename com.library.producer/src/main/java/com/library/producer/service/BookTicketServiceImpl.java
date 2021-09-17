package com.library.producer.service;

import com.library.producer.dto.BookMovieTicket;
import com.library.producer.dto.Location;
import com.library.producer.dto.MovieTheater;
import com.library.producer.repository.BookTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class BookTicketServiceImpl implements  BookTicketService{

    @Autowired
    private BookTicketRepository bookTicketRepository;

    @Override
    public Flux<Location> getMoviesByCity(Optional<String> city) {
        return bookTicketRepository.getMoviesByCity(city);
    }
    @Override
    public Mono<MovieTheater> bookTickets(BookMovieTicket bookmovieTicket) {
        return bookTicketRepository.bookTickets(bookmovieTicket);
    }
}
