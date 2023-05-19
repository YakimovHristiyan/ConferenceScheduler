package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.hallDTOs.CreateHallDTO;
import com.example.conferencescheduler.model.dtos.hallDTOs.HallDTO;
import com.example.conferencescheduler.model.entities.Conference;
import com.example.conferencescheduler.model.entities.Hall;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class HallService extends MasterService {

    public HallDTO addHallToConference(int userId, int hallId, int conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(()->new NotFoundException("Conference not found!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        Hall hall = hallRepository.findById(hallId).orElseThrow(() -> new NotFoundException("Hall does not exist!"));
        validateConferenceOwner(conference, user);
        //Todo Check if the hall have free hours for this hours
        checkHallIsFree(hall, conference);
        conference.getHalls().add(hall);
        conferenceRepository.save(conference);
        return modelMapper.map(hall, HallDTO.class);
    }

    private void checkHallIsFree(Hall hall, Conference conference) {
    }

    public void validateConferenceOwner(Conference conference, User user){
        if(conference.getOwner().getUserId() != user.getUserId()){
            throw new UnauthorizedException("You can not add halls to conferences you are not owner!");
        }
    }

    public HallDTO createHall(CreateHallDTO hall, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        //Todo Check if is admin
        Hall hallToSave = modelMapper.map(hall, Hall.class);
        hallRepository.save(hallToSave);
        return modelMapper.map(hallToSave, HallDTO.class);
    }
}
