package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "registerAt", nullable = false)
    private LocalDateTime registerAt;

    @Column(name = "lastLoginAt", nullable = false)
    private LocalDateTime lastLoginAt;

    @Column(name = "isVerified", nullable = false)
    private boolean isVerified;

    @ManyToMany(mappedBy = "guests")
    private List<Conference> conferences;
}
