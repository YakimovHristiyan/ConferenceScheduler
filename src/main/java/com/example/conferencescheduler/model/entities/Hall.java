package com.example.conferencescheduler.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "hall")
@Getter
@Setter
@RequiredArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hallId;

    @Column
    private String hallName;

    @Column
    private Integer capacity;

    @OneToMany(mappedBy = "hall")
    @JsonIgnore
    private List<Session> sessions;

    @ManyToMany
    @JoinTable(
            name = "conference_hall",
            joinColumns = @JoinColumn(name = "hall_id"),
            inverseJoinColumns = @JoinColumn(name = "conference_id")
    )
    @JsonIgnore
    private List<Conference> conferences;

}
