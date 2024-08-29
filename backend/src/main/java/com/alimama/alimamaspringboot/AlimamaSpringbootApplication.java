package com.alimama.alimamaspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlimamaSpringbootApplication {

    public static void main(String[] args) {
        System.out.println(System.getenv("POSTGRES_PORT"));
        SpringApplication.run(AlimamaSpringbootApplication.class, args);
    }

}
