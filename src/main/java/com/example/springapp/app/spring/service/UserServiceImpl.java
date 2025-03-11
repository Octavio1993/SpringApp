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
}
