package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conference")
@Getter
@Setter
@RequiredArgsConstructor
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

    @OneToMany(mappedBy = "session")
    private List<Session> sessions;

    @ManyToMany(mappedBy = "conferences")
    private List<Hall> halls;
}
