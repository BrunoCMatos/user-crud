package br.com.carmonia.service;

import br.com.carmonia.dto.UserDTO;
import br.com.carmonia.entity.User;
import br.com.carmonia.repository.UserRepository;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.carmonia.controller.UserController.PATH;
import static br.com.carmonia.controller.UserController.PHOTO_DOWNLOAD_PATH;

@Service
public class UserService {

    @Value("${users.photos.path}")
    private String filesPath;
    @Value("${host.uri}")
    private String hostUri;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(UserDTO userDTO) throws IllegalArgumentException {
        validate(userDTO);
        userRepository.save(fromDTO(userDTO));
    }

    public void saveUserPhoto(MultipartFile photo, User user) {
        try {
            if (user.getPhotoIdentifier() != null) {
                deletePhotoFromFileSystem(user.getPhotoIdentifier());
            }
            user.setPhotoIdentifier(UUID.randomUUID());
            savePhotoOnFileSystem(photo, user.getPhotoIdentifier());
            userRepository.save(user);
        } catch (IOException e) {
            throw new IllegalArgumentException("A error ocurred during this process " + e.getMessage());
        }
    }

    public List<UserDTO> findByName(String name) {
        List<User> users = userRepository.findAllByNameContaining(name);
        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        Optional<User> users = userRepository.findById(id);
        if (users.isEmpty()) {
            return null;
        }
        return toDTO(users.get());
    }

    private User fromDTO(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setBirthDate(userDTO.getBirthDate());
        return user;
    }

    private UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setBirthDate(user.getBirthDate());
        if (user.getPhotoIdentifier() != null) {
            String path = hostUri + contextPath + PATH + PHOTO_DOWNLOAD_PATH + "/" + user.getPhotoIdentifier().toString();
            userDTO.setPhotoURI(path);
        }
        return userDTO;
    }

    public byte[] getPhoto(UUID photoIdentifier) throws IOException {
        return loadPhotoFromFileSystem(photoIdentifier);
    }

    public byte[] loadPhotoFromFileSystem(UUID fileIdentifier) throws IOException {
        Path path = Path.of(getFilePath(fileIdentifier));
        if (!Files.exists(path)) {
            return null;
        }
        return Files.readAllBytes(path);
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

    public void deletePhotoFromFileSystem(UUID fileIdentifier) throws IOException {
        Files.deleteIfExists(Path.of(getFilePath(fileIdentifier)));
    }

    private String getFilePath(UUID fileIdentifier) {
        return filesPath + "/" + fileIdentifier.toString();
    }

    public void deleteById(Long id) throws IOException {
        Optional<User> user = userRepository.findById(id);
        deletePhotoFromFileSystem(user.get().getPhotoIdentifier());
        userRepository.deleteById(id);
    }
}
