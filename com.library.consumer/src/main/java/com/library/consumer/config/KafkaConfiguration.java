package com.library.consumer.config;

import com.library.consumer.service.LibraryConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    /*
    The below configuration is not required if we dont want customized kafkalistener. just use with the topic name.
     */

    @Autowired
    private LibraryConsumerService libraryConsumerService;

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
       /* configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));*/
        ConsumerFactory<Object, Object> consumerFactory =  kafkaConsumerFactory.getIfAvailable();
        configurer.configure(factory, consumerFactory);
       // factory.setConcurrency(3); // this thing is not necessary if we are running the applications in cloud like environment
       // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // this is for manual acknowledgement
        factory.setErrorHandler((exception, data) -> {    // this is for error handling
            System.out.println("Hey man, we got an error...");
            //throw new RuntimeException("HEHEHEH");
        });
        factory.setRetryTemplate(getRetryTemplate());
        factory.setRecoveryCallback(context -> {
            System.out.println("Fuck you");
            if(context.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
                System.out.println("Recoverable data");
              ConsumerRecord<Integer, String> consumerRecord = (ConsumerRecord<Integer, String>) context.getAttribute("record");
             // libraryConsumerService.handleRecoveryConsumerExceptions(consumerRecord);
            } else {
                System.out.println("Unrecoverable exception");
                throw new RuntimeException("From unrecoverable");
            }
            return null;
        });
        return factory;
    }

    private RetryTemplate getRetryTemplate() {

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1500); // wait for 1.5 seconds before retrying


        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(getRetryPolicy());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    private RetryPolicy getRetryPolicy() {
        // the below will retry for all the exceptions that are produced in the onConsume method of LibraryEventsConsumer
        /*
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);
        */

        // the below code will retry for the exceptions which are mentioned as true in the constructor

        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(RuntimeException.class, true);
        //exceptions.put(RecoverableDataAccessException.class, false);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, exceptions);


        return simpleRetryPolicy;
    }
}
