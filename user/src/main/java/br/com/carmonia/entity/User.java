package br.com.carmonia.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "photo_uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID photoIdentifier;
}
