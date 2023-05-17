package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conference")
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conferenceId;

    @Column(name = "conferenceName", nullable = false)
    private String conferenceName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "Conference_owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "guest_conference",
            joinColumns = @JoinColumn(name = "conference_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private List<User> guests;

    @OneToMany(mappedBy = "conference")
    private List<Hall> halls;
}
