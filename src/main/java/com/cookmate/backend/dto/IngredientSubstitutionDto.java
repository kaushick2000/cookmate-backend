package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class IngredientSubstitutionDto {
    private List<Substitution> substitutions;
    private String source; // e.g. "rule-based" or "ai"

    public IngredientSubstitutionDto() {}

    public IngredientSubstitutionDto(List<Substitution> substitutions, String source) {
        this.substitutions = substitutions;
        this.source = source;
    }

    public List<Substitution> getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(List<Substitution> substitutions) {
        this.substitutions = substitutions;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Substitution {
        private String ingredient;
        private String ratio;
        private String note;
    }
}
