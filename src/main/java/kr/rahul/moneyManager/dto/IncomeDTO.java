package kr.rahul.moneyManager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeDTO {

    private Long id;
    private String name;
    private String icon;
    private LocalDate date;
    private BigDecimal amount;
    private Long category_id;
    private String category_name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}