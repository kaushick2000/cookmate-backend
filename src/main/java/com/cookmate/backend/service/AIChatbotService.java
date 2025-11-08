package com.cookmate.backend.service;

import com.cookmate.backend.dto.AIChatRequest;
import com.cookmate.backend.dto.AIChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIChatbotService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final boolean enabled;

    public AIChatbotService(
            @Value("${google.ai.api.key:}") String apiKey,
            @Value("${google.ai.enabled:false}") boolean enabled) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.enabled = enabled;
    }

    /**
     * Get AI-powered recipe suggestions based on available ingredients
     */
    public AIChatResponse getRecipeSuggestions(AIChatRequest request) {
        if (!enabled || apiKey == null || apiKey.isEmpty()) {
            return new AIChatResponse("I'm sorry, AI assistance is currently unavailable.", 
                                    new ArrayList<>(), "error");
        }

        try {
            String ingredients = String.join(", ", request.getIngredients());
            String dietaryRestrictions = request.getDietaryRestrictions() != null ? 
                String.join(", ", request.getDietaryRestrictions()) : "";
            
            String prompt = buildRecipeSuggestionPrompt(ingredients, dietaryRestrictions, request.getMealType());
            
            String aiResponse = callGeminiAPI(prompt);
            
            if (aiResponse != null && !aiResponse.isEmpty()) {
                List<AIChatResponse.RecipeSuggestion> suggestions = parseRecipeSuggestions(aiResponse);
                return new AIChatResponse(aiResponse, suggestions, "success");
            } else {
                return getFallbackResponse(request.getIngredients());
            }

        } catch (Exception e) {
            System.err.println("Error in AI chatbot service: " + e.getMessage());
            e.printStackTrace();
            return getFallbackResponse(request.getIngredients());
        }
    }

    /**
     * Handle general chat queries about cooking
     */
    public AIChatResponse handleGeneralChat(String message) {
        System.out.println("=== AI CHATBOT DEBUG ===");
        System.out.println("Enabled: " + enabled);
        System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("Message: " + message);
        
        if (!enabled || apiKey == null || apiKey.isEmpty()) {
            System.out.println("AI service not available - using fallback response");
            return getIntelligentFallbackResponse(message);
        }

        try {
            String prompt = buildGeneralChatPrompt(message);
            System.out.println("Built prompt length: " + prompt.length());
            
            String aiResponse = callGeminiAPI(prompt);
            
            if (aiResponse != null && !aiResponse.isEmpty()) {
                System.out.println("Gemini API returned successful response");
                return new AIChatResponse(aiResponse, new ArrayList<>(), "success");
            } else {
                System.out.println("Gemini API returned empty response - using intelligent fallback");
                return getIntelligentFallbackResponse(message);
            }

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to intelligent response due to API error");
            return getIntelligentFallbackResponse(message);
        }
    }
    
    /**
     * Provide intelligent fallback response based on question type
     */
    private AIChatResponse getIntelligentFallbackResponse(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Biryani questions
        if (lowerMessage.contains("biryani")) {
            if (lowerMessage.contains("hyderabadi")) {
                return new AIChatResponse(getHyderabadiBiryaniFallback(), new ArrayList<>(), "success");
            } else if (lowerMessage.contains("ingredients") || lowerMessage.contains("need")) {
                return new AIChatResponse(getBiryaniFallbackResponse(message), new ArrayList<>(), "success");
            } else {
                return new AIChatResponse(getGeneralBiryaniFallback(), new ArrayList<>(), "success");
            }
        }
        
        // Nationality/Cuisine-specific questions
        if (lowerMessage.contains("indian") || lowerMessage.contains("chinese") || lowerMessage.contains("italian") || 
            lowerMessage.contains("mexican") || lowerMessage.contains("thai") || lowerMessage.contains("japanese") ||
            lowerMessage.contains("french") || lowerMessage.contains("korean") || lowerMessage.contains("mediterranean") ||
            lowerMessage.contains("american") || lowerMessage.contains("british") || lowerMessage.contains("spanish") ||
            lowerMessage.contains("greek") || lowerMessage.contains("middle eastern") || lowerMessage.contains("moroccan") ||
            lowerMessage.contains("vietnamese") || lowerMessage.contains("lebanese") || lowerMessage.contains("turkish")) {
            return new AIChatResponse(getCuisineSpecificFallback(message), new ArrayList<>(), "success");
        }
        
        // Specific dish with ingredient variations (like fried rice, pasta, etc.)
        if ((lowerMessage.contains("fried rice") || lowerMessage.contains("pasta") || lowerMessage.contains("curry") ||
            lowerMessage.contains("stir fry") || lowerMessage.contains("soup") || lowerMessage.contains("salad")) &&
            (lowerMessage.contains("fewer ingredients") || lowerMessage.contains("simple") || lowerMessage.contains("easy") ||
             lowerMessage.contains("minimal") || lowerMessage.contains("basic"))) {
            return new AIChatResponse(getSimpleDishFallback(message), new ArrayList<>(), "success");
        }
        
        // Diet-specific questions (keto, weight loss, weight gain, etc.)
        if (lowerMessage.contains("keto") || lowerMessage.contains("ketogenic") || 
            lowerMessage.contains("weight loss") || lowerMessage.contains("lose weight") ||
            lowerMessage.contains("weight gain") || lowerMessage.contains("gain weight") ||
            lowerMessage.contains("diet") || lowerMessage.contains("low carb") ||
            lowerMessage.contains("vegan") || lowerMessage.contains("vegetarian") ||
            lowerMessage.contains("gluten free") || lowerMessage.contains("paleo")) {
            return new AIChatResponse(getDietSpecificFallback(message), new ArrayList<>(), "success");
        }
        
        // Quick recipe questions (time-based)
        if ((lowerMessage.contains("min") || lowerMessage.contains("quick") || lowerMessage.contains("fast")) && 
            (lowerMessage.contains("recipe") || lowerMessage.contains("make"))) {
            return new AIChatResponse(getQuickRecipeFallback(message), new ArrayList<>(), "success");
        }
        
        // Nutritional information questions
        if (lowerMessage.contains("nutrition") || lowerMessage.contains("calorie") || lowerMessage.contains("nutrient") || 
            lowerMessage.contains("healthy") || lowerMessage.contains("protein") || lowerMessage.contains("carb")) {
            return new AIChatResponse(getNutritionFallback(message), new ArrayList<>(), "success");
        }
        
        // Recipe questions
        if (lowerMessage.contains("recipe") || lowerMessage.contains("how to cook") || lowerMessage.contains("how to make")) {
            return new AIChatResponse(getRecipeFallback(message), new ArrayList<>(), "success");
        }
        
        // Ingredient substitution questions
        if (lowerMessage.contains("substitute") || lowerMessage.contains("replace") || lowerMessage.contains("instead of")) {
            return new AIChatResponse(getSubstitutionFallback(), new ArrayList<>(), "success");
        }
        
        // Cooking tips
        if (lowerMessage.contains("tip") || lowerMessage.contains("advice") || lowerMessage.contains("help")) {
            return new AIChatResponse(getCookingTipsFallback(), new ArrayList<>(), "success");
        }
        
        // Default response
        return new AIChatResponse(getDefaultCookingFallback(), new ArrayList<>(), "success");
    }
    
    /**
     * Hyderabadi style biryani response
     */
    private String getHyderabadiBiryaniFallback() {
        return "🍛 **Hyderabadi Dum Biryani Recipe**\n\n" +
               "**Essential Ingredients:**\n" +
               "• Basmati rice (2 cups)\n" +
               "• Chicken (1 kg, cut into pieces)\n" +
               "• Yogurt (1 cup)\n" +
               "• Fried onions (1 cup)\n" +
               "• Saffron soaked in warm milk\n" +
               "• Ghee (4 tbsp)\n" +
               "• Ginger-garlic paste (2 tbsp)\n\n" +
               "**Signature Spices:**\n" +
               "• Bay leaves, green cardamom, black cardamom\n" +
               "• Cinnamon, cloves, black cumin\n" +
               "• Red chili powder, biryani masala\n" +
               "• Fresh mint and coriander leaves\n\n" +
               "**Hyderabadi Style Steps:**\n" +
               "1. **Marinate**: Chicken with yogurt, spices for 2 hours\n" +
               "2. **Dum Style**: Cook in sealed pot (aluminum foil + tight lid)\n" +
               "3. **Layering**: Alternate rice and chicken layers\n" +
               "4. **Garnish**: Fried onions, saffron milk, mint\n" +
               "5. **Slow Cook**: 45 mins on low heat after 5 mins high heat\n\n" +
               "**Cooking Time**: 2.5 hours | **Serves**: 6-8 people\n" +
               "**Pro Tip**: Use heavy-bottomed pot and don't open lid during dum cooking!";
    }
    
    /**
     * General biryani response
     */
    private String getGeneralBiryaniFallback() {
        return "🍛 **Biryani Cooking Guide**\n\n" +
               "Biryani is a wonderful aromatic rice dish! Here are some key tips:\n\n" +
               "**Popular Types:**\n" +
               "• Hyderabadi Biryani - Slow-cooked dum style\n" +
               "• Lucknowi Biryani - Awadhi style with subtle flavors\n" +
               "• Kolkata Biryani - With potatoes and boiled eggs\n" +
               "• Chennai Biryani - Tangy and spicy South Indian style\n\n" +
               "**Key Success Tips:**\n" +
               "• Use good quality basmati rice\n" +
               "• Marinate meat for at least 1 hour\n" +
               "• Layer properly - rice, meat, garnish\n" +
               "• Use dum cooking method for authentic taste\n" +
               "• Don't skip saffron and fried onions!\n\n" +
               "What specific type of biryani would you like to learn about?";
    }
    
    /**
     * Recipe fallback response
     */
    private String getRecipeFallback(String message) {
        return "👨‍🍳 **Recipe Help Available!**\n\n" +
               "I'd love to help you with cooking! Here are some ways I can assist:\n\n" +
               "**Tell me about:**\n" +
               "• Ingredients you have available\n" +
               "• Type of cuisine you prefer\n" +
               "• Cooking time you have\n" +
               "• Dietary restrictions\n\n" +
               "**Popular Recipes I Can Help With:**\n" +
               "• Indian dishes (Biryani, Curry, Dal)\n" +
               "• Quick meals (Stir-fry, Pasta, Salads)\n" +
               "• Comfort food (Soups, Casseroles)\n" +
               "• Healthy options (Grilled, Steamed)\n\n" +
               "What ingredients do you have, or what type of dish are you craving?";
    }
    
    /**
     * Substitution fallback response
     */
    private String getSubstitutionFallback() {
        return "🔄 **Ingredient Substitution Guide**\n\n" +
               "**Common Substitutions:**\n" +
               "• **Yogurt** → Buttermilk, sour cream, or lemon juice + milk\n" +
               "• **Ginger-garlic paste** → Fresh minced ginger + garlic\n" +
               "• **Saffron** → Turmeric (for color), or skip for flavor\n" +
               "• **Ghee** → Butter, coconut oil, or vegetable oil\n" +
               "• **Fresh herbs** → Dried herbs (use 1/3 amount)\n\n" +
               "**Spice Substitutions:**\n" +
               "• **Garam masala** → Cinnamon + cardamom + cloves\n" +
               "• **Biryani masala** → Garam masala + bay leaves\n" +
               "• **Fresh chilies** → Chili powder (start with less)\n\n" +
               "What specific ingredient would you like to substitute?";
    }
    
    /**
     * Cooking tips fallback
     */
    private String getCookingTipsFallback() {
        return "💡 **Essential Cooking Tips**\n\n" +
               "**Rice Cooking:**\n" +
               "• Wash basmati rice until water runs clear\n" +
               "• Soak rice for 30 minutes before cooking\n" +
               "• Use 1:1.5 rice to water ratio\n\n" +
               "**Meat Preparation:**\n" +
               "• Marinate for at least 30 minutes\n" +
               "• Cook on medium heat to avoid burning\n" +
               "• Let meat rest before serving\n\n" +
               "**Flavor Enhancement:**\n" +
               "• Toast whole spices before grinding\n" +
               "• Add salt gradually and taste frequently\n" +
               "• Fresh herbs added at the end retain flavor\n\n" +
               "What specific cooking challenge can I help you with?";
    }
    
    /**
     * Default cooking response
     */
    private String getDefaultCookingFallback() {
        return "👋 **Welcome to CookMate AI Assistant!**\n\n" +
               "I'm here to help you with all your cooking needs! You can ask me about:\n\n" +
               "🍳 **Recipe Suggestions** - Tell me your ingredients\n" +
               "🥘 **Cooking Instructions** - Step-by-step guides\n" +
               "🌿 **Ingredient Substitutions** - Alternative options\n" +
               "⏱️ **Cooking Tips** - Pro techniques and advice\n" +
               "🍛 **Specific Dishes** - Detailed recipes\n" +
               "⚡ **Quick Recipes** - Fast meals under 15 mins\n" +
               "🥗 **Nutrition Info** - Calorie and nutrient details\n" +
               "🥑 **Diet-Specific** - Keto, weight loss/gain, vegan, gluten-free, paleo\n" +
               "🌍 **World Cuisines** - Indian, Chinese, Italian, Mexican, Thai, and more\n" +
               "🍚 **Simple Dishes** - Minimal ingredient versions of favorites\n\n" +
               "**Try asking:**\n" +
               "• \"What can I make with chicken and rice?\"\n" +
               "• \"I have 10 minutes, give me a quick recipe\"\n" +
               "• \"I'm on keto diet, what can I cook?\"\n" +
               "• \"How can I make fried rice with fewer ingredients?\"\n" +
               "• \"What are good Indian curry recipes?\"\n" +
               "• \"Give me simple pasta ideas\"\n" +
               "• \"What are good weight loss recipes?\"\n" +
               "• \"How many calories are in pasta?\"\n\n" +
               "What would you like to cook today?";
    }
    
    /**
     * Quick recipe fallback for time-constrained cooking
     */
    private String getQuickRecipeFallback(String message) {
        // Extract time if mentioned
        String timeInfo = "10 minutes";
        if (message.contains("5 min")) timeInfo = "5 minutes";
        else if (message.contains("15 min")) timeInfo = "15 minutes";
        else if (message.contains("20 min")) timeInfo = "20 minutes";
        
        return "⚡ **Quick " + timeInfo + " Recipes with 3-4 Ingredients**\n\n" +
               "**🍝 Garlic Spaghetti (8 mins)**\n" +
               "• Spaghetti, garlic, olive oil, parmesan\n" +
               "• Cook pasta, sauté garlic, toss with oil\n" +
               "• **Nutrition**: 450 calories, 15g protein, 65g carbs\n\n" +
               
               "**🥪 Avocado Toast (3 mins)**\n" +
               "• Bread, avocado, salt, lemon juice\n" +
               "• Toast bread, mash avocado, season\n" +
               "• **Nutrition**: 250 calories, 6g protein, 30g carbs, 15g healthy fats\n\n" +
               
               "**🍳 Scrambled Eggs (5 mins)**\n" +
               "• Eggs, butter, salt, pepper\n" +
               "• Beat eggs, cook on low heat, stir gently\n" +
               "• **Nutrition**: 280 calories, 20g protein, 2g carbs, 20g fats\n\n" +
               
               "**🥗 Greek Salad (7 mins)**\n" +
               "• Cucumber, tomato, feta, olive oil\n" +
               "• Chop vegetables, crumble feta, drizzle oil\n" +
               "• **Nutrition**: 180 calories, 8g protein, 12g carbs, 12g fats\n\n" +
               
               "**🍌 Banana Smoothie (2 mins)**\n" +
               "• Banana, milk, honey, ice\n" +
               "• Blend all ingredients until smooth\n" +
               "• **Nutrition**: 220 calories, 8g protein, 45g carbs, 3g fats\n\n" +
               
               "**💡 Quick Cooking Tips:**\n" +
               "• Keep basic ingredients stocked (eggs, pasta, bread)\n" +
               "• Prep vegetables in advance when possible\n" +
               "• Use one-pot/pan methods to save time\n" +
               "• Season generously for maximum flavor\n\n" +
               "Which quick recipe interests you most?";
    }
    
    /**
     * Nutrition information fallback
     */
    private String getNutritionFallback(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Check if asking about specific food
        if (lowerMessage.contains("pasta") || lowerMessage.contains("spaghetti")) {
            return getNutritionForPasta();
        } else if (lowerMessage.contains("rice") || lowerMessage.contains("biryani")) {
            return getNutritionForRice();
        } else if (lowerMessage.contains("chicken")) {
            return getNutritionForChicken();
        } else if (lowerMessage.contains("egg")) {
            return getNutritionForEggs();
        }
        
        // General nutrition guide
        return "🥗 **Nutrition Guide & Healthy Cooking**\n\n" +
               "**Daily Nutritional Targets (2000 cal diet):**\n" +
               "• **Protein**: 50-60g (lean meat, eggs, legumes)\n" +
               "• **Carbohydrates**: 225-325g (whole grains, fruits)\n" +
               "• **Fats**: 44-78g (healthy oils, nuts, avocado)\n" +
               "• **Fiber**: 25-30g (vegetables, fruits, whole grains)\n" +
               "• **Sodium**: <2300mg (limit processed foods)\n\n" +
               
               "**Healthy Cooking Methods:**\n" +
               "🔥 **Grilling/Roasting**: Retains nutrients, adds flavor\n" +
               "🍲 **Steaming**: Best for vegetables, preserves vitamins\n" +
               "🥘 **Stir-frying**: Quick cooking, minimal oil needed\n" +
               "🍳 **Poaching**: Gentle method for proteins\n\n" +
               
               "**Nutrient-Dense Ingredients:**\n" +
               "• **Leafy Greens**: Iron, folate, vitamins A, C, K\n" +
               "• **Fatty Fish**: Omega-3s, protein, vitamin D\n" +
               "• **Quinoa**: Complete protein, fiber, minerals\n" +
               "• **Berries**: Antioxidants, vitamin C, fiber\n" +
               "• **Nuts/Seeds**: Healthy fats, protein, vitamin E\n\n" +
               
               "**Portion Control Tips:**\n" +
               "• Protein: Palm size (3-4 oz)\n" +
               "• Carbs: Cupped hand (½ cup)\n" +
               "• Fats: Thumb size (1 tbsp)\n" +
               "• Vegetables: Two handfuls (1-2 cups)\n\n" +
               
               "What specific nutritional information would you like to know?";
    }
    
    /**
     * Pasta nutrition information
     */
    private String getNutritionForPasta() {
        return "🍝 **Pasta Nutrition Facts**\n\n" +
               "**1 Cup Cooked Spaghetti (140g):**\n" +
               "• **Calories**: 220\n" +
               "• **Protein**: 8g\n" +
               "• **Carbohydrates**: 44g\n" +
               "• **Fiber**: 2.5g\n" +
               "• **Fat**: 1g\n" +
               "• **Sodium**: 2mg\n\n" +
               
               "**Whole Wheat Pasta (same portion):**\n" +
               "• **Calories**: 174\n" +
               "• **Protein**: 7.5g\n" +
               "• **Carbohydrates**: 37g\n" +
               "• **Fiber**: 6g (much higher!)\n" +
               "• **Fat**: 0.8g\n\n" +
               
               "**Healthy Pasta Tips:**\n" +
               "• Choose whole wheat for more fiber and nutrients\n" +
               "• Control portions (1 cup cooked = 1 serving)\n" +
               "• Add vegetables to increase nutrition\n" +
               "• Use tomato-based vs. cream-based sauces\n" +
               "• Add lean protein (chicken, shrimp, beans)\n\n" +
               
               "**Popular Healthy Pasta Dishes:**\n" +
               "• Spaghetti with marinara + vegetables: ~300 calories\n" +
               "• Pasta primavera: ~350 calories\n" +
               "• Whole wheat pasta salad: ~280 calories";
    }
    
    /**
     * Rice nutrition information
     */
    private String getNutritionForRice() {
        return "🍚 **Rice & Biryani Nutrition Facts**\n\n" +
               "**1 Cup Cooked White Rice (158g):**\n" +
               "• **Calories**: 205\n" +
               "• **Protein**: 4.3g\n" +
               "• **Carbohydrates**: 45g\n" +
               "• **Fiber**: 0.6g\n" +
               "• **Fat**: 0.4g\n\n" +
               
               "**1 Cup Cooked Brown Rice:**\n" +
               "• **Calories**: 216\n" +
               "• **Protein**: 5g\n" +
               "• **Carbohydrates**: 45g\n" +
               "• **Fiber**: 3.5g (much higher!)\n" +
               "• **Fat**: 1.8g\n\n" +
               
               "**Chicken Biryani (1 serving, ~250g):**\n" +
               "• **Calories**: 450-550\n" +
               "• **Protein**: 25-30g\n" +
               "• **Carbohydrates**: 55-65g\n" +
               "• **Fat**: 15-20g\n" +
               "• **Fiber**: 2-3g\n\n" +
               
               "**Healthier Rice Options:**\n" +
               "• **Brown Rice**: More fiber and nutrients\n" +
               "• **Wild Rice**: Higher protein, antioxidants\n" +
               "• **Cauliflower Rice**: Low-carb alternative (25 cal/cup)\n" +
               "• **Quinoa**: Complete protein, more minerals\n\n" +
               
               "**Tips for Healthier Biryani:**\n" +
               "• Use brown basmati rice\n" +
               "• Reduce oil/ghee by half\n" +
               "• Add extra vegetables\n" +
               "• Use lean cuts of meat\n" +
               "• Control portion sizes";
    }
    
    /**
     * Chicken nutrition information
     */
    private String getNutritionForChicken() {
        return "🍗 **Chicken Nutrition Facts**\n\n" +
               "**Chicken Breast (3.5 oz/100g, skinless):**\n" +
               "• **Calories**: 165\n" +
               "• **Protein**: 31g (excellent source!)\n" +
               "• **Carbohydrates**: 0g\n" +
               "• **Fat**: 3.6g\n" +
               "• **Cholesterol**: 85mg\n\n" +
               
               "**Chicken Thigh (3.5 oz, skinless):**\n" +
               "• **Calories**: 209\n" +
               "• **Protein**: 26g\n" +
               "• **Fat**: 10.9g\n" +
               "• More flavor but higher fat content\n\n" +
               
               "**Key Nutrients in Chicken:**\n" +
               "• **Niacin (B3)**: Energy metabolism\n" +
               "• **Phosphorus**: Bone health\n" +
               "• **Selenium**: Antioxidant properties\n" +
               "• **Vitamin B6**: Brain function\n\n" +
               
               "**Healthy Cooking Methods:**\n" +
               "• **Grilled**: No added fats, great flavor\n" +
               "• **Baked**: Easy, retains moisture\n" +
               "• **Poached**: Lowest calorie method\n" +
               "• **Stir-fried**: Quick, minimal oil needed\n\n" +
               
               "**Healthy Chicken Dishes:**\n" +
               "• Grilled chicken salad: ~300 calories\n" +
               "• Chicken stir-fry with vegetables: ~350 calories\n" +
               "• Baked chicken with herbs: ~200 calories\n" +
               "• Chicken soup: ~150-250 calories";
    }
    
    /**
     * Eggs nutrition information
     */
    private String getNutritionForEggs() {
        return "🥚 **Egg Nutrition Facts**\n\n" +
               "**1 Large Egg (50g):**\n" +
               "• **Calories**: 70\n" +
               "• **Protein**: 6g (complete protein!)\n" +
               "• **Carbohydrates**: 0.6g\n" +
               "• **Fat**: 5g (healthy fats)\n" +
               "• **Cholesterol**: 186mg\n\n" +
               
               "**Key Nutrients in Eggs:**\n" +
               "• **Choline**: Brain development and function\n" +
               "• **Vitamin D**: Bone health, immunity\n" +
               "• **Lutein & Zeaxanthin**: Eye health\n" +
               "• **Vitamin B12**: Nervous system\n" +
               "• **Folate**: Cell division, DNA synthesis\n\n" +
               
               "**Cooking Methods (per egg):**\n" +
               "• **Boiled**: 70 calories (no added fat)\n" +
               "• **Poached**: 70 calories (no added fat)\n" +
               "• **Scrambled with butter**: ~100 calories\n" +
               "• **Fried**: 90-120 calories (depends on oil)\n\n" +
               
               "**Healthy Egg Dishes:**\n" +
               "• Vegetable omelet (2 eggs): ~200 calories\n" +
               "• Hard-boiled egg salad: ~150 calories\n" +
               "• Egg white scramble: ~50 calories (2 whites)\n" +
               "• Shakshuka (eggs in tomato): ~250 calories\n\n" +
               
               "**Health Benefits:**\n" +
               "• High-quality protein for muscle building\n" +
               "• Supports weight management (very satiating)\n" +
               "• Good for brain health and memory\n" +
               "• Affordable and versatile protein source";
    }
    
    /**
     * Diet-specific recipe recommendations
     */
    private String getDietSpecificFallback(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Keto diet
        if (lowerMessage.contains("keto") || lowerMessage.contains("ketogenic") || lowerMessage.contains("low carb")) {
            return getKetoDietFallback();
        }
        
        // Weight loss
        if (lowerMessage.contains("weight loss") || lowerMessage.contains("lose weight")) {
            return getWeightLossFallback();
        }
        
        // Weight gain
        if (lowerMessage.contains("weight gain") || lowerMessage.contains("gain weight")) {
            return getWeightGainFallback();
        }
        
        // Vegan/Vegetarian
        if (lowerMessage.contains("vegan") || lowerMessage.contains("vegetarian")) {
            return getVeganVegetarianFallback();
        }
        
        // Gluten-free
        if (lowerMessage.contains("gluten free") || lowerMessage.contains("celiac")) {
            return getGlutenFreeFallback();
        }
        
        // Paleo
        if (lowerMessage.contains("paleo")) {
            return getPaleoFallback();
        }
        
        // General diet advice
        return getGeneralDietFallback();
    }
    
    /**
     * Keto diet recipes and advice
     */
    private String getKetoDietFallback() {
        return "🥑 **Keto Diet Recipes & Guide**\n\n" +
               "**Keto Basics:** High fat (70-75%), moderate protein (20-25%), very low carb (<5%)\n" +
               "**Daily Target:** <20-25g net carbs, 70-100g protein, 155-200g fat\n\n" +
               
               "**🍳 Quick Keto Recipes:**\n\n" +
               "**Avocado & Egg Bowl (5 mins)**\n" +
               "• 1 avocado, 2 eggs, olive oil, salt\n" +
               "• **Macros**: 520 cal, 20g protein, 6g net carbs, 47g fat\n\n" +
               
               "**Keto Chicken Salad (10 mins)**\n" +
               "• Chicken breast, mayo, celery, leafy greens\n" +
               "• **Macros**: 380 cal, 35g protein, 4g net carbs, 25g fat\n\n" +
               
               "**Zucchini Noodles with Pesto (15 mins)**\n" +
               "• Zucchini, basil pesto, parmesan, pine nuts\n" +
               "• **Macros**: 320 cal, 12g protein, 8g net carbs, 28g fat\n\n" +
               
               "**Keto Fat Bomb (2 mins)**\n" +
               "• Cream cheese, coconut oil, cocoa powder, stevia\n" +
               "• **Macros**: 180 cal, 3g protein, 2g net carbs, 18g fat\n\n" +
               
               "**✅ Keto-Friendly Foods:**\n" +
               "• **Fats**: Avocado, olive oil, coconut oil, nuts, seeds\n" +
               "• **Proteins**: Meat, fish, eggs, cheese\n" +
               "• **Vegetables**: Leafy greens, broccoli, cauliflower, zucchini\n" +
               "• **Dairy**: Heavy cream, butter, full-fat cheese\n\n" +
               
               "**❌ Avoid:**\n" +
               "• Grains, sugar, fruits (except berries), potatoes, legumes\n\n" +
               
               "**💡 Keto Tips:**\n" +
               "• Track net carbs (total carbs - fiber)\n" +
               "• Stay hydrated and supplement electrolytes\n" +
               "• Meal prep to avoid carb temptations\n" +
               "• Focus on whole, unprocessed foods";
    }
    
    /**
     * Weight loss recipes and advice
     */
    private String getWeightLossFallback() {
        return "⚖️ **Weight Loss Recipes & Guide**\n\n" +
               "**Weight Loss Basics:** Create calorie deficit while maintaining nutrition\n" +
               "**Target**: 1200-1500 calories/day (adjust based on your needs)\n\n" +
               
               "**🥗 Low-Calorie, High-Volume Recipes:**\n\n" +
               "**Vegetable Soup (20 mins)**\n" +
               "• Mixed vegetables, vegetable broth, herbs\n" +
               "• **Nutrition**: 150 cal, 6g protein, 30g carbs, 1g fat\n" +
               "• **Benefits**: High fiber, very filling\n\n" +
               
               "**Grilled Chicken Salad (15 mins)**\n" +
               "• Chicken breast, mixed greens, tomatoes, cucumber\n" +
               "• **Nutrition**: 250 cal, 35g protein, 8g carbs, 8g fat\n" +
               "• **Benefits**: High protein, low calorie\n\n" +
               
               "**Zucchini Pasta (12 mins)**\n" +
               "• Spiralized zucchini, marinara sauce, lean ground turkey\n" +
               "• **Nutrition**: 280 cal, 25g protein, 15g carbs, 12g fat\n" +
               "• **Benefits**: Low carb substitute for pasta\n\n" +
               
               "**Egg White Omelet (8 mins)**\n" +
               "• 4 egg whites, vegetables, cooking spray\n" +
               "• **Nutrition**: 120 cal, 20g protein, 5g carbs, 1g fat\n" +
               "• **Benefits**: Very high protein, low calorie\n\n" +
               
               "**Greek Yogurt Parfait (3 mins)**\n" +
               "• Non-fat Greek yogurt, berries, cinnamon\n" +
               "• **Nutrition**: 180 cal, 20g protein, 25g carbs, 0g fat\n" +
               "• **Benefits**: High protein, satisfying\n\n" +
               
               "**🎯 Weight Loss Strategies:**\n" +
               "• **Portion Control**: Use smaller plates, measure portions\n" +
               "• **High Protein**: Aim for 25-30g protein per meal\n" +
               "• **Fiber-Rich**: Choose vegetables and whole grains\n" +
               "• **Hydration**: Drink water before meals\n" +
               "• **Meal Timing**: Eat regularly to avoid overeating\n\n" +
               
               "**📊 Calorie Guidelines:**\n" +
               "• Breakfast: 300-400 calories\n" +
               "• Lunch: 400-500 calories\n" +
               "• Dinner: 400-500 calories\n" +
               "• Snacks: 100-200 calories each";
    }
    
    /**
     * Weight gain recipes and advice
     */
    private String getWeightGainFallback() {
        return "💪 **Weight Gain Recipes & Guide**\n\n" +
               "**Weight Gain Basics:** Create calorie surplus with nutrient-dense foods\n" +
               "**Target**: 2500-3500 calories/day (adjust based on your needs)\n\n" +
               
               "**🥜 High-Calorie, Nutrient-Dense Recipes:**\n\n" +
               "**Peanut Butter Banana Smoothie (5 mins)**\n" +
               "• Banana, peanut butter, milk, oats, honey\n" +
               "• **Nutrition**: 650 cal, 25g protein, 75g carbs, 28g fat\n" +
               "• **Benefits**: Easy to consume, high calories\n\n" +
               
               "**Loaded Avocado Toast (8 mins)**\n" +
               "• Whole grain bread, avocado, eggs, olive oil, nuts\n" +
               "• **Nutrition**: 580 cal, 20g protein, 35g carbs, 42g fat\n" +
               "• **Benefits**: Healthy fats, complete nutrition\n\n" +
               
               "**Quinoa Power Bowl (25 mins)**\n" +
               "• Quinoa, chicken, sweet potato, nuts, olive oil\n" +
               "• **Nutrition**: 720 cal, 35g protein, 65g carbs, 32g fat\n" +
               "• **Benefits**: Complete protein, complex carbs\n\n" +
               
               "**Trail Mix Energy Balls (10 mins)**\n" +
               "• Dates, nuts, seeds, dark chocolate, coconut oil\n" +
               "• **Nutrition**: 280 cal, 8g protein, 25g carbs, 18g fat\n" +
               "• **Benefits**: Perfect high-calorie snack\n\n" +
               
               "**Pasta with Creamy Sauce (20 mins)**\n" +
               "• Whole wheat pasta, heavy cream, cheese, chicken\n" +
               "• **Nutrition**: 850 cal, 40g protein, 75g carbs, 38g fat\n" +
               "• **Benefits**: High calories, satisfying\n\n" +
               
               "**💡 Weight Gain Strategies:**\n" +
               "• **Frequent Meals**: 5-6 smaller meals throughout day\n" +
               "• **Healthy Fats**: Add nuts, oils, avocado to meals\n" +
               "• **Liquid Calories**: Smoothies, milk, protein shakes\n" +
               "• **Strength Training**: Build muscle along with weight\n" +
               "• **Calorie-Dense**: Choose foods with more calories per bite\n\n" +
               
               "**🥛 High-Calorie Additions:**\n" +
               "• Add 2 tbsp peanut butter: +190 calories\n" +
               "• Add 1 oz nuts: +160-180 calories\n" +
               "• Add 1 tbsp olive oil: +120 calories\n" +
               "• Add 1 cup whole milk: +150 calories";
    }
    
    /**
     * Vegan/Vegetarian recipes and advice
     */
    private String getVeganVegetarianFallback() {
        return "🌱 **Vegan & Vegetarian Recipes**\n\n" +
               "**Plant-Based Nutrition:** Focus on complete proteins and B12 supplementation\n\n" +
               
               "**🥘 High-Protein Plant-Based Recipes:**\n\n" +
               "**Lentil Power Bowl (25 mins)**\n" +
               "• Red lentils, quinoa, tahini, vegetables\n" +
               "• **Nutrition**: 520 cal, 22g protein, 68g carbs, 18g fat\n" +
               "• **Benefits**: Complete amino acid profile\n\n" +
               
               "**Chickpea Curry (20 mins)**\n" +
               "• Chickpeas, coconut milk, tomatoes, spices\n" +
               "• **Nutrition**: 380 cal, 18g protein, 45g carbs, 15g fat\n" +
               "• **Benefits**: High fiber, plant protein\n\n" +
               
               "**Tofu Stir-Fry (15 mins)**\n" +
               "• Firm tofu, mixed vegetables, sesame oil, soy sauce\n" +
               "• **Nutrition**: 320 cal, 20g protein, 18g carbs, 20g fat\n" +
               "• **Benefits**: Complete protein, quick cooking\n\n" +
               
               "**Chia Pudding (5 mins + overnight)**\n" +
               "• Chia seeds, plant milk, maple syrup, berries\n" +
               "• **Nutrition**: 280 cal, 12g protein, 32g carbs, 15g fat\n" +
               "• **Benefits**: Omega-3s, fiber, easy prep\n\n" +
               
               "**Bean & Vegetable Soup (30 mins)**\n" +
               "• Mixed beans, vegetables, vegetable broth, herbs\n" +
               "• **Nutrition**: 250 cal, 15g protein, 40g carbs, 3g fat\n" +
               "• **Benefits**: High fiber, very filling\n\n" +
               
               "**🌟 Essential Plant Proteins:**\n" +
               "• **Complete Proteins**: Quinoa, chia seeds, hemp seeds, soy products\n" +
               "• **Legumes**: Lentils, chickpeas, black beans, kidney beans\n" +
               "• **Nuts & Seeds**: Almonds, walnuts, pumpkin seeds, sunflower seeds\n" +
               "• **Grains**: Brown rice, oats, barley, buckwheat\n\n" +
               
               "**💊 Important Nutrients to Monitor:**\n" +
               "• **Vitamin B12**: Supplement required for vegans\n" +
               "• **Iron**: Combine with vitamin C for better absorption\n" +
               "• **Calcium**: Fortified plant milks, sesame seeds, leafy greens\n" +
               "• **Omega-3**: Flax seeds, chia seeds, walnuts, algae supplements\n" +
               "• **Zinc**: Pumpkin seeds, cashews, hemp seeds";
    }
    
    /**
     * Gluten-free recipes and advice
     */
    private String getGlutenFreeFallback() {
        return "🌾 **Gluten-Free Recipes & Guide**\n\n" +
               "**Gluten-Free Basics:** Avoid wheat, barley, rye, and contaminated oats\n\n" +
               
               "**🍽️ Naturally Gluten-Free Recipes:**\n\n" +
               "**Quinoa Salad Bowl (15 mins)**\n" +
               "• Quinoa, vegetables, olive oil, lemon juice\n" +
               "• **Nutrition**: 380 cal, 14g protein, 55g carbs, 12g fat\n" +
               "• **Benefits**: Complete protein, naturally GF\n\n" +
               
               "**Rice & Bean Bowl (20 mins)**\n" +
               "• Brown rice, black beans, salsa, avocado\n" +
               "• **Nutrition**: 420 cal, 16g protein, 65g carbs, 12g fat\n" +
               "• **Benefits**: Complete amino acids, filling\n\n" +
               
               "**Baked Sweet Potato (45 mins)**\n" +
               "• Sweet potato, black beans, vegetables, tahini\n" +
               "• **Nutrition**: 350 cal, 12g protein, 68g carbs, 8g fat\n" +
               "• **Benefits**: High fiber, vitamins A & C\n\n" +
               
               "**Corn Tortilla Tacos (10 mins)**\n" +
               "• Corn tortillas, grilled chicken, vegetables, salsa\n" +
               "• **Nutrition**: 320 cal, 25g protein, 35g carbs, 10g fat\n" +
               "• **Benefits**: Naturally gluten-free, customizable\n\n" +
               
               "**Vegetable Stir-Fry (15 mins)**\n" +
               "• Mixed vegetables, tamari sauce, rice, protein of choice\n" +
               "• **Nutrition**: 300 cal, 18g protein, 40g carbs, 8g fat\n" +
               "• **Benefits**: Quick, versatile, nutrient-dense\n\n" +
               
               "**✅ Gluten-Free Grains & Starches:**\n" +
               "• **Grains**: Rice, quinoa, millet, amaranth, buckwheat\n" +
               "• **Flours**: Almond flour, coconut flour, rice flour, chickpea flour\n" +
               "• **Starches**: Potatoes, sweet potatoes, corn, tapioca\n\n" +
               
               "**⚠️ Hidden Gluten Sources:**\n" +
               "• Soy sauce (use tamari instead)\n" +
               "• Seasonings and spice blends\n" +
               "• Processed meats and sauces\n" +
               "• Cross-contamination in shared kitchens\n\n" +
               
               "**🏪 Shopping Tips:**\n" +
               "• Look for \"Certified Gluten-Free\" labels\n" +
               "• Check ingredient lists carefully\n" +
               "• Shop the perimeter (fresh foods)\n" +
               "• Avoid bulk bins due to cross-contamination";
    }
    
    /**
     * Paleo diet recipes and advice
     */
    private String getPaleoFallback() {
        return "🦴 **Paleo Diet Recipes & Guide**\n\n" +
               "**Paleo Basics:** Eat like our ancestors - whole foods, no processed items\n" +
               "**Focus:** Meat, fish, vegetables, fruits, nuts, seeds (no grains, legumes, dairy)\n\n" +
               
               "**🥩 Paleo Recipe Ideas:**\n\n" +
               "**Grilled Salmon with Vegetables (20 mins)**\n" +
               "• Salmon fillet, broccoli, sweet potato, olive oil\n" +
               "• **Nutrition**: 450 cal, 35g protein, 25g carbs, 22g fat\n" +
               "• **Benefits**: Omega-3s, complete nutrition\n\n" +
               
               "**Cauliflower Rice Stir-Fry (15 mins)**\n" +
               "• Cauliflower rice, chicken, vegetables, coconut oil\n" +
               "• **Nutrition**: 320 cal, 28g protein, 12g carbs, 18g fat\n" +
               "• **Benefits**: Low carb, grain-free alternative\n\n" +
               
               "**Stuffed Bell Peppers (35 mins)**\n" +
               "• Bell peppers, ground beef, vegetables, herbs\n" +
               "• **Nutrition**: 380 cal, 30g protein, 15g carbs, 22g fat\n" +
               "• **Benefits**: Complete meal, nutrient-dense\n\n" +
               
               "**Zucchini Noodle Bowl (12 mins)**\n" +
               "• Spiralized zucchini, meatballs, marinara (no sugar)\n" +
               "• **Nutrition**: 340 cal, 25g protein, 18g carbs, 20g fat\n" +
               "• **Benefits**: Pasta substitute, satisfying\n\n" +
               
               "**Coconut Curry Chicken (25 mins)**\n" +
               "• Chicken, coconut milk, curry spices, vegetables\n" +
               "• **Nutrition**: 420 cal, 32g protein, 12g carbs, 28g fat\n" +
               "• **Benefits**: Rich flavor, anti-inflammatory spices\n\n" +
               
               "**✅ Paleo-Approved Foods:**\n" +
               "• **Proteins**: Grass-fed meat, wild fish, pastured eggs\n" +
               "• **Vegetables**: All except legumes\n" +
               "• **Fruits**: Berries, apples, citrus (moderate amounts)\n" +
               "• **Fats**: Olive oil, coconut oil, avocado, nuts, seeds\n" +
               "• **Herbs & Spices**: Fresh and dried (no additives)\n\n" +
               
               "**❌ Avoid:**\n" +
               "• Grains (wheat, rice, oats, quinoa)\n" +
               "• Legumes (beans, peanuts, soy)\n" +
               "• Dairy products\n" +
               "• Refined sugar and processed foods\n" +
               "• Vegetable oils (canola, soybean, corn)\n\n" +
               
               "**💡 Paleo Tips:**\n" +
               "• Focus on food quality (organic, grass-fed when possible)\n" +
               "• Prep vegetables in bulk for easy cooking\n" +
               "• Use herbs and spices for flavor variety\n" +
               "• Listen to your body's response to foods";
    }
    
    /**
     * General diet advice
     */
    private String getGeneralDietFallback() {
        return "🍽️ **General Diet & Nutrition Guide**\n\n" +
               "**Balanced Diet Basics:** Focus on whole foods, portion control, and variety\n\n" +
               
               "**🌟 Universal Healthy Eating Principles:**\n\n" +
               "**Daily Meal Structure:**\n" +
               "• **Breakfast**: Protein + Complex carbs + Healthy fats\n" +
               "• **Lunch**: Lean protein + Vegetables + Whole grains\n" +
               "• **Dinner**: Protein + Lots of vegetables + Small portion carbs\n" +
               "• **Snacks**: Protein + Fiber (nuts, Greek yogurt, fruits)\n\n" +
               
               "**🥗 Sample Balanced Meals:**\n\n" +
               "**Mediterranean Bowl (20 mins)**\n" +
               "• Grilled chicken, quinoa, vegetables, olive oil, feta\n" +
               "• **Nutrition**: 480 cal, 30g protein, 45g carbs, 20g fat\n\n" +
               
               "**Asian-Inspired Stir-Fry (15 mins)**\n" +
               "• Tofu/chicken, brown rice, mixed vegetables, sesame oil\n" +
               "• **Nutrition**: 420 cal, 25g protein, 50g carbs, 15g fat\n\n" +
               
               "**Mexican Burrito Bowl (18 mins)**\n" +
               "• Black beans, brown rice, vegetables, salsa, avocado\n" +
               "• **Nutrition**: 450 cal, 18g protein, 65g carbs, 15g fat\n\n" +
               
               "**📊 Portion Guidelines:**\n" +
               "• **Protein**: Palm size (3-4 oz)\n" +
               "• **Carbs**: Cupped hand (½ cup)\n" +
               "• **Fats**: Thumb size (1 tbsp)\n" +
               "• **Vegetables**: Two handfuls (1-2 cups)\n\n" +
               
               "**🎯 Healthy Habits:**\n" +
               "• **Hydration**: 8-10 glasses water daily\n" +
               "• **Meal Timing**: Regular meals, avoid skipping\n" +
               "• **Mindful Eating**: Eat slowly, listen to hunger cues\n" +
               "• **Variety**: Different colors and food groups daily\n" +
               "• **Preparation**: Plan and prep meals when possible\n\n" +
               
               "**🚫 Limit These:**\n" +
               "• Processed foods and added sugars\n" +
               "• Excessive saturated and trans fats\n" +
               "• High sodium foods\n" +
               "• Alcohol (if consumed, in moderation)\n" +
               "• Oversized portions\n\n" +
               
               "What specific dietary goals or restrictions do you have? I can provide more targeted advice!";
    }
    
    /**
     * Cuisine-specific recipe recommendations based on nationality
     */
    private String getCuisineSpecificFallback(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Indian cuisine
        if (lowerMessage.contains("indian")) {
            return getIndianCuisineFallback();
        }
        
        // Chinese cuisine
        if (lowerMessage.contains("chinese")) {
            return getChineseCuisineFallback();
        }
        
        // Italian cuisine
        if (lowerMessage.contains("italian")) {
            return getItalianCuisineFallback();
        }
        
        // Mexican cuisine
        if (lowerMessage.contains("mexican")) {
            return getMexicanCuisineFallback();
        }
        
        // Thai cuisine
        if (lowerMessage.contains("thai")) {
            return getThaiCuisineFallback();
        }
        
        // Japanese cuisine
        if (lowerMessage.contains("japanese")) {
            return getJapaneseCuisineFallback();
        }
        
        // Mediterranean cuisine
        if (lowerMessage.contains("mediterranean") || lowerMessage.contains("greek")) {
            return getMediterraneanCuisineFallback();
        }
        
        // Middle Eastern cuisine
        if (lowerMessage.contains("middle eastern") || lowerMessage.contains("lebanese") || lowerMessage.contains("turkish")) {
            return getMiddleEasternCuisineFallback();
        }
        
        // Korean cuisine
        if (lowerMessage.contains("korean")) {
            return getKoreanCuisineFallback();
        }
        
        // French cuisine
        if (lowerMessage.contains("french")) {
            return getFrenchCuisineFallback();
        }
        
        // General world cuisine guide
        return getWorldCuisineFallback();
    }
    
    /**
     * Simple dish variations with minimal ingredients
     */
    private String getSimpleDishFallback(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Fried Rice variations
        if (lowerMessage.contains("fried rice")) {
            return getFriedRiceSimpleFallback();
        }
        
        // Pasta variations
        if (lowerMessage.contains("pasta")) {
            return getPastaSimpleFallback();
        }
        
        // Curry variations
        if (lowerMessage.contains("curry")) {
            return getCurrySimpleFallback();
        }
        
        // Stir-fry variations
        if (lowerMessage.contains("stir fry")) {
            return getStirFrySimpleFallback();
        }
        
        // Soup variations
        if (lowerMessage.contains("soup")) {
            return getSoupSimpleFallback();
        }
        
        // Salad variations
        if (lowerMessage.contains("salad")) {
            return getSaladSimpleFallback();
        }
        
        // General simple cooking guide
        return getGeneralSimpleFallback();
    }
    
    /**
     * Indian cuisine recipes and guidance
     */
    private String getIndianCuisineFallback() {
        return "🇮🇳 **Indian Cuisine Recipes & Guide**\n\n" +
               "**Essential Indian Spices:** Turmeric, cumin, coriander, garam masala, cardamom, cloves\n\n" +
               
               "**🍛 Popular Indian Dishes:**\n\n" +
               "**Chicken Curry (30 mins)**\n" +
               "• Chicken, onions, tomatoes, ginger-garlic paste, spices\n" +
               "• **Nutrition**: 320 cal, 28g protein, 12g carbs, 18g fat\n" +
               "• **Region**: North Indian style\n\n" +
               
               "**Dal Tadka (25 mins)**\n" +
               "• Yellow lentils, turmeric, cumin, mustard seeds, tomatoes\n" +
               "• **Nutrition**: 180 cal, 12g protein, 25g carbs, 4g fat\n" +
               "• **Region**: Pan-Indian comfort food\n\n" +
               
               "**Aloo Gobi (20 mins)**\n" +
               "• Potatoes, cauliflower, onions, turmeric, coriander\n" +
               "• **Nutrition**: 150 cal, 4g protein, 28g carbs, 3g fat\n" +
               "• **Region**: Punjabi vegetarian classic\n\n" +
               
               "**Pulao Rice (25 mins)**\n" +
               "• Basmati rice, whole spices, ghee, vegetables/meat\n" +
               "• **Nutrition**: 250 cal, 6g protein, 45g carbs, 8g fat\n" +
               "• **Region**: Mughlai influence\n\n" +
               
               "**Masala Chai (10 mins)**\n" +
               "• Black tea, milk, cardamom, ginger, cinnamon\n" +
               "• **Nutrition**: 80 cal, 3g protein, 12g carbs, 3g fat\n" +
               "• **Region**: National beverage\n\n" +
               
               "**🌶️ Regional Specialties:**\n" +
               "• **North**: Butter chicken, naan, rajma, chole\n" +
               "• **South**: Dosa, sambar, rasam, coconut curry\n" +
               "• **West**: Dhokla, bhel puri, vada pav, gujarati thali\n" +
               "• **East**: Fish curry, mishti doi, rosogolla, macher jhol\n\n" +
               
               "**💡 Indian Cooking Tips:**\n" +
               "• Temper spices in oil for maximum flavor\n" +
               "• Use fresh ginger-garlic paste for authenticity\n" +
               "• Layer flavors - onions, tomatoes, then spices\n" +
               "• Finish with fresh coriander and garam masala";
    }
    
    /**
     * Chinese cuisine recipes and guidance
     */
    private String getChineseCuisineFallback() {
        return "🇨🇳 **Chinese Cuisine Recipes & Guide**\n\n" +
               "**Essential Ingredients:** Soy sauce, oyster sauce, sesame oil, ginger, garlic, scallions\n\n" +
               
               "**🥡 Popular Chinese Dishes:**\n\n" +
               "**Kung Pao Chicken (15 mins)**\n" +
               "• Chicken, peanuts, vegetables, dried chilies, soy sauce\n" +
               "• **Nutrition**: 380 cal, 32g protein, 18g carbs, 20g fat\n" +
               "• **Region**: Sichuan province\n\n" +
               
               "**Yangzhou Fried Rice (12 mins)**\n" +
               "• Rice, eggs, ham/char siu, peas, scallions\n" +
               "• **Nutrition**: 420 cal, 18g protein, 55g carbs, 15g fat\n" +
               "• **Region**: Jiangsu province\n\n" +
               
               "**Ma Po Tofu (20 mins)**\n" +
               "• Silken tofu, ground pork, fermented black beans, chili oil\n" +
               "• **Nutrition**: 280 cal, 18g protein, 12g carbs, 18g fat\n" +
               "• **Region**: Sichuan (spicy and numbing)\n\n" +
               
               "**Sweet & Sour Pork (25 mins)**\n" +
               "• Pork, bell peppers, pineapple, vinegar, sugar\n" +
               "• **Nutrition**: 450 cal, 25g protein, 35g carbs, 22g fat\n" +
               "• **Region**: Cantonese style\n\n" +
               
               "**Hot & Sour Soup (15 mins)**\n" +
               "• Tofu, mushrooms, bamboo shoots, egg, white pepper\n" +
               "• **Nutrition**: 120 cal, 8g protein, 10g carbs, 6g fat\n" +
               "• **Region**: Northern China\n\n" +
               
               "**🍜 Regional Specialties:**\n" +
               "• **Sichuan**: Spicy, numbing (mapo tofu, kung pao)\n" +
               "• **Cantonese**: Mild, sweet (dim sum, char siu)\n" +
               "• **Hunan**: Very spicy, aromatic (orange beef)\n" +
               "• **Beijing**: Northern style (Peking duck, zhajiangmian)\n\n" +
               
               "**🔥 Chinese Cooking Techniques:**\n" +
               "• **Stir-frying**: High heat, constant motion\n" +
               "• **Steaming**: Gentle cooking for dumplings, fish\n" +
               "• **Red cooking**: Braising in soy sauce and sugar\n" +
               "• **Velvet coating**: Egg white marinade for tender meat";
    }
    
    /**
     * Italian cuisine recipes and guidance
     */
    private String getItalianCuisineFallback() {
        return "🇮🇹 **Italian Cuisine Recipes & Guide**\n\n" +
               "**Essential Ingredients:** Olive oil, garlic, tomatoes, basil, parmesan, pasta\n\n" +
               
               "**🍝 Classic Italian Dishes:**\n\n" +
               "**Spaghetti Carbonara (15 mins)**\n" +
               "• Spaghetti, eggs, pecorino cheese, pancetta, black pepper\n" +
               "• **Nutrition**: 520 cal, 22g protein, 55g carbs, 24g fat\n" +
               "• **Region**: Roman (Lazio)\n\n" +
               
               "**Margherita Pizza (25 mins)**\n" +
               "• Pizza dough, tomato sauce, mozzarella, fresh basil\n" +
               "• **Nutrition**: 280 cal, 12g protein, 35g carbs, 12g fat\n" +
               "• **Region**: Neapolitan\n\n" +
               
               "**Risotto Milanese (30 mins)**\n" +
               "• Arborio rice, saffron, white wine, parmesan, butter\n" +
               "• **Nutrition**: 380 cal, 12g protein, 58g carbs, 12g fat\n" +
               "• **Region**: Lombardy (Milan)\n\n" +
               
               "**Osso Buco (2 hours)**\n" +
               "• Veal shanks, vegetables, white wine, tomatoes\n" +
               "• **Nutrition**: 450 cal, 35g protein, 15g carbs, 25g fat\n" +
               "• **Region**: Lombardy\n\n" +
               
               "**Tiramisu (30 mins + chill)**\n" +
               "• Ladyfingers, mascarpone, coffee, cocoa, eggs\n" +
               "• **Nutrition**: 380 cal, 8g protein, 35g carbs, 22g fat\n" +
               "• **Region**: Veneto\n\n" +
               
               "**🌍 Regional Italian Cooking:**\n" +
               "• **North**: Creamy sauces, risotto, polenta\n" +
               "• **Central**: Pasta, olive oil, simple preparations\n" +
               "• **South**: Tomatoes, seafood, spicy flavors\n" +
               "• **Sicily**: Arab influences, sweet & sour, nuts\n\n" +
               
               "**🍷 Italian Cooking Principles:**\n" +
               "• Use high-quality, few ingredients\n" +
               "• Respect seasonality and regional traditions\n" +
               "• Never overcook pasta - al dente is key\n" +
               "• Finish pasta in the sauce pan with pasta water";
    }
    
    /**
     * Mexican cuisine recipes and guidance
     */
    private String getMexicanCuisineFallback() {
        return "🇲🇽 **Mexican Cuisine Recipes & Guide**\n\n" +
               "**Essential Ingredients:** Lime, chilies, cilantro, cumin, beans, corn, avocado\n\n" +
               
               "**🌮 Popular Mexican Dishes:**\n\n" +
               "**Chicken Tacos (20 mins)**\n" +
               "• Corn tortillas, seasoned chicken, onions, cilantro, lime\n" +
               "• **Nutrition**: 280 cal, 22g protein, 25g carbs, 12g fat\n\n" +
               
               "**Guacamole (10 mins)**\n" +
               "• Avocados, lime juice, onions, tomatoes, cilantro\n" +
               "• **Nutrition**: 160 cal, 2g protein, 8g carbs, 15g fat\n\n" +
               
               "**Black Bean Quesadillas (15 mins)**\n" +
               "• Flour tortillas, black beans, cheese, peppers\n" +
               "• **Nutrition**: 420 cal, 18g protein, 45g carbs, 18g fat\n\n" +
               
               "**Mexican Rice (25 mins)**\n" +
               "• Long-grain rice, tomatoes, onions, chicken broth\n" +
               "• **Nutrition**: 220 cal, 5g protein, 45g carbs, 3g fat\n\n" +
               
               "**Salsa Verde (15 mins)**\n" +
               "• Tomatillos, jalapeños, onions, garlic, cilantro\n" +
               "• **Nutrition**: 25 cal, 1g protein, 6g carbs, 0g fat\n\n" +
               
               "**🌶️ Mexican Cooking Tips:**\n" +
               "• Char vegetables for smoky flavor\n" +
               "• Use fresh lime juice generously\n" +
               "• Toast spices before grinding\n" +
               "• Balance heat with acid and fat";
    }
    
    /**
     * Thai cuisine recipes and guidance
     */
    private String getThaiCuisineFallback() {
        return "🇹🇭 **Thai Cuisine Recipes & Guide**\n\n" +
               "**Essential Ingredients:** Fish sauce, lime, coconut milk, chilies, lemongrass, galangal\n\n" +
               
               "**🍜 Popular Thai Dishes:**\n\n" +
               "**Pad Thai (15 mins)**\n" +
               "• Rice noodles, shrimp/chicken, eggs, bean sprouts, tamarind\n" +
               "• **Nutrition**: 450 cal, 20g protein, 60g carbs, 15g fat\n\n" +
               
               "**Green Curry (25 mins)**\n" +
               "• Coconut milk, green curry paste, chicken, Thai basil\n" +
               "• **Nutrition**: 380 cal, 25g protein, 12g carbs, 28g fat\n\n" +
               
               "**Tom Yum Soup (20 mins)**\n" +
               "• Shrimp, mushrooms, lemongrass, lime leaves, chilies\n" +
               "• **Nutrition**: 150 cal, 18g protein, 8g carbs, 6g fat\n\n" +
               
               "**Thai Fried Rice (12 mins)**\n" +
               "• Jasmine rice, fish sauce, soy sauce, vegetables\n" +
               "• **Nutrition**: 320 cal, 12g protein, 55g carbs, 8g fat\n\n" +
               
               "**🥭 Thai Mango Sticky Rice (30 mins)**\n" +
               "• Glutinous rice, coconut milk, mango, sugar\n" +
               "• **Nutrition**: 280 cal, 4g protein, 58g carbs, 8g fat\n\n" +
               
               "**🌿 Thai Cooking Balance:**\n" +
               "• **Sweet**: Palm sugar, coconut\n" +
               "• **Sour**: Lime, tamarind\n" +
               "• **Salty**: Fish sauce, soy sauce\n" +
               "• **Spicy**: Chilies, white pepper\n" +
               "• **Aromatic**: Herbs and spices";
    }
    
    /**
     * General world cuisine guide
     */
    private String getWorldCuisineFallback() {
        return "🌍 **World Cuisine Guide**\n\n" +
               "Explore flavors from around the globe! Each cuisine has unique characteristics:\n\n" +
               
               "**🇮🇳 Indian**: Rich spices, complex curries, diverse regional styles\n" +
               "**🇨🇳 Chinese**: Stir-frying, balance of flavors, regional variations\n" +
               "**🇮🇹 Italian**: Simple quality ingredients, pasta, regional traditions\n" +
               "**🇲🇽 Mexican**: Corn, beans, chilies, fresh herbs and lime\n" +
               "**🇹🇭 Thai**: Sweet-sour-salty-spicy balance, coconut, herbs\n" +
               "**🇯🇵 Japanese**: Fresh ingredients, minimal processing, umami\n" +
               "**🇫🇷 French**: Technique-focused, sauces, wine in cooking\n" +
               "**🇬🇷 Greek**: Olive oil, seafood, vegetables, herbs\n" +
               "**🇰🇷 Korean**: Fermentation, spicy flavors, banchan sides\n" +
               "**🇱🇧 Lebanese**: Mezze style, grains, legumes, fresh herbs\n\n" +
               
               "**Ask me about specific cuisines like:**\n" +
               "• \"What are popular Indian dishes?\"\n" +
               "• \"How to make Chinese fried rice?\"\n" +
               "• \"What are Italian pasta basics?\"\n" +
               "• \"Give me Mexican taco recipes\"\n\n" +
               
               "What cuisine would you like to explore today?";
    }
    
    /**
     * Simple fried rice with minimal ingredients
     */
    private String getFriedRiceSimpleFallback() {
        return "🍚 **Simple Fried Rice with Fewer Ingredients**\n\n" +
               "**🥢 Basic Fried Rice (12 mins) - 4 ingredients:**\n" +
               "• Cooked rice (preferably day-old), eggs, soy sauce, oil\n" +
               "• **Method**: Heat oil, scramble eggs, add rice, season with soy sauce\n" +
               "• **Nutrition**: 320 cal, 12g protein, 55g carbs, 8g fat\n\n" +
               
               "**🥕 Vegetable Fried Rice (15 mins) - 5 ingredients:**\n" +
               "• Cooked rice, mixed frozen vegetables, eggs, soy sauce, garlic\n" +
               "• **Method**: Sauté garlic, add vegetables, rice, scrambled eggs\n" +
               "• **Nutrition**: 280 cal, 10g protein, 50g carbs, 6g fat\n\n" +
               
               "**🍤 Protein Fried Rice (18 mins) - 5 ingredients:**\n" +
               "• Cooked rice, leftover chicken/shrimp, eggs, soy sauce, green onions\n" +
               "• **Method**: Heat protein, add rice, eggs, season, garnish with onions\n" +
               "• **Nutrition**: 380 cal, 22g protein, 48g carbs, 10g fat\n\n" +
               
               "**🧄 Garlic Fried Rice (10 mins) - 4 ingredients:**\n" +
               "• Cooked rice, garlic, soy sauce, sesame oil\n" +
               "• **Method**: Fry minced garlic until golden, add rice and seasonings\n" +
               "• **Nutrition**: 300 cal, 6g protein, 58g carbs, 6g fat\n\n" +
               
               "**💡 Pro Tips for Perfect Fried Rice:**\n" +
               "• Use day-old rice (less sticky, better texture)\n" +
               "• High heat throughout cooking\n" +
               "• Don't overcrowd the pan\n" +
               "• Season gradually and taste as you go\n" +
               "• Push rice to one side when adding eggs\n\n" +
               
               "**⚡ Quick Variations:**\n" +
               "• Add frozen peas for color and nutrition\n" +
               "• Use butter instead of oil for richer flavor\n" +
               "• Sprinkle sesame seeds for crunch\n" +
               "• Finish with a squeeze of lime";
    }
    
    // Placeholder methods for remaining cuisines - can be expanded later
    private String getJapaneseCuisineFallback() {
        return "🇯🇵 **Japanese Cuisine** - Fresh ingredients, minimal processing, umami flavors. Try sushi, teriyaki chicken, miso soup, or ramen!";
    }
    
    private String getMediterraneanCuisineFallback() {
        return "🇬🇷 **Mediterranean Cuisine** - Olive oil, fresh vegetables, seafood, herbs. Try Greek salad, grilled fish, hummus, or moussaka!";
    }
    
    private String getMiddleEasternCuisineFallback() {
        return "🥙 **Middle Eastern Cuisine** - Spices, grains, legumes, fresh herbs. Try falafel, hummus, tabbouleh, or lamb kebabs!";
    }
    
    private String getKoreanCuisineFallback() {
        return "🇰🇷 **Korean Cuisine** - Fermented foods, spicy flavors, banchan sides. Try kimchi, bulgogi, bibimbap, or Korean fried chicken!";
    }
    
    private String getFrenchCuisineFallback() {
        return "🇫🇷 **French Cuisine** - Technique-focused, rich sauces, wine in cooking. Try coq au vin, ratatouille, or French onion soup!";
    }
    
    // Placeholder methods for simple dish variations
    private String getPastaSimpleFallback() {
        return "🍝 **Simple Pasta** - Aglio e olio (garlic + olive oil + pasta + parmesan). Ready in 10 minutes with just 4 ingredients!";
    }
    
    private String getCurrySimpleFallback() {
        return "🍛 **Simple Curry** - Chicken + onion + curry powder + coconut milk. One-pot meal ready in 25 minutes!";
    }
    
    private String getStirFrySimpleFallback() {
        return "🥘 **Simple Stir-Fry** - Vegetables + protein + soy sauce + oil. High heat cooking, ready in 8-10 minutes!";
    }
    
    private String getSoupSimpleFallback() {
        return "🍲 **Simple Soup** - Broth + vegetables + protein. One-pot comfort food, ready in 20 minutes!";
    }
    
    private String getSaladSimpleFallback() {
        return "🥗 **Simple Salad** - Greens + protein + dressing + toppings. Fresh, healthy, ready in 5 minutes!";
    }
    
    private String getGeneralSimpleFallback() {
        return "🍳 **Simple Cooking** - Focus on 3-5 ingredients, one cooking method, minimal prep time. Keep it simple and delicious!";
    }
    
    /**
     * Provide intelligent fallback response for biryani questions
     */
    private String getBiryaniFallbackResponse(String message) {
        return "🍛 **For your Biryani with basmati rice, chicken, onions, tomato, bay leaves, and cloves:**\n\n" +
               "**Missing Essential Ingredients:**\n" +
               "• Plain yogurt (1 cup) - for marinating chicken\n" +
               "• Ginger-garlic paste (2 tbsp) - key flavor base\n" +
               "• Fresh mint leaves (½ cup) - signature aroma\n" +
               "• Ghee or oil (4 tbsp) - for cooking\n" +
               "• Salt (to taste)\n\n" +
               "**Recommended Additions:**\n" +
               "• Green cardamom pods (4-5)\n" +
               "• Cinnamon stick (1 inch)\n" +
               "• Saffron (pinch) + warm milk\n" +
               "• Red chili powder (1 tsp)\n" +
               "• Fresh coriander leaves\n\n" +
               "**Quick Cooking Instructions:**\n" +
               "1. **Marinate**: Mix chicken with yogurt, ginger-garlic paste, salt, chili powder (30 min)\n" +
               "2. **Rice**: Boil basmati rice with bay leaves, cardamom, cloves until 70% cooked\n" +
               "3. **Chicken**: Cook marinated chicken with onions and tomatoes until tender\n" +
               "4. **Layer**: Alternate layers of rice and chicken in heavy-bottom pot\n" +
               "5. **Garnish**: Top with mint, saffron milk, fried onions\n" +
               "6. **Dum**: Cover tightly, cook on high for 3 min, then low heat for 45 min\n" +
               "7. **Rest**: Let it rest for 10 min before serving\n\n" +
               "**Cooking Time**: 1.5 hours | **Serves**: 4-6 people";
    }

    /**
     * Build prompt for recipe suggestions based on ingredients
     */
    private String buildRecipeSuggestionPrompt(String ingredients, String dietaryRestrictions, String mealType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("As a professional chef and cooking assistant, suggest 3-5 recipes using these ingredients: ")
              .append(ingredients)
              .append("\n\n");

        if (mealType != null && !mealType.isEmpty()) {
            prompt.append("Meal type preference: ").append(mealType).append("\n");
        }

        if (dietaryRestrictions != null && !dietaryRestrictions.isEmpty()) {
            prompt.append("Dietary restrictions: ").append(dietaryRestrictions).append("\n");
        }

        prompt.append("\nFor each recipe suggestion, provide:\n")
              .append("1. Recipe name\n")
              .append("2. Brief description (1-2 sentences)\n")
              .append("3. Estimated cooking time\n")
              .append("4. Difficulty level (Easy/Medium/Hard)\n")
              .append("5. Key ingredients from the available list\n\n")
              .append("Format each recipe suggestion clearly and separate them with '---'\n\n")
              .append("Also provide general cooking tips and suggestions for the ingredients provided.\n")
              .append("Be helpful, friendly, and encouraging in your response!");

        return prompt.toString();
    }

    /**
     * Build prompt for general cooking questions
     */
    private String buildGeneralChatPrompt(String message) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a friendly, expert chef and cooking assistant. ");
        
        String lowerMessage = message.toLowerCase();
        
        // Detect question type and provide specific context
        if (lowerMessage.contains("biryani")) {
            prompt.append("The user is asking about biryani. Provide detailed, authentic recipes with specific techniques, ingredients, and cooking methods. ");
        } else if (lowerMessage.contains("indian") || lowerMessage.contains("chinese") || lowerMessage.contains("italian") || 
                  lowerMessage.contains("mexican") || lowerMessage.contains("thai") || lowerMessage.contains("japanese") ||
                  lowerMessage.contains("french") || lowerMessage.contains("korean") || lowerMessage.contains("mediterranean")) {
            prompt.append("The user is asking about specific cuisine/nationality. Provide authentic recipes, cooking techniques, and cultural context for that cuisine. Include regional specialties and essential ingredients. ");
        } else if ((lowerMessage.contains("fried rice") || lowerMessage.contains("pasta") || lowerMessage.contains("curry")) &&
                  (lowerMessage.contains("fewer ingredients") || lowerMessage.contains("simple") || lowerMessage.contains("easy"))) {
            prompt.append("The user wants simple versions of dishes with minimal ingredients. Focus on 3-5 ingredient recipes that are quick and easy to make. Include cooking tips for success. ");
        } else if (lowerMessage.contains("keto") || lowerMessage.contains("ketogenic") || lowerMessage.contains("low carb")) {
            prompt.append("The user is asking about keto/low-carb recipes. Focus on high-fat, moderate-protein, very low-carb foods. Include net carb counts and macro breakdowns. ");
        } else if (lowerMessage.contains("weight loss") || lowerMessage.contains("lose weight")) {
            prompt.append("The user wants weight loss recipes. Focus on low-calorie, high-volume, nutrient-dense foods. Include calorie counts and portion control tips. ");
        } else if (lowerMessage.contains("weight gain") || lowerMessage.contains("gain weight")) {
            prompt.append("The user wants weight gain recipes. Focus on calorie-dense, nutrient-rich foods. Include healthy high-calorie additions and frequent meal suggestions. ");
        } else if (lowerMessage.contains("vegan") || lowerMessage.contains("vegetarian")) {
            prompt.append("The user wants plant-based recipes. Focus on complete proteins, B12 considerations, and nutrient combinations. Ensure no animal products. ");
        } else if (lowerMessage.contains("gluten free") || lowerMessage.contains("celiac")) {
            prompt.append("The user needs gluten-free recipes. Ensure all ingredients are naturally gluten-free or certified GF. Suggest alternative grains and flours. ");
        } else if (lowerMessage.contains("paleo")) {
            prompt.append("The user wants paleo recipes. Focus on whole foods, no grains, legumes, or dairy. Emphasize meat, vegetables, fruits, nuts, and seeds. ");
        } else if ((lowerMessage.contains("min") || lowerMessage.contains("quick") || lowerMessage.contains("fast")) && 
                  (lowerMessage.contains("recipe") || lowerMessage.contains("make"))) {
            prompt.append("The user wants quick recipes that can be made in limited time. Focus on simple 3-4 ingredient recipes with fast cooking methods. Include exact cooking times and nutritional information (calories, protein, carbs, fats) for each recipe. ");
        } else if (lowerMessage.contains("nutrition") || lowerMessage.contains("calorie") || lowerMessage.contains("nutrient") || 
                  lowerMessage.contains("healthy") || lowerMessage.contains("protein") || lowerMessage.contains("carb")) {
            prompt.append("The user is asking about nutrition. Provide detailed nutritional information including calories, macronutrients (protein, carbs, fats), vitamins, and minerals. Include healthy cooking tips and portion recommendations. ");
        } else if (lowerMessage.contains("recipe")) {
            prompt.append("The user wants recipe help. Be specific with ingredients, measurements, and step-by-step instructions. ");
        } else if (lowerMessage.contains("ingredient") && (lowerMessage.contains("substitute") || lowerMessage.contains("replace"))) {
            prompt.append("The user needs ingredient substitutions. Provide practical alternatives with ratios and usage tips. ");
        } else if (lowerMessage.contains("how to")) {
            prompt.append("The user wants cooking instructions. Give clear, numbered steps with timing and techniques. ");
        } else {
            prompt.append("Provide helpful cooking advice and suggestions. ");
        }
        
        prompt.append("Answer this cooking question: \"").append(message).append("\"\n\n");
        
        prompt.append("Please:\n")
              .append("• Use clear headers and bullet points for organization\n")
              .append("• Include specific measurements and timing\n")
              .append("• Add nutritional information when relevant (calories, protein, carbs, fats)\n")
              .append("• Be encouraging and friendly in tone\n")
              .append("• Provide practical, easy-to-follow instructions\n")
              .append("• Add helpful tips or variations when relevant\n")
              .append("• Use emojis to make the response more engaging\n")
              .append("• For quick recipes, focus on 3-4 ingredients and include prep/cook time\n")
              .append("• For nutrition questions, include daily value percentages when possible\n\n")
              .append("Keep your response informative but conversational!");
        
        return prompt.toString();
    }

    /**
     * Call Google Gemini API with enhanced error handling and debugging
     */
    private String callGeminiAPI(String prompt) {
        try {
            System.out.println("=== CALLING GEMINI API ===");
            
            // Try gemini-2.5-flash first (latest model)
            String url = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=%s",
                    apiKey
            );
            System.out.println("Primary URL: " + url.substring(0, url.lastIndexOf("=") + 5) + "...");

            String result = makeGeminiAPICall(url, prompt);
            if (result != null && !result.isEmpty()) {
                System.out.println("✅ Primary API call successful with gemini-2.5-flash");
                return result;
            }
            
            System.out.println("⚠️ Primary API call failed, trying fallback model...");
            
            // Fallback to gemini-2.5-pro if gemini-2.5-flash fails
            String fallbackUrl = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=%s",
                    apiKey
            );
            System.out.println("Fallback URL: " + fallbackUrl.substring(0, fallbackUrl.lastIndexOf("=") + 5) + "...");
            
            result = makeGeminiAPICall(fallbackUrl, prompt);
            if (result != null && !result.isEmpty()) {
                System.out.println("✅ Fallback API call successful with gemini-2.5-pro");
                return result;
            }
            
            System.out.println("❌ Both API calls failed, returning null");

        } catch (Exception e) {
            System.err.println("❌ Exception in callGeminiAPI: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Make actual API call to Gemini
     */
    private String makeGeminiAPICall(String url, String prompt) {
        try {
            GeminiRequest request = new GeminiRequest();
            GeminiRequest.Content content = new GeminiRequest.Content();
            GeminiRequest.Part part = new GeminiRequest.Part();
            part.text = prompt;
            content.parts = List.of(part);
            request.contents = List.of(content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "CookMate-AI-Assistant/1.0");

            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            System.out.println("Making API call to Gemini...");
            System.out.println("Prompt length: " + prompt.length() + " characters");
            
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );
            
            System.out.println("API Response Status: " + response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String result = extractTextFromResponse(response.getBody());
                System.out.println("✅ API Response Success. Text length: " + (result != null ? result.length() : 0));
                
                if (result != null && result.length() > 50) { // Ensure we got a substantial response
                    return result;
                } else {
                    System.out.println("⚠️ Response too short or empty, considering as failed");
                }
            } else {
                System.out.println("❌ API call unsuccessful - Status: " + response.getStatusCode());
                if (response.getBody() != null) {
                    System.out.println("Response body: " + response.getBody());
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error in makeGeminiAPICall: " + e.getMessage());
            if (e.getMessage() != null) {
                if (e.getMessage().contains("429")) {
                    System.err.println("Rate limit exceeded - too many requests");
                } else if (e.getMessage().contains("403")) {
                    System.err.println("API key might be invalid or insufficient permissions");
                } else if (e.getMessage().contains("404")) {
                    System.err.println("Model not found - API endpoint might be incorrect");
                }
            }
        }
        
        return null;
    }

    /**
     * Extract text from Gemini response
     */
    private String extractTextFromResponse(GeminiResponse response) {
        if (response.candidates == null || response.candidates.isEmpty()) {
            return null;
        }

        GeminiResponse.Candidate candidate = response.candidates.get(0);
        if (candidate.content == null || candidate.content.parts == null) {
            return null;
        }

        return candidate.content.parts.stream()
                .filter(p -> p.text != null)
                .map(p -> p.text)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Parse AI response to extract recipe suggestions
     */
    private List<AIChatResponse.RecipeSuggestion> parseRecipeSuggestions(String aiResponse) {
        List<AIChatResponse.RecipeSuggestion> suggestions = new ArrayList<>();
        
        // Split by --- separator
        String[] recipeParts = aiResponse.split("---");
        
        for (String part : recipeParts) {
            part = part.trim();
            if (part.length() > 50) { // Minimum length check
                AIChatResponse.RecipeSuggestion suggestion = extractRecipeFromText(part);
                if (suggestion != null) {
                    suggestions.add(suggestion);
                }
            }
        }
        
        return suggestions;
    }

    /**
     * Extract recipe information from text block
     */
    private AIChatResponse.RecipeSuggestion extractRecipeFromText(String text) {
        try {
            String[] lines = text.split("\n");
            String name = "";
            String description = "";
            String cookTime = "";
            String difficulty = "";
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                // Try to identify recipe name (usually first significant line)
                if (name.isEmpty() && line.length() > 5 && line.length() < 100) {
                    // Remove common prefixes
                    line = line.replaceFirst("^\\d+\\.\\s*", "")
                              .replaceFirst("^Recipe:?\\s*", "")
                              .replaceFirst("^Name:?\\s*", "");
                    name = line;
                } else if (line.toLowerCase().contains("description") || description.isEmpty()) {
                    if (line.toLowerCase().contains("description")) {
                        description = line.replaceFirst(".*description:?\\s*", "");
                    } else if (description.isEmpty() && line.length() > 20) {
                        description = line;
                    }
                } else if (line.toLowerCase().contains("time") && cookTime.isEmpty()) {
                    cookTime = line.replaceFirst(".*time:?\\s*", "");
                } else if (line.toLowerCase().contains("difficulty") && difficulty.isEmpty()) {
                    difficulty = line.replaceFirst(".*difficulty:?\\s*", "");
                }
            }
            
            if (!name.isEmpty()) {
                return new AIChatResponse.RecipeSuggestion(
                    name, 
                    description.isEmpty() ? "Delicious recipe suggestion" : description,
                    cookTime.isEmpty() ? "30 mins" : cookTime,
                    difficulty.isEmpty() ? "Medium" : difficulty
                );
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing recipe suggestion: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Provide fallback response when AI is unavailable
     */
    private AIChatResponse getFallbackResponse(List<String> ingredients) {
        String fallbackMessage = "I can help you with those ingredients! Here are some general suggestions:\n\n" +
                               "Based on your ingredients (" + String.join(", ", ingredients) + "), " +
                               "you could try making a stir-fry, salad, pasta dish, or soup. " +
                               "Check out our recipe collection for specific ideas!";
        
        return new AIChatResponse(fallbackMessage, new ArrayList<>(), "fallback");
    }

    // Request/Response DTOs for Gemini API
    private static class GeminiRequest {
        private List<Content> contents;

        public GeminiRequest() {}

        public List<Content> getContents() { return contents; }
        public void setContents(List<Content> contents) { this.contents = contents; }

        private static class Content {
            private List<Part> parts;

            public Content() {}

            public List<Part> getParts() { return parts; }
            public void setParts(List<Part> parts) { this.parts = parts; }
        }

        private static class Part {
            private String text;

            public Part() {}

            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
        }
    }

    private static class GeminiResponse {
        private List<Candidate> candidates;

        public GeminiResponse() {}

        public List<Candidate> getCandidates() { return candidates; }
        public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

        private static class Candidate {
            private Content content;

            public Candidate() {}

            public Content getContent() { return content; }
            public void setContent(Content content) { this.content = content; }

            private static class Content {
                private List<Part> parts;

                public Content() {}

                public List<Part> getParts() { return parts; }
                public void setParts(List<Part> parts) { this.parts = parts; }

                private static class Part {
                    private String text;

                    public Part() {}

                    public String getText() { return text; }
                    public void setText(String text) { this.text = text; }
                }
            }
        }
    }
}