package com.example.springapp.app.spring.repository;

import com.example.springapp.app.spring.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role,Long> {
}
