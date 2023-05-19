package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Column(name = "hallName", nullable = false)
    private String hallName;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @OneToMany(mappedBy = "hall")
    private List<Session> sessions;
}
