package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
    
    List<Instruction> findByRecipe_IdOrderByStepNumberAsc(Long recipeId);
    
    void deleteByRecipe_Id(Long recipeId);
}