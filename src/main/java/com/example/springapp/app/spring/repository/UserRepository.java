package com.example.springapp.app.spring.repository;

import com.example.springapp.app.spring.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    public Optional<User> findByUserName(String userName);

    public Optional<User> findByIdAndPassword(Long id, String password);
}
