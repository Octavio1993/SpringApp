package com.example.springapp.app.spring.service;

import com.example.springapp.app.spring.entity.User;
import jakarta.validation.Valid;

public interface UserService {
    public Iterable<User> getAllUsers();

    public User createUser(User user) throws Exception;

    public User getUserById(Long id) throws Exception;

    public User updateUser(User user) throws Exception;

    public void deleteUser(Long id) throws Exception;
}
