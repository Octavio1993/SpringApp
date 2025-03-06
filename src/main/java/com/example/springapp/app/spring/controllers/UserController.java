package com.example.springapp.app.spring.controllers;

import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.repository.RoleRepository;
import com.example.springapp.app.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @GetMapping("/home")
    public String index() {
        System.out.println("HOLA OCTAVIO DESDE EL HOME");
        return "index";
    }

    @GetMapping("/userForm")
    public String userForm(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList",userService.getAllUsers());
        model.addAttribute("listTab","active");
        return "user-form/user-view";
    }
}
