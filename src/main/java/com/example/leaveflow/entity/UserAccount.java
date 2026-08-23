package com.example.leaveflow.entity;

import jakarta.persistence.*;
import org.apache.catalina.User;

@Entity
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    protected UserAccount() {

    }

    public UserAccount(String email, String passwordHash, UserRole role){
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }
}
