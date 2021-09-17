package com.library.producer.controller;

import com.library.producer.dto.BookMovieTicket;
import com.library.producer.dto.Location;
import com.library.producer.dto.MovieTheater;
import com.library.producer.service.BookTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/v1/booking")
public class BookTicketController {

    @Autowired
    private BookTicketService bookTicketService;

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Location> getMoviesByCity(@RequestParam Optional<String> city){
        return bookTicketService.getMoviesByCity(city);
    }

    @PostMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<MovieTheater> bookMovieTicket(@RequestBody BookMovieTicket movieTicket){
        return bookTicketService.bookTickets(movieTicket);
        //return bookTicketService.getMoviesByCity(city);
    }
}
