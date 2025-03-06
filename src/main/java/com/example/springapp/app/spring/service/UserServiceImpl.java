package com.example.springapp.app.spring.service;

import com.example.springapp.app.spring.entity.User;
import com.example.springapp.app.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Override
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }
}
