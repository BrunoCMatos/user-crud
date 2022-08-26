package br.com.carmonia.br.com.carmonia.br.com.carmonia.service;

import br.com.carmonia.br.com.carmonia.br.com.carmonia.dto.UserDTO;
import br.com.carmonia.br.com.carmonia.repository.UserRepository;
import br.com.carmonia.entity.User;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UserService {

    @Value("${users.photos.path}")
    private String filesPath;
    private UserRepository userRepository;

    public UserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(UserDTO userDTO) throws IllegalArgumentException {
        validate(userDTO);
        User savedUser = userRepository.save(fromDTO(userDTO));
        try {
            savePhotoOnFileSystem(userDTO.getPhoto(), savedUser.getPhotoIdentifier());
        } catch (IOException e) {
            throw new IllegalArgumentException("Photo is not on the correct format");
        }
    }

    public UserDTO findByName(String name) {
        List<User> users = userRepository.findAllByNameContaining(name);

    }

    private User fromDTO(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setBirthDate(userDTO.getBirthDate());
        return user;
    }

    private User toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.getBirthDate(user)
    }

    public Resource loadPhotoFromFileSystem(UUID fileIdentifier) throws IOException{
        return new UrlResource(getFilePath(fileIdentifier));
    }

    private void validate(UserDTO userDTO) throws IllegalArgumentException {
        if (userDTO == null) {
            throw new IllegalArgumentException("User Cannot be null");
        }

        if (StringHelper.isBlank(userDTO.getName())) {
            throw new IllegalArgumentException("User's name cannot be empty");
        }

    }

    private void savePhotoOnFileSystem(MultipartFile file, UUID fileIdentifier) throws IOException{
        if (file != null && file.getSize() > 0) {
            try(InputStream inputStream = file.getInputStream()) {
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                String path = getFilePath(fileIdentifier);
                ImageIO.write(bufferedImage, "jpg", new File(path));
            }
        }
    }

    private String getFilePath(UUID fileIdentifier) {
        return filesPath + fileIdentifier.toString();
    }
}
