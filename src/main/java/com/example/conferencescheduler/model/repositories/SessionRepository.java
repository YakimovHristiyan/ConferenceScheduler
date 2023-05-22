package com.example.conferencescheduler.model.repositories;
import com.example.conferencescheduler.model.entities.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Native;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session>findBySessionId(int id);

    @Query(value = "SELECT * FROM session WHERE DATE(start_date) = :date", nativeQuery = true)
    List<Session> getSessionByDate(LocalDate date);

    @Query(value = "SELECT * FROM session WHERE DATE(start_date) = :date ORDER BY (start_date) ASC", nativeQuery = true)
    List<Session> getSessionByDateOrderByStartDate(LocalDate date);

}
