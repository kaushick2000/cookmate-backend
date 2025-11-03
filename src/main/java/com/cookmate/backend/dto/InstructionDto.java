package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructionDto {
    
    private Long id;
    private Integer stepNumber;
    private String instruction;
    private Integer timerMinutes;
    private String imageUrl;
}