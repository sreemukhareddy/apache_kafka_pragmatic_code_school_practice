package com.library.producer.repository;

import com.library.producer.dto.BookMovieTicket;
import com.library.producer.dto.Location;
import com.library.producer.dto.MovieTheater;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class BookTicketRepositoryImpl implements BookTicketRepository{

    private List<Location> locations = new ArrayList<>();

    public BookTicketRepositoryImpl(){
        initData();
    }

    private void initData() {
        MovieTheater ishqHyd = new MovieTheater();
        ishqHyd.setMovieTheaterLocation("HYD");
        ishqHyd.setTotalMovieSeats(500);
        ishqHyd.setMovieTheaterName("theater-1");
        ishqHyd.setMovieSeatsAvailable(250);
        MovieTheater ishqHyd2 = new MovieTheater();
        ishqHyd2.setMovieTheaterLocation("HYD");
        ishqHyd2.setTotalMovieSeats(300);
        ishqHyd2.setMovieTheaterName("theater-2");
        ishqHyd2.setMovieSeatsAvailable(150);
        MovieTheater ishqHyd3 = new MovieTheater();
        ishqHyd3.setMovieTheaterLocation("HYD");
        ishqHyd3.setTotalMovieSeats(200);
        ishqHyd3.setMovieTheaterName("theater-3");
        ishqHyd3.setMovieSeatsAvailable(150);
        Map<String, List<MovieTheater>> hyderabadMovies = new HashMap<>();
        hyderabadMovies.put("ishq", List.of(ishqHyd, ishqHyd2, ishqHyd3));
        hyderabadMovies.put("krrish", List.of(ishqHyd, ishqHyd2, ishqHyd3));
        hyderabadMovies.put("omg", List.of(ishqHyd, ishqHyd2, ishqHyd3));

        MovieTheater delhi = new MovieTheater();
        delhi.setMovieTheaterLocation("DEL");
        delhi.setTotalMovieSeats(500);
        delhi.setMovieTheaterName("theater-1");
        delhi.setMovieSeatsAvailable(250);
        MovieTheater delhi1 = new MovieTheater();
        delhi1.setMovieTheaterLocation("DEL");
        delhi1.setTotalMovieSeats(300);
        delhi1.setMovieTheaterName("theater-2");
        delhi1.setMovieSeatsAvailable(150);
        MovieTheater delhi2 = new MovieTheater();
        delhi2.setMovieTheaterLocation("DEL");
        delhi2.setTotalMovieSeats(200);
        delhi2.setMovieTheaterName("theater-3");
        delhi2.setMovieSeatsAvailable(150);
        Map<String, List<MovieTheater>> delhiMovies = new HashMap<>();
        delhiMovies.put("ishq", List.of(delhi, delhi1, delhi2));
        delhiMovies.put("krrish", List.of(delhi, delhi1, delhi2));
        delhiMovies.put("omg", List.of(delhi, delhi1, delhi2));

        MovieTheater mumbai = new MovieTheater();
        mumbai.setMovieTheaterLocation("MUM");
        mumbai.setTotalMovieSeats(500);
        mumbai.setMovieTheaterName("theater-1");
        mumbai.setMovieSeatsAvailable(250);
        MovieTheater mumbai1 = new MovieTheater();
        mumbai1.setMovieTheaterLocation("MUM");
        mumbai1.setTotalMovieSeats(300);
        mumbai1.setMovieTheaterName("theater-2");
        mumbai1.setMovieSeatsAvailable(150);
        MovieTheater mumbai2 = new MovieTheater();
        mumbai2.setMovieTheaterLocation("MUM");
        mumbai2.setTotalMovieSeats(200);
        mumbai2.setMovieTheaterName("theater-3");
        mumbai2.setMovieSeatsAvailable(150);
        Map<String, List<MovieTheater>> mumbaiMovies = new HashMap<>();
        mumbaiMovies.put("ishq", List.of(mumbai, mumbai1, mumbai2));
        mumbaiMovies.put("krrish", List.of(mumbai, mumbai1, mumbai2));
        mumbaiMovies.put("omg", List.of(mumbai, mumbai1, mumbai2));

        locations.add(Location.builder().city("hyderabad").movies(hyderabadMovies).build());
        locations.add(Location.builder().city("delhi").movies(delhiMovies).build());
        locations.add(Location.builder().city("mumbai").movies(mumbaiMovies).build());
    }

    @Override
    public Flux<Location> getMoviesByCity(Optional<String> city) {
        if(!city.isPresent() || city.isEmpty()){
            return Flux.fromIterable(locations);
        }
        return Flux.fromIterable(locations)
                .filter(location -> {
                    System.out.println("The given location is " + city+ " and the location from the list is " + location.getCity());
                    return location.getCity().equalsIgnoreCase(city.get());
                });
    }

    @Override
    public Mono<MovieTheater> bookTickets(BookMovieTicket bookmovieTicket) {
    MovieTheater movieTheater =    locations.stream().filter(location -> location.getCity().equalsIgnoreCase(bookmovieTicket.getCity()))
                .map(Location::getMovies)
                .filter(movieAndSeats -> movieAndSeats.containsKey(bookmovieTicket.getMovie()))
                .findAny().get().get(bookmovieTicket.getMovie())
                .stream()
                .filter(mt -> mt.getMovieTheaterName().equalsIgnoreCase(bookmovieTicket.getMovieTheaterName()))
                .findAny()
                .get();
    movieTheater.setMovieSeatsAvailable(movieTheater.getMovieSeatsAvailable()-Integer.valueOf(bookmovieTicket.getTickets()));
    return Mono.just(movieTheater);
    }
}
