package com.library.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.library.producer.producer.EventType;
import com.library.producer.producer.LibraryEventProducer;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LibraryEventController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;



    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> saveLibraryebent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        libraryEvent.setEventType(EventType.NEW);
        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        //invoke the kafka producer
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    @PostMapping(value = "/v1/libraryevent/functional", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<LibraryEvent> saveLibraryebentFunc(@RequestBody LibraryEvent libraryEvent) {
        libraryEvent.setEventType(EventType.NEW);
        return libraryEventProducer.sendLibraryEventFunctional_using_get_and_topic(libraryEvent);
        //return libraryEventProducer.sendLibraryEventFunctional(libraryEvent);
    }


    @PutMapping(value = "/v1/libraryevent/functional", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<LibraryEvent> updateLibraryebentFunc(@RequestBody LibraryEvent libraryEvent) {
        if(StringUtil.isNullOrEmpty(String.valueOf(libraryEvent.getLibraryEventId()))) {
            libraryEvent.setEventType(EventType.NEW);
        } else {
            libraryEvent.setEventType(EventType.UPDATE);
        }
        return libraryEventProducer.sendLibraryEventFunctional_using_get_and_topic(libraryEvent);
        //return libraryEventProducer.sendLibraryEventFunctional(libraryEvent);
    }
}
