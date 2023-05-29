package com.example.conferencescheduler.model.repositories;
import com.example.conferencescheduler.model.entities.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {

}
