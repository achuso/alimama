package com.alimama.alimamaspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlimamaSpringbootApplication {
    public static void main(String[] args) {
        MongoDBConnection abc = new MongoDBConnection();

        System.out.println(abc.connectMongoDB());

        SpringApplication.run(AlimamaSpringbootApplication.class, args);
    }
}
