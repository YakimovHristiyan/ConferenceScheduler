package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    @Column(name = "role_type", nullable = false)
    private String roleType;

    @OneToMany(mappedBy = "userRole")
    private List<User> users;

}
