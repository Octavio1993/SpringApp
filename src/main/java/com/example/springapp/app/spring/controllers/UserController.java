package com.example.springapp.app.spring.controllers;

import com.example.springapp.app.spring.dto.ChangePasswordForm;
import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.repository.RoleRepository;
import com.example.springapp.app.spring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("listTab", "active");
        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    public String createUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("formErrorMessage", null);
        } else {
            try {
                userService.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
                model.addAttribute("formErrorMessage", null);
            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab", "active");
                model.addAttribute("roles", roleRepository.findAll());
                model.addAttribute("userList", userService.getAllUsers());
            }
        }
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());

        return "user-form/user-view";
    }

    @GetMapping("/editUser/{id}")
    public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception {
        User userToEdit = userService.getUserById(id);

        model.addAttribute("userForm", userToEdit);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("formTab", "active");
        model.addAttribute("editMode", "true");
        model.addAttribute("passwordForm", new ChangePasswordForm(id));

        return "user-form/user-view";
    }

    @PostMapping("/editUser")
    public String postEditUserForm(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {

        System.out.println("ID recibido: " + user.getId()); // DEBUG

        if (result.hasErrors()) {

            System.out.println("Errores de validación:");
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));

            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", "true");
            model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
        } else {
            try {
                userService.updateUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab", "active");
                model.addAttribute("roles", roleRepository.findAll());
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("editMode", "true");
                model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
            }
        }
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());

        return "user-form/user-view";
    }

    @GetMapping("/userForm/cancel")
    public String cancelEditUser(ModelMap model) {
        return "redirect:/userForm";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable(name = "id") Long id) {
        try {
            userService.deleteUser(id);
            model.addAttribute("deleteSuccess", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            model.addAttribute("deleteError", e.getMessage());
        }

        return userForm(model);
    }

    @PostMapping("/editUser/changePassword")
    public ResponseEntity<?> postEditUseChangePassword(@Valid @RequestBody ChangePasswordForm form, Errors errors) {
        try {
            //Si hay error, retorna un 400 bad request, junto con el mensaje de error
            if (errors.hasErrors()) {
                String result = errors.getAllErrors()
                        .stream().map(x -> x.getDefaultMessage())
                        .collect(Collectors.joining("<br/>"));

                return ResponseEntity.badRequest().body(result);
            }
            userService.changePassword(form);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("success");
    }
}
