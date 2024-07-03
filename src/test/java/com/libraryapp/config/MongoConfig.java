package com.libraryapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public String mongodbUri() {
        return "mongodb://localhost:27017";
    }

    @Bean
    public MongoClient mongoClient(@Qualifier("mongodbUri") String uri) {
        return MongoClients.create(uri);
    }
}
