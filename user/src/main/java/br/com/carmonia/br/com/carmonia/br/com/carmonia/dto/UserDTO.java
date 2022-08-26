package br.com.carmonia.br.com.carmonia.br.com.carmonia.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private @NonNull String name;
    private LocalDate birthDate;
    private MultipartFile photo;
}
