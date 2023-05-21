package com.example.conferencescheduler.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "speaker")
@Getter
@Setter
@NoArgsConstructor
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int speakerId;

    @Column
    private
    String description;

    @Column
    private String profilePhoto;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
