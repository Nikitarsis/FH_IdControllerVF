package com.filecontr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/say-hello/{name}")
    String hello(@PathVariable String name) {
        return String.format("Hello, %s!", name);
    }
}
