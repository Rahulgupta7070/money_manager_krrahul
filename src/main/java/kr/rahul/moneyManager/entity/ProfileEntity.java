package kr.rahul.moneyManager.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String email;

    private String password;

    private String profileImageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean isActive;
    private String activationToken;

    @PrePersist
    public void prePersist() {
       
        if (isActive == null) isActive = false;
    }

 
}
