package com.example.springapp.app.spring.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String userName;

    @Column
    private String password;

    @Transient //necesaria cuando queremos tener campos en nuestra entidad pero no queremos que tenga ninguna relación
    // con la tabla de la base de datos.

    private String confirmarPassword;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles"
            ,joinColumns = @JoinColumn(name = "user_id")
            ,inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles; //me deja ingresar valores que no se repitan

    public User() {
    }

    public User(Long id, String nombre, String apellido, String email, String userName, String password, String confirmarPassword, Set<Role> roles) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.confirmarPassword = confirmarPassword;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", confirmarPassword='" + confirmarPassword + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(nombre, user.nombre) && Objects.equals(apellido, user.apellido) && Objects.equals(email, user.email) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && Objects.equals(confirmarPassword, user.confirmarPassword) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellido, email, userName, password, confirmarPassword, roles);
    }
}
