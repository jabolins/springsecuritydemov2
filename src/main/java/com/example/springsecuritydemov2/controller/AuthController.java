package com.example.springsecuritydemov2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/index")
    public String getLevel2(){
        return "index";
    }
}
