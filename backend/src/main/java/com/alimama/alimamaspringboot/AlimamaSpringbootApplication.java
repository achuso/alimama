package com.alimama.alimamaspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlimamaSpringbootApplication {
    public static void main(String[] args) {
        DatabaseConnection abc = new DatabaseConnection();

        System.out.println(abc.connectMongodb());

        SpringApplication.run(AlimamaSpringbootApplication.class, args);
    }
}
