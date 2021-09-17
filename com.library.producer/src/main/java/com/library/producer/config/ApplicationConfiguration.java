package com.library.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class ApplicationConfiguration {

    // the below configuration is not suitable // is not recommended
    @Bean
    public NewTopic libraryEvents(){
        return TopicBuilder.name("library-events")
                .replicas(3)
                .partitions(3)
                .build();
    }
}
