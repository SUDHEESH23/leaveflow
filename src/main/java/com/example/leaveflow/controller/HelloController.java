package com.example.leaveflow.controller;

import com.example.leaveflow.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final HelloService helloservice;

    public HelloController(HelloService helloservice) {
        this.helloservice = helloservice;
    }

    @GetMapping("/api/v1/hello")
    public String hello(){
        return helloservice.getMessage();
    }
}
