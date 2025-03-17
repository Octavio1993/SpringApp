package com.example.springapp.app.spring.service;

import com.example.springapp.app.spring.entity.Role;
import com.example.springapp.app.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Buscar nombre de usuario en nuestra base de datos
        com.example.springapp.app.spring.entity.User appUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario invalido"));

        Set<GrantedAuthority> grantList = new HashSet<GrantedAuthority>();

        //Crear la lista de los roles/accesos que tienen el usuarios
        for (Role role: appUser.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getDescripcion());
            grantList.add(grantedAuthority);
        }

        //Crear y retornar Objeto de usuario soportado por Spring Security
        UserDetails user = (UserDetails) new User(appUser.getUserName(), appUser.getPassword(), grantList);

        return user;
    }
}
