package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "speaker")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int speakerId;

    @Column(name = "description")
    private
    String description;

    @Column(name = "profilePhoto", nullable = false)
    private String profilePhoto;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
