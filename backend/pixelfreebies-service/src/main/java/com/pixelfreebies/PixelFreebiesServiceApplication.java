package com.pixelfreebies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class PixelFreebiesServiceApplication {

    // todo: different types of exceptions for authentication and authorization needs to be populated.
    public static void main(String[] args) {
        SpringApplication.run(PixelFreebiesServiceApplication.class, args);
    }

    // default webpage
    @GetMapping
    public String index() {
        return "index";
    }

}
