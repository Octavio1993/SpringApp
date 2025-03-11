package com.example.springapp.app.spring.controllers;

import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.repository.RoleRepository;
import com.example.springapp.app.spring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/userForm")
    public String createUser (@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model){
        if (result.hasErrors()){
            model.addAttribute("userForm", user);
            model.addAttribute("formTab","active");
            model.addAttribute("formErrorMessage", null);
        } else {
            try {
                userService.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab","active");
                model.addAttribute("formErrorMessage", null);
            } catch (Exception e){
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab","active");
                model.addAttribute("roles", roleRepository.findAll());
                model.addAttribute("userList",userService.getAllUsers());
            }
        }
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList",userService.getAllUsers());

        return "user-form/user-view";
    }
}
