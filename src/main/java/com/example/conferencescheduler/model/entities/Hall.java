package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hall")
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hallId;

    @Column(name = "hallName", nullable = false)
    private String hallName;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "hall")
    private List<Session> sessions;

    @ManyToMany
    @JoinTable(
            name = "conference_hall",
            joinColumns = @JoinColumn(name = "hall_id"),
            inverseJoinColumns = @JoinColumn(name = "conference_id")
    )
    private List<Conference> conferences;
}
