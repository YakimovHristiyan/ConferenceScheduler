package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToMany
    @JoinTable(
            name = "guest_session",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private List<User> guests;
}
