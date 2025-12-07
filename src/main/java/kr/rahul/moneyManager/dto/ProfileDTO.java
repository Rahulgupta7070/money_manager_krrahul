package kr.rahul.moneyManager.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class ProfileDTO {

    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String imageUrl;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

}