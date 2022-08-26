package br.com.carmonia.br.com.carmonia.controller;

import br.com.carmonia.br.com.carmonia.br.com.carmonia.dto.UserDTO;
import br.com.carmonia.br.com.carmonia.br.com.carmonia.service.UserService;
import br.com.carmonia.br.com.carmonia.repository.UserRepository;
import org.apache.coyote.Response;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/save")
    public ResponseEntity<String> save(UserDTO userToSave) {
        try {
            userService.save(userToSave);
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully built");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
