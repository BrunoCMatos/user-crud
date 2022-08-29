package br.com.carmonia.controller;

import br.com.carmonia.dto.UserDTO;
import br.com.carmonia.entity.User;
import br.com.carmonia.repository.UserRepository;
import br.com.carmonia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.carmonia.controller.UserController.PATH;
import static org.springframework.util.CollectionUtils.isEmpty;

@RestController
@RequestMapping(PATH)
public class UserController {

    public static final String PATH = "/user";

    public static final String PHOTO_DOWNLOAD_PATH = "/photo";

    private  UserRepository userRepository;
    private UserService userService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PutMapping(value = "/save")
    public ResponseEntity<String> save(@RequestBody UserDTO userToSave) {
        try {
            userService.save(userToSave);
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully saved");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/find")
    public ResponseEntity<List<UserDTO>> findByName(String name) {
        List<UserDTO> users = userService.findByName(name);
        if (isEmpty(users)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            userService.deleteById(id);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Deleted");
    }

    @PostMapping(value = "/photo", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadPhoto(Long id, @RequestPart MultipartFile photo) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }
        userService.saveUserPhoto(photo, user.get());
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully saved");
    }

    @GetMapping(value = PHOTO_DOWNLOAD_PATH + "/{photoIdentification}", produces = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable("photoIdentification") String photoUUID) {
        try {
            byte[] photo = userService.getPhoto(UUID.fromString(photoUUID));
            if (photo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(photo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
