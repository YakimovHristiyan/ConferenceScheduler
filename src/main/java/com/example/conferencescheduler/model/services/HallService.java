package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallWithSessionsDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

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

    public List<HallDTO> viewAllHalls() {
        List<Hall> halls = hallRepository.findAll();

        return halls.stream().map(e -> modelMapper.map(e, HallDTO.class)).collect(Collectors.toList());
    }


}
