package com.library.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.producer.LibraryEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
       ListenableFuture<SendResult<Integer, String>> future =  kafkaTemplate.sendDefault(libraryEvent.getLibraryEventId(), objectMapper.writeValueAsString(libraryEvent));
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                try{
                    log.info("Message sent unsuccessfully for the key {}, and the value is {}, partition is {}", libraryEvent.getLibraryEventId(),  objectMapper.writeValueAsString(libraryEvent),
                            ex.getMessage() );
                } catch (Exception e) {
                        throw new RuntimeException("addagagg");
                }

            }


            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                try{
                    log.info("Message sent successfully for the key {}, and the value is {}, partition is {}", libraryEvent.getLibraryEventId(),  objectMapper.writeValueAsString(libraryEvent),
                            result.getRecordMetadata().partition() );
                } catch (Exception e) {

                }

            }
        });
    }

    public void sendLibraryEvent_approach2(LibraryEvent libraryEvent) throws JsonProcessingException {
        ListenableFuture<SendResult<Integer, String>> future =  kafkaTemplate.send("library-events",libraryEvent.getLibraryEventId(), objectMapper.writeValueAsString(libraryEvent));
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                try{
                    log.info("Message sent unsuccessfully for the key {}, and the value is {}, partition is {}", libraryEvent.getLibraryEventId(),  objectMapper.writeValueAsString(libraryEvent),
                            ex.getMessage() );
                } catch (Exception e) {
                    throw new RuntimeException("addagagg");
                }

            }


            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                try{
                    log.info("Message sent successfully for the key {}, and the value is {}, partition is {}", libraryEvent.getLibraryEventId(),  objectMapper.writeValueAsString(libraryEvent),
                            result.getRecordMetadata().partition() );
                } catch (Exception e) {

                }

            }
        });
    }

    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent event) throws JsonProcessingException {
       int key =  event.getLibraryEventId();
       String value = objectMapper.writeValueAsString(event);
        try {
           return kafkaTemplate.sendDefault(key, value)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SendResult<Integer, String> sendLibraryEventSynchronous_approach_using_producer_record(LibraryEvent event) throws JsonProcessingException {
        int key =  event.getLibraryEventId();
        String value = objectMapper.writeValueAsString(event);
        String topic_name = "library-events";
        ProducerRecord<Integer, String> topic = new ProducerRecord(topic_name, key, value);
        try {
            return kafkaTemplate.send(topic)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Mono<LibraryEvent> sendLibraryEventFunctional(LibraryEvent libraryEvent) {
        return Mono.create(sink -> {
            String value = "";
            try {
                value = objectMapper.writeValueAsString(libraryEvent);
            } catch (Exception e) {}

            kafkaTemplate.sendDefault(libraryEvent.getLibraryEventId(), value)
                    .addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                        @Override
                        public void onFailure(Throwable ex) {
                            try{
                                log.info("Message sent unsuccessfully for the key {}, and the value is {}, partition is {}", libraryEvent.getLibraryEventId(),  objectMapper.writeValueAsString(libraryEvent),
                                        ex.getMessage() );
                                sink.error(ex);
                            } catch (Exception e) {
                                throw new RuntimeException("addagagg");
                            }

                        }


                        @Override
                        public void onSuccess(SendResult<Integer, String> result) {
                            try{
                                log.info("Message sent successfully for the key {}, and the value is {}, partition is {}", libraryEvent.getLibraryEventId(),  objectMapper.writeValueAsString(libraryEvent),
                                        result.getRecordMetadata().partition() );
                                sink.success(libraryEvent);
                            } catch (Exception e) {

                            }

                        }
                    });
        })
                .map(obj -> {
                    System.out.println("Hey man, converted this shit to async-await");
                    return (LibraryEvent)obj;
                });
    }

    public Mono<LibraryEvent> sendLibraryEventFunctional_using_get(LibraryEvent libraryEvent) {
        return Mono.create(sink -> {
            String value = "";
            try {
                value = objectMapper.writeValueAsString(libraryEvent);
                kafkaTemplate.sendDefault(libraryEvent.getLibraryEventId(), value)
                        .get(3, TimeUnit.SECONDS);
                log.info("Might be success");
                sink.success(libraryEvent);
            } catch (Exception e) {
                sink.error(e);
            }
        }).map(obj -> {
                    log.info("Made the synchronous to asynchronous");
                    return (LibraryEvent) obj;
        });

    }

    public Mono<LibraryEvent> sendLibraryEventFunctional_using_get_and_topic(LibraryEvent libraryEvent) {
        return Mono.create(sink -> {
            String value = "";
            List<Header> kafkaHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
            try {
                value = objectMapper.writeValueAsString(libraryEvent);
                ProducerRecord<Integer, String> topic = new ProducerRecord("library-events",null, libraryEvent.getLibraryEventId(), value, kafkaHeaders );
                kafkaTemplate.send(topic)
                        .get();
                log.info("Might be success");
                sink.success(libraryEvent);
            } catch (Exception e) {
                sink.error(e);
            }
        }).map(obj -> {
            log.info("Made the synchronous to asynchronous");
            return (LibraryEvent) obj;
        });

    }
}
