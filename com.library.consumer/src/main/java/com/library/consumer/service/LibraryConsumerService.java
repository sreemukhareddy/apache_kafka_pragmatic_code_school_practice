package com.library.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.consumer.entity.LibraryEvent;
import com.library.consumer.repository.BookRepository;
import com.library.consumer.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class LibraryConsumerService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EventRepository libraryEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
      LibraryEvent libraryEvent =   objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
      log.info("libraryEvent : {}", libraryEvent);
      switch (libraryEvent.getEventType()) {
          case NEW:
              libraryEvent.getBook().setLibraryEvent(libraryEvent);
              libraryEventRepository.save(libraryEvent);
              break;
          case UPDATE:
              libraryEvent.getBook().setLibraryEvent(libraryEvent);
              libraryEventRepository.save(libraryEvent);
              break;
          default:
              log.info("Invalid library event type");
      }
    }

    public void handleRecoveryConsumerExceptions(ConsumerRecord<Integer, String> consumerRecord) {
        kafkaTemplate.send("library-events", consumerRecord.key(), consumerRecord.value())
                .addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        System.out.println("Exception while pushing the error onto kafka topic");
                    }

                    @Override
                    public void onSuccess(SendResult<Integer, String> result) {
                        System.out.println("Success");
                    }
                });
    }
}
