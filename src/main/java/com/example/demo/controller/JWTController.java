package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWTController {

    @PostMapping("/jwt/token")
    public String filter(){
        return "ok";
    }

    @GetMapping("/api/v1/user")
    @ResponseBody
    public String user(){
        return "user";
    }

    @GetMapping("/api/v1/manager")
    @ResponseBody
    public String manager(){
        return "manager";
    }

    @GetMapping("/api/v1/admin")
    @ResponseBody
    public String admin(){
        return "admin";
    }
}
