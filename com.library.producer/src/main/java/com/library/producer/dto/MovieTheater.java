package com.library.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieTheater {
    private String movieTheaterName;
    private String movieTheaterLocation;
    private Integer movieSeatsAvailable;
    private Integer totalMovieSeats;
}
