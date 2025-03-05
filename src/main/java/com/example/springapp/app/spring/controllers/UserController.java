package com.example.springapp.app.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/home")
    public String index() {
        System.out.println("HOLA OCTAVIO DESDE EL HOME");
        return "index";
    }

    @GetMapping("/userForm")
    public String userForm() {
        System.out.println("Entrando a /userForm...");
        return "user-form/user-view";
    }
}
