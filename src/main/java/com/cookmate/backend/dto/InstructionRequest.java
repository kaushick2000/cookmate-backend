package com.cookmate.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructionRequest {
    
    @NotNull(message = "Step number is required")
    private Integer stepNumber;
    
    @NotBlank(message = "Instruction is required")
    private String instruction;
    
    private Integer timerMinutes;
    private String imageUrl;
}