package com.example.conferencescheduler.model.services;

import com.example.conferencescheduler.model.dtos.speakerDTOs.SpeakerRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserLoginDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserRegisterDTO;
import com.example.conferencescheduler.model.dtos.userDTOs.UserWithoutPassDTO;
import com.example.conferencescheduler.model.entities.Speaker;
import com.example.conferencescheduler.model.entities.User;
import com.example.conferencescheduler.model.exceptions.BadRequestException;
import com.example.conferencescheduler.model.exceptions.NotFoundException;
import com.example.conferencescheduler.model.exceptions.UnauthorizedException;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class SpeakerService extends MasterService {

    @Transactional
    public UserWithoutPassDTO register(SpeakerRegisterDTO dto) {
//        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        UserRegisterDTO userRegisterDTO = modelMapper.map(dto, UserRegisterDTO.class);
        validateUserInformation(userRegisterDTO);
        User user = modelMapper.map(dto, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRegisterAt(LocalDateTime.now());
        user.setVerified(false);
        userRepository.save(user);
        sendVerificationEmail(user.getEmail());

        Speaker speaker = new Speaker();
        speaker.setProfilePhoto(DEF_PROFILE_IMAGE_URI);
        speaker.setDescription(dto.getDescription());
        speakerRepository.save(speaker);

        return modelMapper.map(user, UserWithoutPassDTO.class);
    }

    public void changeProfileImage(int uid, MultipartFile image) {
        Speaker speaker = validateSpeaker(uid);
        String oldImageURI = speaker.getProfilePhoto();
        if(oldImageURI != null && !oldImageURI.equals(DEF_PROFILE_IMAGE_URI)) {
            deleteOldFile(speaker.getProfilePhoto());
        }
        validateImage(image);
        String uri = saveFile(image);
        speaker.setProfilePhoto(uri);
        speakerRepository.save(speaker);
    }

    private void validateImage(MultipartFile image) {
        if (image == null) {
            throw new BadRequestException("Image not uploaded.");
        }
        if (image.getContentType().equals("image/jpeg") || image.getContentType().equals("image/jpg")
                || image.getContentType().equals("image/png")) {
            return;
        }
        throw new BadRequestException("Image type needs to be jpg,jpeg or png.");
    }

    private Speaker validateSpeaker(int uid) {
        User user = getUserById(uid);
        Speaker speaker = speakerRepository.findByUser(user);
        if (speaker != null) {
            return speaker;
        }
        throw new NotFoundException("Speaker not found.");
    }

    @SneakyThrows
    private void deleteOldFile(String uri) {
        Files.delete(Path.of(uri));
    }

    @SneakyThrows
    private String saveFile(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uri = "uploads" + File.separator + System.nanoTime() + "." + extension;
        File f = new File(uri);
        Files.copy(file.getInputStream(), f.toPath());
        return uri;
    }
}
