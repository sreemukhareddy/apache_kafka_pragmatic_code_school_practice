package com.library.consumer.libraryeventsconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.library.consumer.service.LibraryConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsConsumer {

    @Autowired
    private LibraryConsumerService libraryConsumerService;

    @KafkaListener(topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("ComsumerRecord : {}", consumerRecord);
        //throw new RuntimeException("HEHEHEZZZZZZZZ");
        //throw new RecoverableDataAccessException("");
        libraryConsumerService.processLibraryEvent(consumerRecord);
    }
}
