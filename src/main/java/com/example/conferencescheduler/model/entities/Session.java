package com.example.conferencescheduler.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "session")
@Getter
@Setter
@RequiredArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @ManyToMany
    @JoinTable(
            name = "guest_session",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    @JsonIgnore
    private List<User> guests;
}
