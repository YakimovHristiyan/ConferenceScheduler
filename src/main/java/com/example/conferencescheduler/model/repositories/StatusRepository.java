package com.example.conferencescheduler.model.repositories;

import com.example.conferencescheduler.model.entities.Status;
import com.example.conferencescheduler.model.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Optional<Status> findByConferenceStatusId(int integer);
}
