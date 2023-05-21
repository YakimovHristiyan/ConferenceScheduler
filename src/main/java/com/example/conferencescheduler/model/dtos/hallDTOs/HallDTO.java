package com.example.conferencescheduler.model.dtos.hallDTOs;

import com.example.conferencescheduler.model.entities.Conference;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class HallDTO {

    private int hallId;
    private String hallName;
    private Integer capacity;
    private Integer conferenceId;
    private List<LocalTime> times = getAllTimeSlots();

    private List<LocalTime> getAllTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9,0);
        LocalTime endTime = LocalTime.of(18,0);
        int slotDuration = 60;
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            timeSlots.add(currentTime);
            currentTime = currentTime.plusMinutes(slotDuration);
        }
        return timeSlots;
    }
}
