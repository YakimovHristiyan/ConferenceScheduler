package com.example.conferencescheduler.model.repositories;

import com.example.conferencescheduler.model.entities.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Integer> {
}
