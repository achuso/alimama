package com.alimama.alimamaspringboot;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private String abc;

    Controller() {
        abc = "abc";
    }
}
