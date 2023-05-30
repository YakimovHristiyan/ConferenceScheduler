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
@Table(name = "conference")
@Getter
@Setter
@RequiredArgsConstructor
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conferenceId;

    @Column
    private String conferenceName;

    @Column
    private String description;

    @Column
    private String address;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "Conference_owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "conference", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Session> sessions;

    @ManyToMany(mappedBy = "conferences")
    private List<Hall> halls;

    @OneToOne
    @JoinColumn(name = "conference_status_id")
    private Status status;


}
