package com.example.springapp.app.spring.controllers;

import com.example.springapp.app.spring.dto.ChangePasswordForm;
import com.example.springapp.app.spring.entity.Role;
import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.exception.CustomeFieldValidationException;
import com.example.springapp.app.spring.repository.RoleRepository;
import com.example.springapp.app.spring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String index() {
        System.out.println("HOLA OCTAVIO DESDE EL HOME");
        return "index";
    }

    @GetMapping("/userForm")
    public String userForm(Model model) {
//        model.addAttribute("userForm", new User());
//        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("listTab", "active");

        if (isAdmin()) {
            model.addAttribute("userForm", new User());
            model.addAttribute("roles", roleRepository.findAll());
        }

        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @GetMapping("/myProfile")
    public String getMyProfile(Model model) throws Exception {
        // Obtener el nombre de usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Buscar el usuario en la base de datos por nombre de usuario
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new Exception("Usuario no encontrado");
        }

        // Configurar el modelo para la vista de edición
        model.addAttribute("userForm", currentUser);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("formTab", "active");
        model.addAttribute("editMode", "true");
        model.addAttribute("passwordForm", new ChangePasswordForm(currentUser.getId()));

        // Si es un usuario regular, desactivar los campos que no debería editar
        if (!isAdmin()) {
            model.addAttribute("disableFields", "true");
        }

        return "user-form/user-view";
    }

    @GetMapping("/editUser/{id}")
    public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception {
        User userToEdit = userService.getUserById(id);

        // Verificar si el usuario actual tiene permiso para editar este usuario
        if (!isAdminOrSameUser(userToEdit)) {
            return "redirect:/userForm?accessDenied";
        }

        model.addAttribute("userForm", userToEdit);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("formTab", "active");
        model.addAttribute("editMode", "true");
        model.addAttribute("passwordForm", new ChangePasswordForm(id));

        // Si es un usuario regular editando su propio perfil, deshabilitar campos específicos
        if (!isAdmin() && isSameUser(userToEdit)) {
            model.addAttribute("disableFields", "true");
        }

        return "user-form/user-view";
    }

    @PostMapping("/editUser")
    public String postEditUserForm(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        try {
            // Verificar si el usuario actual tiene permiso para editar este usuario
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            User existingUser = userService.getUserById(user.getId());
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isSameUser = existingUser.getUserName().equals(currentUsername);

            // Solo permitir si es admin o el mismo usuario
            if (!isAdmin && !isSameUser) {
                return "redirect:/userForm?accessDenied";
            }

            // Si hay errores de validación
            if (result.hasErrors()) {
                model.addAttribute("userForm", user);
                model.addAttribute("formTab", "active");
                model.addAttribute("editMode", "true");
                model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
                model.addAttribute("roles", roleRepository.findAll());
                model.addAttribute("userList", userService.getAllUsers());
                return "user-form/user-view";
            }

            // Si es un usuario normal (no admin), mantener los roles existentes
            if (!isAdmin && isSameUser) {
                user.setRoles(existingUser.getRoles());
            }

            // Si estamos en modo edición, la contraseña viene como "xxxx" desde el form
            // por lo que debemos mantener la contraseña existente
            if (user.getPassword().equals("xxxx")) {
                user.setPassword(existingUser.getPassword());
            }

            userService.updateUser(user);

            // Después de guardar, redirigir según el tipo de usuario
            if (isAdmin) {
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
            } else {
                // Si es usuario normal, redirigir a su perfil
                return "redirect:/myProfile?success";
            }

        } catch (Exception e) {
            model.addAttribute("formErrorMessage", e.getMessage());
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", "true");
            model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
        }

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userList", userService.getAllUsers());
        return "user-form/user-view";
    }

    // Método para verificar si es administrador o el mismo usuario
    private boolean isAdminOrSameUser(User user) {
        return isAdmin() || isSameUser(user);
    }

    // Método para verificar si es el mismo usuario
    private boolean isSameUser(User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName().equals(user.getUserName());
    }

    // Método para verificar si es administrador
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
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

    @GetMapping("/signup")
    public String signUp (Model model) {
        Optional<Role> userRole = roleRepository.findByNombre("USER");
        List<Optional<Role>> roles = Arrays.asList(userRole);

        model.addAttribute("signup",true);
        model.addAttribute("userForm", new User());
        model.addAttribute("roles",roles);

        return "user-form/user-signup";
    }

    @PostMapping("/signup")
    public String signupAction(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model) {
        Optional<Role> userRole = roleRepository.findByNombre("USER");
        List<Optional<Role>> roles = Arrays.asList(userRole);
        model.addAttribute("userForm", user);
        model.addAttribute("roles",roles);
        model.addAttribute("signup",true);

        if(result.hasErrors()) {
            return "user-form/user-signup";
        }else {
            try {
                userService.createUser(user);
            } catch (CustomeFieldValidationException cfve) {
                result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
            }catch (Exception e) {
                model.addAttribute("formErrorMessage",e.getMessage());
            }
        }
        return index();
    }
}
