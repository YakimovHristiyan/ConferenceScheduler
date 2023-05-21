package com.example.conferencescheduler.model.repositories;
import com.example.conferencescheduler.model.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session>findBySessionId(int id);

}
