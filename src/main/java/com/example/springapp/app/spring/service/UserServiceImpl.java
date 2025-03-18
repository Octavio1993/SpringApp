package com.example.springapp.app.spring.service;

import com.example.springapp.app.spring.dto.ChangePasswordForm;
import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    private boolean checkUsernameAvailable(User user) throws Exception {
        Optional<User> userFound = userRepository.findByUserName(user.getUserName());

        if (userFound.isPresent()) {
            throw new Exception("Nombre de usuario no disponible");
        }
        return true;
    }

    private boolean checkPasswordValid(User user) throws Exception {
        if (user.getConfirmarPassword() == null || user.getConfirmarPassword().isEmpty()) {
            throw new Exception("Confirm Password es obligatorio");
        }

        if (!user.getPassword().equals(user.getConfirmarPassword())) {
            throw new Exception("Las contraseñas deben ser iguales");
        }
        return true;
    }

    @Override
    public User createUser(User user) throws Exception {
        if (checkUsernameAvailable(user) && checkPasswordValid(user)) {
            String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);

            user = userRepository.save(user);
        }

        return user;
    }

    @Override
    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("El ID del usuario no existe"));
    }

    @Override
    public User updateUser(User fromUser) throws Exception {
        User toUser = getUserById(fromUser.getId());
        mapUser(fromUser, toUser);

        System.out.println("Guardando usuario: " + toUser); // DEBUG

        return userRepository.save(toUser);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("Usuario con ID " + id + " no encontrado."); // Verifica en consola
                    return new Exception("Usuario no encontrado - " + this.getClass().getName());
                });
        /*User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado -"+this.getClass().getName()));
        */
        userRepository.delete(user);
    }

    @Override
    public User changePassword(ChangePasswordForm form) throws Exception {
        User storedUser = userRepository
                .findById( form.getId() )
                .orElseThrow(() -> new Exception("UsernotFound in ChangePassword -"+this.getClass().getName()));

        //verifico que la contraseña actual sea igual a la de la base de datos
        if ( !isLoggedUserADMIN() && !storedUser.getPassword().equals(form.getCurrentPassword())) {
            throw new IllegalArgumentException("Current Password Incorrect.");
        }

        //verifico que la contraseña nueva sea distinta a la actual
        if ( form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new IllegalArgumentException("New Password must be different than Current Password!");
        }

        //verifico que la contraseña nueva sea igual a la contraseña que quiero confirmar
        if( !form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("New Password and Confirm Password does not match!");
        }

        String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
        storedUser.setPassword(encodePassword);
        return userRepository.save(storedUser);
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new Exception("El usuario no existe"));
    }

    private boolean isLoggedUserADMIN() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggedUser = null;
        if (principal instanceof UserDetails) {
            loggedUser = (UserDetails) principal;

            loggedUser.getAuthorities().stream()
                    .filter(x -> "ADMIN".equals(x.getAuthority() ))
                    .findFirst().orElse(null); //loggedUser = null;
        }
        return loggedUser != null ?true :false;
    }

    protected void mapUser(User from, User to) {
        to.setNombre(from.getNombre());
        to.setApellido(from.getApellido());
        to.setUserName(from.getUserName());
        to.setEmail(from.getEmail());
        to.setRoles(from.getRoles());
    }
}
