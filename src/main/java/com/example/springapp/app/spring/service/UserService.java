package com.example.springapp.app.spring.service;

import com.example.springapp.app.spring.entity.User;
import jakarta.validation.Valid;

public interface UserService {
    public Iterable<User> getAllUsers();

    public User createUser(User user) throws Exception;
}
