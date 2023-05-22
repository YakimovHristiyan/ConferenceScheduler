package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.DateDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallWithSessionsDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.Session;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HallService extends MasterService {

    public HallDTO addHallToConference(int userId, int hallId, int conferenceId) {
        Conference conference = getConferenceById(conferenceId);
        User user = getUserById(userId);
        Hall hall = getHallById(hallId);

        validateConferenceOwner(conference, user);
        conference.getHalls().add(hall);
        conferenceRepository.save(conference);
        return modelMapper.map(hall, HallDTO.class);
    }

    public HallDTO removeHallFromConference(int userId, int hallId, int conferenceId) {
        Conference conference = getConferenceById(conferenceId);
        User user = getUserById(userId);
        Hall hall = getHallById(hallId);

        validateConferenceOwner(conference, user);
        conference.getHalls().remove(hall);
        conferenceRepository.save(conference);
        return modelMapper.map(hall, HallDTO.class);
    }

    public void validateConferenceOwner(Conference conference, User user) {
        if (conference.getOwner().getUserId() != user.getUserId()) {
            throw new UnauthorizedException("You can not add halls to conferences you are not owner!");
        }
    }

    public HallDTO createHall(CreateHallDTO hall, int userId) {
        getUserById(userId);
        Hall hallToSave = modelMapper.map(hall, Hall.class);
        hallRepository.save(hallToSave);
        return modelMapper.map(hallToSave, HallDTO.class);
    }

    public HallWithSessionsDTO viewHall(int hid) {
        Hall hall = getHallById(hid);
        return modelMapper.map(hall, HallWithSessionsDTO.class);
    }

    public List<HallDTO> viewAllFreeHallsAndSlots() {//TODO Are we need this???
        List<Hall> halls = hallRepository.findAll();
        return halls.stream().map(e -> modelMapper.map(e, HallDTO.class)).collect(Collectors.toList());
    }

    public List<HallDTO> getAvailableTimeSlots(DateDTO dto) {
        List<Session> sessions = sessionRepository.getSessionByDate(dto.getDate());
        List<HallDTO> dtos = new ArrayList<>();
        HallDTO hallDTO = new HallDTO();
        for (Session s : sessions) {
            LocalTime time = s.getStartDate().toLocalTime();
            if (dtos.contains(hallDTO)) {
                int index = dtos.indexOf(hallDTO);
                HallDTO hall = dtos.get(index);
                hall.getTimes().remove(time);
                hallDTO = modelMapper.map(s.getHall(), HallDTO.class);
                continue;
            }
            hallDTO = modelMapper.map(s.getHall(), HallDTO.class);
            hallDTO.getTimes().remove(time);
            dtos.add(hallDTO);
        }
        ;
        if (dtos.isEmpty()) {
            return hallRepository.findAll()
                    .stream()
                    .map(hall -> modelMapper.map(hall, HallDTO.class))
                    .toList();
        }
        return dtos;
    }
}
