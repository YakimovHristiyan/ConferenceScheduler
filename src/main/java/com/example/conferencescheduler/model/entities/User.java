package com.example.conferencescheduler.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private LocalDateTime registerAt;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private boolean isVerified;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(mappedBy = "guests")
    private List<Session> sessions;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole userRole;
}
