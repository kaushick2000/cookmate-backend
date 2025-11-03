package com.cookmate.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instruction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;
    
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String instruction;
    
    @Column(name = "timer_minutes")
    private Integer timerMinutes;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
}