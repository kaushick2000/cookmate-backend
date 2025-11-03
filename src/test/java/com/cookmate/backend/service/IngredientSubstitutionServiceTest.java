package com.cookmate.backend.service;

import com.cookmate.backend.dto.IngredientSubstitutionDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientSubstitutionServiceTest {

    @Test
    void suggestKnownIngredient_returnsSubstitutions() {
        IngredientSubstitutionService svc = new IngredientSubstitutionService();
        IngredientSubstitutionDto dto = svc.suggest("butter", false);

        assertNotNull(dto);
        assertNotNull(dto.getSubstitutions());
        assertFalse(dto.getSubstitutions().isEmpty(), "Expected at least one substitution for butter");
        assertEquals("rule-based", dto.getSource());
        assertNotNull(dto.getSubstitutions().get(0).getIngredient());
    }

    @Test
    void suggestUnknown_returnsEmptyList() {
        IngredientSubstitutionService svc = new IngredientSubstitutionService();
        IngredientSubstitutionDto dto = svc.suggest("unicorn-hair", false);

        assertNotNull(dto);
        assertNotNull(dto.getSubstitutions());
        assertTrue(dto.getSubstitutions().isEmpty() || "none".equals(dto.getSource()), 
                "Unknown ingredient should yield empty substitutions or none source");
    }

    @Test
    void suggestWithAI_flag() {
        IngredientSubstitutionService svc = new IngredientSubstitutionService();
        IngredientSubstitutionDto dto = svc.suggest("butter", true);

        assertNotNull(dto);
        assertNotNull(dto.getSubstitutions());
        // AI might enhance or return empty, both are valid
        assertTrue("ai".equals(dto.getSource()) || "rule-based".equals(dto.getSource()) || "none".equals(dto.getSource()));
    }
}
