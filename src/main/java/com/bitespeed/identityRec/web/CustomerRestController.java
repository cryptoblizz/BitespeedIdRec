package com.bitespeed.identityRec.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/identity")
@RestController
public class CustomerRestController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }


}
