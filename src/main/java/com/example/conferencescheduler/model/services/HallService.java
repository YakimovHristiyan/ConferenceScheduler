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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HallService extends MasterService {

    public static final int SESSION_TIME_DURATION = 60;

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

    private void validateConferenceOwner(Conference conference, User user) {
        if (conference.getOwner().getUserId() != user.getUserId()) {
            throw new UnauthorizedException("You are not owner of this conference!");
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
        List<HallDTO> dtos = new ArrayList<>();
        HallDTO hallDTO;
        List<Hall> allHalls = hallRepository.findAll();
        for (Hall hall : allHalls) {
            if (hall.getSessions().isEmpty()) {
                hallDTO = modelMapper.map(hall, HallDTO.class);
                dtos.add(hallDTO);
            } else {
                List<Session> sessions = hall.getSessions();
                for (Session s : sessions) {
                    List<LocalTime> timesToRemove = timesToRemove(s.getStartDate().toLocalTime(), s.getEndDate().toLocalTime());
                    hallDTO = modelMapper.map(s.getHall(), HallDTO.class);
                    if (dtos.contains(hallDTO)) {
                        int index = dtos.indexOf(hallDTO);
                        HallDTO hallDto = dtos.get(index);
                        removeTimeSlots(timesToRemove, hallDto);
                        continue;
                    }
                    hallDTO = modelMapper.map(s.getHall(), HallDTO.class);
                    removeTimeSlots(timesToRemove, hallDTO);
                    dtos.add(hallDTO);
                }
            }
        }
        return dtos;
    }

    private void removeTimeSlots(List<LocalTime> timesToRemove, HallDTO hallDTO) {
        for (LocalTime time : timesToRemove) {
            hallDTO.getTimes().remove(time);
        }
    }

    private List<LocalTime> timesToRemove(LocalTime start, LocalTime end) {
        List<LocalTime> timesToRemove = new ArrayList<>();
        LocalTime curTime = start;
        while (curTime.isBefore(end)) {
            timesToRemove.add(curTime);
            curTime = curTime.plusMinutes(SESSION_TIME_DURATION);
        }
        timesToRemove.add(curTime);
        return timesToRemove;
    }

    public List<CreateHallDTO> getAllConferenceHalls(int hid) {
        Conference conference = conferenceRepository.findByConferenceId(hid)
                .orElseThrow(() -> new NotFoundException("Conference not found."));
        return conference.getHalls()
                .stream()
                .map(hall -> modelMapper.map(hall, CreateHallDTO.class))
                .toList();
    }
}
