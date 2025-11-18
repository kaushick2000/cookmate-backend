package com.cookmate.backend.service;

import com.cookmate.backend.dto.IngredientSubstitutionDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IngredientSubstitutionService {

    private final Map<String, List<IngredientSubstitutionDto.Substitution>> rules = new HashMap<>();
    private GoogleGeminiService geminiService;

    public IngredientSubstitutionService() {
        initializeSubstitutionRules();
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setGeminiService(GoogleGeminiService geminiService) {
        this.geminiService = geminiService;
    }

    private void initializeSubstitutionRules() {
        // Dairy substitutions
        rules.put("butter", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Olive Oil", "3/4", "For baking, reduce liquid by 3 tbsp per cup"),
            new IngredientSubstitutionDto.Substitution("Coconut Oil", "1:1", "Best for baking and sautéing"),
            new IngredientSubstitutionDto.Substitution("Applesauce", "1/2", "For baking, reduces fat content")
        ));

        rules.put("milk", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Almond Milk", "1:1", "Unsweetened for savory dishes"),
            new IngredientSubstitutionDto.Substitution("Oat Milk", "1:1", "Creamy texture, good for baking"),
            new IngredientSubstitutionDto.Substitution("Coconut Milk", "1:1", "Adds slight coconut flavor"),
            new IngredientSubstitutionDto.Substitution("Soy Milk", "1:1", "Neutral flavor, high protein")
        ));

        rules.put("heavy cream", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Coconut Cream", "1:1", "For dairy-free option"),
            new IngredientSubstitutionDto.Substitution("Cashew Cream", "1:1", "Blend cashews with water"),
            new IngredientSubstitutionDto.Substitution("Half & Half + Butter", "7/8 cup half & half + 1/8 cup butter", "Closest to heavy cream")
        ));

        rules.put("cream cheese", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Greek Yogurt", "1:1", "Lower fat, tangy flavor"),
            new IngredientSubstitutionDto.Substitution("Coconut Cream", "1:1", "Dairy-free option"),
            new IngredientSubstitutionDto.Substitution("Silken Tofu", "1:1", "Blend until smooth")
        ));

        // Flour substitutions
        rules.put("all-purpose flour", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Whole Wheat Flour", "1:1", "Use 3/4 cup whole wheat + 1/4 cup all-purpose for better texture"),
            new IngredientSubstitutionDto.Substitution("Almond Flour", "1/4 cup less", "Gluten-free, higher fat"),
            new IngredientSubstitutionDto.Substitution("Coconut Flour", "1/4", "Highly absorbent, use with eggs"),
            new IngredientSubstitutionDto.Substitution("Oat Flour", "1:1", "Blend rolled oats to make flour")
        ));

        rules.put("wheat flour", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Rice Flour", "1:1", "For gluten-free baking"),
            new IngredientSubstitutionDto.Substitution("Buckwheat Flour", "1:1", "Nutty flavor, gluten-free"),
            new IngredientSubstitutionDto.Substitution("Quinoa Flour", "1:1", "High protein, mild flavor")
        ));

        // Egg substitutions
        rules.put("egg", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Flax Egg", "1 tbsp ground flaxseed + 3 tbsp water", "Let sit 5 minutes, equals 1 egg"),
            new IngredientSubstitutionDto.Substitution("Chia Egg", "1 tbsp chia seeds + 3 tbsp water", "Similar to flax egg"),
            new IngredientSubstitutionDto.Substitution("Applesauce", "1/4 cup", "For binding in baking"),
            new IngredientSubstitutionDto.Substitution("Mashed Banana", "1/4 cup", "Adds moisture and sweetness"),
            new IngredientSubstitutionDto.Substitution("Silken Tofu", "1/4 cup blended", "Good for dense baked goods")
        ));

        // Sugar substitutions
        rules.put("white sugar", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Honey", "3/4 cup honey = 1 cup sugar", "Reduce liquid by 1/4 cup"),
            new IngredientSubstitutionDto.Substitution("Maple Syrup", "3/4 cup = 1 cup sugar", "Adds maple flavor"),
            new IngredientSubstitutionDto.Substitution("Coconut Sugar", "1:1", "Similar texture and sweetness"),
            new IngredientSubstitutionDto.Substitution("Stevia", "1 tsp = 1 cup sugar", "Very concentrated, adjust to taste")
        ));

        rules.put("brown sugar", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("White Sugar + Molasses", "1 cup white sugar + 1 tbsp molasses", "Mix thoroughly"),
            new IngredientSubstitutionDto.Substitution("Coconut Sugar", "1:1", "Natural brown sugar alternative"),
            new IngredientSubstitutionDto.Substitution("Maple Syrup", "3/4 cup", "For liquid recipes")
        ));

        // Meat substitutions
        rules.put("ground beef", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Ground Turkey", "1:1", "Leaner option, may need extra seasoning"),
            new IngredientSubstitutionDto.Substitution("Ground Chicken", "1:1", "Lower fat, similar texture"),
            new IngredientSubstitutionDto.Substitution("Lentils", "1 cup cooked = 1 lb ground beef", "Plant-based protein"),
            new IngredientSubstitutionDto.Substitution("Mushrooms", "1:1 by weight", "Umami flavor, great for burgers")
        ));

        rules.put("chicken", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Tofu", "1:1 by weight", "Marinate for best flavor"),
            new IngredientSubstitutionDto.Substitution("Tempeh", "1:1 by weight", "Firm texture, high protein"),
            new IngredientSubstitutionDto.Substitution("Chickpeas", "1:1 by weight", "Great in salads and curries")
        ));

        // Vegetable oil substitutions
        rules.put("vegetable oil", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Olive Oil", "1:1", "Use extra virgin for salads, regular for cooking"),
            new IngredientSubstitutionDto.Substitution("Coconut Oil", "1:1", "Solid at room temp, melt before use"),
            new IngredientSubstitutionDto.Substitution("Avocado Oil", "1:1", "High smoke point, neutral flavor"),
            new IngredientSubstitutionDto.Substitution("Canola Oil", "1:1", "Neutral flavor, good for baking")
        ));

        // Leavening agents
        rules.put("baking powder", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Baking Soda + Cream of Tartar", "1/4 tsp baking soda + 1/2 tsp cream of tartar = 1 tsp baking powder", "Mix before adding"),
            new IngredientSubstitutionDto.Substitution("Baking Soda + Buttermilk", "1/4 tsp baking soda = 1 tsp baking powder", "Replace liquid with buttermilk")
        ));

        rules.put("baking soda", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Baking Powder", "3x the amount", "Use 3 tsp baking powder = 1 tsp baking soda")
        ));

        // Vinegar substitutions
        rules.put("white vinegar", Arrays.asList(
            new IngredientSubstitutionDto.Substitution("Apple Cider Vinegar", "1:1", "Milder flavor, slight apple taste"),
            new IngredientSubstitutionDto.Substitution("Lemon Juice", "1:1", "Adds citrus flavor"),
            new IngredientSubstitutionDto.Substitution("Rice Vinegar", "1:1", "Milder, slightly sweet")
        ));
    }

    /**
     * Return rule-based or AI-powered substitutions for a provided ingredient string.
     * Performs basic normalization and fallback partial matching.
     * 
     * @param ingredient The ingredient name to find substitutions for
     * @param useAI Whether to use AI-powered substitutions (when true, enhances rule-based with AI)
     * @return IngredientSubstitutionDto with list of substitutions
     */
    public IngredientSubstitutionDto suggest(String ingredient, boolean useAI) {
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return new IngredientSubstitutionDto(Collections.emptyList(), "none");
        }

        String key = normalize(ingredient);
        List<IngredientSubstitutionDto.Substitution> subs = rules.getOrDefault(key, Collections.emptyList());

        if (subs.isEmpty()) {
            // Try partial matching
            for (Map.Entry<String, List<IngredientSubstitutionDto.Substitution>> entry : rules.entrySet()) {
                String k = entry.getKey();
                if (!k.isEmpty() && (key.contains(k) || k.contains(key))) {
                    subs = new ArrayList<>(entry.getValue());
                    break;
                }
            }
        }

        boolean aiUsed = false;
        
        // If AI is requested and we have some results, enhance them
        if (useAI && !subs.isEmpty() && geminiService != null) {
            List<IngredientSubstitutionDto.Substitution> enhanced = enhanceWithAI(ingredient, subs);
            if (enhanced.size() > subs.size()) {
                subs = enhanced;
                aiUsed = true;
            }
        }

        // If AI is requested and no rule-based matches, try AI-only
        if (useAI && subs.isEmpty() && geminiService != null) {
            subs = getAISubstitutions(ingredient);
            if (!subs.isEmpty()) {
                aiUsed = true;
            }
        }

        // Fallback: if still empty, generate simple heuristic substitutions
        if (subs.isEmpty()) {
            subs = generateFallbacks(key);
        }

        String source;
        if (subs.isEmpty()) {
            source = "none";
        } else if (aiUsed) {
            source = "ai";
        } else {
            source = "rule-based";
        }
        return new IngredientSubstitutionDto(subs, source);
    }

    /**
     * Enhance existing rule-based substitutions with AI suggestions
     */
    private List<IngredientSubstitutionDto.Substitution> enhanceWithAI(String ingredient, List<IngredientSubstitutionDto.Substitution> existing) {
        List<IngredientSubstitutionDto.Substitution> enhanced = new ArrayList<>(existing);
        
        // Call AI service to get additional suggestions
        if (geminiService != null) {
            List<IngredientSubstitutionDto.Substitution> aiSubs = geminiService.getAISubstitutions(ingredient);
            
            // Add AI suggestions that are not already in the rule-based list
            for (IngredientSubstitutionDto.Substitution aiSub : aiSubs) {
                boolean exists = false;
                for (IngredientSubstitutionDto.Substitution existingSub : existing) {
                    if (existingSub.getIngredient().equalsIgnoreCase(aiSub.getIngredient())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    enhanced.add(aiSub);
                }
            }
        }
        
        return enhanced;
    }

    /**
     * Get AI-only substitutions for ingredients not in rule base
     */
    private List<IngredientSubstitutionDto.Substitution> getAISubstitutions(String ingredient) {
        if (geminiService != null) {
            List<IngredientSubstitutionDto.Substitution> aiSubs = geminiService.getAISubstitutions(ingredient);
            if (!aiSubs.isEmpty()) {
                return aiSubs;
            }
        }
        
        // Return empty list if AI service is not available or returns no results
        return Collections.emptyList();
    }

    private String normalize(String s) {
        String t = s.trim().toLowerCase();
        // Strip quantities, parentheses, and common words
        t = t.replaceAll("\\(.*?\\)", "")
             .replaceAll("\\d+", "")
             .replaceAll("\\s+", " ")
             .trim();
        return t;
    }

    /**
     * Suggest substitutions for a list of ingredients. Useful for batch UI rendering
     * where each ingredient in a recipe should have suggestions available.
     */
    public Map<String, IngredientSubstitutionDto> suggestAll(List<String> ingredients, boolean useAI) {
        Map<String, IngredientSubstitutionDto> result = new LinkedHashMap<>();
        if (ingredients == null) return result;
        for (String ing : ingredients) {
            IngredientSubstitutionDto dto = suggest(ing, useAI);
            result.put(ing, dto);
        }
        return result;
    }

    /**
     * Heuristic fallback substitutions when neither rule-based nor AI provides data.
     */
    private List<IngredientSubstitutionDto.Substitution> generateFallbacks(String key) {
        List<IngredientSubstitutionDto.Substitution> list = new ArrayList<>();
        // Simple category-based suggestions
        if (key.contains("salt")) {
            list.add(new IngredientSubstitutionDto.Substitution("Sea Salt", "1:1", "Cleaner mineral taste"));
            list.add(new IngredientSubstitutionDto.Substitution("Kosher Salt", "1:1", "Larger crystals, adjust by taste"));
        } else if (key.contains("sugar")) {
            list.add(new IngredientSubstitutionDto.Substitution("Honey", "3/4", "Reduce other liquids slightly"));
            list.add(new IngredientSubstitutionDto.Substitution("Maple Syrup", "3/4", "Adds its own flavor"));
        } else if (key.contains("oil")) {
            list.add(new IngredientSubstitutionDto.Substitution("Olive Oil", "1:1", "Good for sautéing"));
            list.add(new IngredientSubstitutionDto.Substitution("Avocado Oil", "1:1", "High smoke point"));
        } else if (key.contains("flour")) {
            list.add(new IngredientSubstitutionDto.Substitution("Whole Wheat Flour", "1:1", "Denser texture"));
            list.add(new IngredientSubstitutionDto.Substitution("Oat Flour", "1:1", "Mild flavor"));
        } else if (key.contains("milk")) {
            list.add(new IngredientSubstitutionDto.Substitution("Almond Milk", "1:1", "Neutral dairy-free option"));
            list.add(new IngredientSubstitutionDto.Substitution("Oat Milk", "1:1", "Creamier texture"));
        } else if (key.contains("egg")) {
            list.add(new IngredientSubstitutionDto.Substitution("Flax Egg", "1 tbsp flax + 3 tbsp water", "Let gel 5 minutes"));
            list.add(new IngredientSubstitutionDto.Substitution("Applesauce", "1/4 cup", "Moisture & binding in baking"));
        } else if (key.contains("butter")) {
            list.add(new IngredientSubstitutionDto.Substitution("Olive Oil", "3/4", "Use in cooking, adjust liquids"));
            list.add(new IngredientSubstitutionDto.Substitution("Coconut Oil", "1:1", "Solid at room temp"));
        } else if (key.contains("chicken")) {
            list.add(new IngredientSubstitutionDto.Substitution("Turkey", "1:1", "Leaner protein"));
            list.add(new IngredientSubstitutionDto.Substitution("Tofu", "1:1 by weight", "Marinate for flavor"));
        } else if (key.contains("beef")) {
            list.add(new IngredientSubstitutionDto.Substitution("Ground Turkey", "1:1", "Lower fat"));
            list.add(new IngredientSubstitutionDto.Substitution("Lentils", "1 cup cooked = 1 lb", "Plant-based option"));
        } else if (key.contains("rice")) {
            list.add(new IngredientSubstitutionDto.Substitution("Quinoa", "1:1", "Higher protein"));
            list.add(new IngredientSubstitutionDto.Substitution("Cauliflower Rice", "1:1", "Low-carb alternative"));
        } else if (key.contains("cream")) {
            list.add(new IngredientSubstitutionDto.Substitution("Coconut Cream", "1:1", "Dairy-free richness"));
            list.add(new IngredientSubstitutionDto.Substitution("Cashew Cream", "1:1", "Blend soaked cashews"));
        }
        return list;
    }
}
