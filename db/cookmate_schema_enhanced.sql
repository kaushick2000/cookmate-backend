-- Cookmate SQL Schema - ENHANCED VERSION
-- Generated: 2025-11-04
-- This version adds cuisine_preferences and meal_types columns to user_preferences

-- IMPORTANT: application.properties points to database `cookmate`
DROP DATABASE IF EXISTS cookmate;
CREATE DATABASE cookmate CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cookmate;

-- =====================================================
-- 1. USERS
-- =====================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    provider VARCHAR(20) DEFAULT 'LOCAL',
    provider_id VARCHAR(100),
    image_url VARCHAR(500),
    is_enabled BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. USER PREFERENCES (1:1 with users) - ENHANCED
-- =====================================================
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    cooking_skill_level VARCHAR(20),
    preferred_prep_time INT COMMENT 'minutes',
    preferred_cook_time INT COMMENT 'minutes',
    household_size INT,
    budget_preference VARCHAR(20),
    health_goals VARCHAR(1000),
    food_allergies VARCHAR(1000),
    cooking_equipment VARCHAR(1000),
    meal_planning_frequency VARCHAR(20),
    cuisine_preferences VARCHAR(1000) COMMENT 'Comma-separated list of preferred cuisines',
    meal_types VARCHAR(500) COMMENT 'Comma-separated list of preferred meal types',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. DIETARY RESTRICTIONS (lookup)
-- =====================================================
CREATE TABLE dietary_restrictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. USER_DIETARY_RESTRICTIONS (junction)
-- =====================================================
CREATE TABLE user_dietary_restrictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dietary_restriction_id BIGINT NOT NULL,
    strictness_level VARCHAR(20) DEFAULT 'MODERATE',
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_udr_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_udr_dr FOREIGN KEY (dietary_restriction_id) REFERENCES dietary_restrictions(id) ON DELETE CASCADE,
    CONSTRAINT uq_udr UNIQUE (user_id, dietary_restriction_id),
    INDEX idx_user_id (user_id),
    INDEX idx_dietary_restriction_id (dietary_restriction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. CUISINE TYPES (lookup)
-- =====================================================
CREATE TABLE cuisine_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    region VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. MEAL TYPES (lookup)
-- =====================================================
CREATE TABLE meal_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    typical_time VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. RECIPES
-- =====================================================
CREATE TABLE recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    cuisine_type VARCHAR(50),
    meal_type VARCHAR(50),
    difficulty_level VARCHAR(20),
    prep_time INT COMMENT 'minutes',
    cook_time INT COMMENT 'minutes',
    total_time INT COMMENT 'minutes',
    servings INT DEFAULT 4,
    calories INT,
    protein DECIMAL(10, 2),
    carbs DECIMAL(10, 2),
    fat DECIMAL(10, 2),
    fiber DECIMAL(10, 2),
    image_url VARCHAR(500),
    video_url VARCHAR(500),
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    is_dairy_free BOOLEAN DEFAULT FALSE,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    total_reviews INT DEFAULT 0,
    view_count INT DEFAULT 0,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_recipes_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_title (title),
    INDEX idx_cuisine_type (cuisine_type),
    INDEX idx_meal_type (meal_type),
    INDEX idx_difficulty_level (difficulty_level),
    INDEX idx_created_by (created_by),
    INDEX idx_average_rating (average_rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. INGREDIENTS
-- =====================================================
CREATE TABLE ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 9. RECIPE_INGREDIENTS (junction)
-- =====================================================
CREATE TABLE recipe_ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2),
    unit VARCHAR(50),
    notes TEXT,
    CONSTRAINT fk_ri_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    CONSTRAINT fk_ri_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 10. INSTRUCTIONS
-- =====================================================
CREATE TABLE instructions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    timer_minutes INT,
    image_url VARCHAR(500),
    CONSTRAINT fk_instructions_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_step_number (step_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 11. FAVORITES
-- =====================================================
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    CONSTRAINT uq_favorite UNIQUE (user_id, recipe_id),
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 12. REVIEWS
-- =====================================================
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 13. MEAL PLANS
-- =====================================================
CREATE TABLE meal_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_meal_plans_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 14. MEAL PLAN RECIPES (junction)
-- =====================================================
CREATE TABLE meal_plan_recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meal_plan_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    planned_date DATE NOT NULL,
    meal_time VARCHAR(20),
    servings INT DEFAULT 1,
    CONSTRAINT fk_mpr_meal_plan FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_mpr_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_meal_plan_id (meal_plan_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_planned_date (planned_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 15. SHOPPING LISTS
-- =====================================================
CREATE TABLE shopping_lists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_shopping_lists_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 16. SHOPPING LIST ITEMS
-- =====================================================
CREATE TABLE shopping_list_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shopping_list_id BIGINT NOT NULL,
    ingredient_id BIGINT,
    ingredient_name VARCHAR(100) NOT NULL,
    quantity DECIMAL(10, 2),
    unit VARCHAR(50),
    is_purchased BOOLEAN DEFAULT FALSE,
    category VARCHAR(50),
    CONSTRAINT fk_sli_shopping_list FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id) ON DELETE CASCADE,
    CONSTRAINT fk_sli_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE SET NULL,
    INDEX idx_shopping_list_id (shopping_list_id),
    INDEX idx_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 17. PASSWORD RESET TOKENS
-- =====================================================
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 18. RECENTLY VIEWED
-- =====================================================
CREATE TABLE recently_viewed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_recent_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_recent_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_viewed_at (viewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- OPTIONAL: SAMPLE DATA FOR LOOKUP TABLES
-- =====================================================

-- Populate dietary_restrictions
INSERT INTO dietary_restrictions (name, description) VALUES
('Vegetarian', 'No meat or fish'),
('Vegan', 'No animal products'),
('Gluten-Free', 'No gluten-containing grains'),
('Dairy-Free', 'No milk or dairy products'),
('Nut-Free', 'No tree nuts or peanuts'),
('Low-Carb', 'Reduced carbohydrate intake'),
('Keto', 'Very low-carb, high-fat diet'),
('Paleo', 'No processed foods or grains'),
('Halal', 'Islamic dietary laws'),
('Kosher', 'Jewish dietary laws');

-- Populate cuisine_types
INSERT INTO cuisine_types (name, description, region) VALUES
('Italian', 'Traditional Italian cuisine', 'Europe'),
('Chinese', 'Traditional Chinese cuisine', 'Asia'),
('Mexican', 'Traditional Mexican cuisine', 'North America'),
('Indian', 'Traditional Indian cuisine', 'Asia'),
('Japanese', 'Traditional Japanese cuisine', 'Asia'),
('Thai', 'Traditional Thai cuisine', 'Asia'),
('Mediterranean', 'Mediterranean diet and recipes', 'Europe'),
('French', 'Traditional French cuisine', 'Europe'),
('American', 'Traditional American cuisine', 'North America'),
('Greek', 'Traditional Greek cuisine', 'Europe'),
('Korean', 'Traditional Korean cuisine', 'Asia'),
('Spanish', 'Traditional Spanish cuisine', 'Europe');

-- Populate meal_types
INSERT INTO meal_types (name, description, typical_time) VALUES
('Breakfast', 'Morning meal', '7:00 AM - 10:00 AM'),
('Lunch', 'Midday meal', '12:00 PM - 2:00 PM'),
('Dinner', 'Evening meal', '6:00 PM - 8:00 PM'),
('Snack', 'Light snack between meals', 'Anytime'),
('Dessert', 'Sweet course after a meal', 'After main meal');
