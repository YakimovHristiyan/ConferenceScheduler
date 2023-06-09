package com.example.conferencescheduler.model.repositories;

import com.example.conferencescheduler.model.entities.Speaker;
import com.example.conferencescheduler.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, Integer> {

    Speaker findByUser(User user);
    Optional<Speaker> findSpeakerBySpeakerId(int id);
}
