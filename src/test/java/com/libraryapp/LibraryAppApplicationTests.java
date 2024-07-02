package com.libraryapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class LibraryAppApplicationTests {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @BeforeAll
    static void setUp() {
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @Test
    void contextLoads() {
    }
}
