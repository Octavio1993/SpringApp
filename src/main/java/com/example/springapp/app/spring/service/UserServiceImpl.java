package com.example.springapp.app.spring.service;

import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Override
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    private boolean checkUsernameAvailable(User user) throws Exception{
        Optional<User> userFound = userRepository.findByUserName(user.getUserName());

        if (userFound.isPresent()){
            throw new Exception("Nombre de usuario no disponible");
        }
        return true;
    }

    private boolean checkPasswordValid(User user) throws Exception{
        if (user.getConfirmarPassword() == null || user.getConfirmarPassword().isEmpty()) {
            throw new Exception("Confirm Password es obligatorio");
        }

        if (!user.getPassword().equals(user.getConfirmarPassword())){
            throw new Exception("Las contraseñas deben ser iguales");
        }
        return true;
    }

    @Override
    public User createUser(User user) throws Exception{
        if (checkUsernameAvailable(user) && checkPasswordValid(user)){
            user = userRepository.save(user);
        }

        return user;
    }

    @Override
    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("El usuario no existe"));
    }

    @Override
    public User updateUser(User fromUser) throws Exception {
        User toUser = getUserById(fromUser.getId());
        mapUser(fromUser, toUser);

        System.out.println("Guardando usuario: " + toUser); // DEBUG

        return userRepository.save(toUser);
    }

    protected void mapUser(User from, User to){
        to.setNombre(from.getNombre());
        to.setApellido(from.getApellido());
        to.setUserName(from.getUserName());
        to.setEmail(from.getEmail());
        to.setRoles(from.getRoles());
    }
}
