package com.example.jwtlogin.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@ToString(exclude = "resume")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Job job;

    @ManyToOne
    private UserEntity user;

    @Lob
    @Column(name = "resume", columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] resume;

    private String status;
    private LocalDate appliedDate;
}
