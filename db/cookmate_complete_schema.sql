-- ==============================================================
-- CookMate Database Schema - Complete Production Version
-- ==============================================================
-- Generated from complete codebase analysis
-- Backend: Spring Boot 3.1.5 + Hibernate 6.2.13
-- Frontend: React 18 + Vite
-- Database: MySQL 8.0
-- 
-- This schema includes:
-- - All 18 entities from the backend
-- - Complete foreign key relationships
-- - Proper indexes for performance
-- - Unique constraints
-- - Seed data for lookup tables
-- ==============================================================

-- Drop database if exists (CAUTION: USE WITH CARE)
-- DROP DATABASE IF EXISTS cookmate;

-- Create database with proper character set
CREATE DATABASE IF NOT EXISTS cookmate
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE cookmate;

-- ==============================================================
-- CORE TABLES
-- ==============================================================

-- -------------------------------------------------------------
-- Table: users
-- Description: Core user authentication and profile data
-- Relationships: 1:1 with user_preferences, 1:N with favorites, reviews, meal_plans, shopping_lists
-- -------------------------------------------------------------
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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT chk_users_provider CHECK (provider IN ('LOCAL', 'GOOGLE', 'FACEBOOK', 'TWITTER', 'INSTAGRAM')),
    
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: user_preferences
-- Description: User cooking preferences and settings (1:1 with users)
-- Relationships: 1:1 with users
-- -------------------------------------------------------------
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    cooking_skill_level VARCHAR(50),
    preferred_prep_time INT COMMENT 'in minutes',
    preferred_cook_time INT COMMENT 'in minutes',
    household_size INT,
    budget_preference VARCHAR(50),
    health_goals VARCHAR(1000),
    food_allergies VARCHAR(1000),
    cooking_equipment VARCHAR(1000),
    meal_planning_frequency VARCHAR(50),
    cuisine_preferences VARCHAR(1000) COMMENT 'Comma-separated cuisine names',
    meal_types VARCHAR(500) COMMENT 'Comma-separated meal type names',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_preferences_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_user_prefs_skill_level 
        CHECK (cooking_skill_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    CONSTRAINT chk_user_prefs_budget 
        CHECK (budget_preference IN ('LOW', 'MODERATE', 'HIGH', 'NO_PREFERENCE')),
    CONSTRAINT chk_user_prefs_meal_frequency 
        CHECK (meal_planning_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'RARELY', 'NEVER')),
    
    INDEX idx_user_preferences_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: password_reset_tokens
-- Description: Tokens for password reset functionality
-- Relationships: N:1 with users
-- -------------------------------------------------------------
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_password_reset_tokens_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    INDEX idx_password_reset_tokens_token (token),
    INDEX idx_password_reset_tokens_user_id (user_id),
    INDEX idx_password_reset_tokens_expiry (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- RECIPE MANAGEMENT TABLES
-- ==============================================================

-- -------------------------------------------------------------
-- Table: recipes
-- Description: Core recipe data with nutritional info and metadata
-- Relationships: N:1 with users (creator), 1:N with instructions, recipe_ingredients, reviews, favorites
-- -------------------------------------------------------------
CREATE TABLE recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    cuisine_type VARCHAR(50),
    meal_type VARCHAR(50),
    difficulty_level VARCHAR(20),
    prep_time INT COMMENT 'in minutes',
    cook_time INT COMMENT 'in minutes',
    total_time INT COMMENT 'in minutes',
    servings INT DEFAULT 4,
    calories INT,
    protein DECIMAL(10,2),
    carbs DECIMAL(10,2),
    fat DECIMAL(10,2),
    fiber DECIMAL(10,2),
    image_url VARCHAR(500),
    video_url VARCHAR(500),
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    is_dairy_free BOOLEAN DEFAULT FALSE,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    total_reviews INT DEFAULT 0,
    view_count INT DEFAULT 0,
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_recipes_created_by 
        FOREIGN KEY (created_by) REFERENCES users(id) 
        ON DELETE SET NULL,
    
    INDEX idx_recipes_title (title),
    INDEX idx_recipes_cuisine_type (cuisine_type),
    INDEX idx_recipes_meal_type (meal_type),
    INDEX idx_recipes_difficulty_level (difficulty_level),
    INDEX idx_recipes_created_by (created_by),
    INDEX idx_recipes_average_rating (average_rating),
    INDEX idx_recipes_created_at (created_at),
    INDEX idx_recipes_dietary (is_vegetarian, is_vegan, is_gluten_free, is_dairy_free),
    FULLTEXT INDEX idx_recipes_search (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: instructions
-- Description: Step-by-step cooking instructions for recipes
-- Relationships: N:1 with recipes
-- -------------------------------------------------------------
CREATE TABLE instructions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    timer_minutes INT,
    image_url VARCHAR(500),
    
    CONSTRAINT fk_instructions_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    
    INDEX idx_instructions_recipe_id (recipe_id),
    INDEX idx_instructions_step_number (recipe_id, step_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: ingredients
-- Description: Master list of ingredients
-- Relationships: 1:N with recipe_ingredients, shopping_list_items
-- -------------------------------------------------------------
CREATE TABLE ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_ingredients_name (name),
    INDEX idx_ingredients_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: recipe_ingredients
-- Description: Junction table linking recipes to ingredients with quantities
-- Relationships: N:1 with recipes, N:1 with ingredients
-- -------------------------------------------------------------
CREATE TABLE recipe_ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity DECIMAL(10,2),
    unit VARCHAR(50),
    notes TEXT,
    
    CONSTRAINT fk_recipe_ingredients_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_recipe_ingredients_ingredient 
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) 
        ON DELETE CASCADE,
    
    INDEX idx_recipe_ingredients_recipe_id (recipe_id),
    INDEX idx_recipe_ingredients_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: reviews
-- Description: User reviews and ratings for recipes
-- Relationships: N:1 with recipes, N:1 with users
-- -------------------------------------------------------------
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_reviews_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    
    INDEX idx_reviews_recipe_id (recipe_id),
    INDEX idx_reviews_user_id (user_id),
    INDEX idx_reviews_rating (rating),
    INDEX idx_reviews_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: favorites
-- Description: User favorite recipes
-- Relationships: N:1 with users, N:1 with recipes
-- -------------------------------------------------------------
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_favorites_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_favorites_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    
    UNIQUE KEY uk_favorites_user_recipe (user_id, recipe_id),
    
    INDEX idx_favorites_user_id (user_id),
    INDEX idx_favorites_recipe_id (recipe_id),
    INDEX idx_favorites_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: recently_viewed
-- Description: Track user's recently viewed recipes
-- Relationships: N:1 with users, N:1 with recipes
-- -------------------------------------------------------------
CREATE TABLE recently_viewed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_recently_viewed_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_recently_viewed_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    
    INDEX idx_recently_viewed_user_id (user_id),
    INDEX idx_recently_viewed_recipe_id (recipe_id),
    INDEX idx_recently_viewed_viewed_at (viewed_at),
    INDEX idx_recently_viewed_user_viewed (user_id, viewed_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- MEAL PLANNING TABLES
-- ==============================================================

-- -------------------------------------------------------------
-- Table: meal_plans
-- Description: User meal plans with date ranges
-- Relationships: N:1 with users, 1:N with meal_plan_recipes
-- -------------------------------------------------------------
CREATE TABLE meal_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_meal_plans_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    INDEX idx_meal_plans_user_id (user_id),
    INDEX idx_meal_plans_start_date (start_date),
    INDEX idx_meal_plans_end_date (end_date),
    INDEX idx_meal_plans_dates (user_id, start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: meal_plan_recipes
-- Description: Junction table linking meal plans to recipes with scheduling
-- Relationships: N:1 with meal_plans, N:1 with recipes
-- -------------------------------------------------------------
CREATE TABLE meal_plan_recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meal_plan_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    planned_date DATE NOT NULL,
    meal_time VARCHAR(20),
    servings INT DEFAULT 1,
    
    CONSTRAINT fk_meal_plan_recipes_meal_plan 
        FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_meal_plan_recipes_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    
    INDEX idx_meal_plan_recipes_meal_plan_id (meal_plan_id),
    INDEX idx_meal_plan_recipes_recipe_id (recipe_id),
    INDEX idx_meal_plan_recipes_planned_date (planned_date),
    INDEX idx_meal_plan_recipes_schedule (meal_plan_id, planned_date, meal_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- SHOPPING LIST TABLES
-- ==============================================================

-- -------------------------------------------------------------
-- Table: shopping_lists
-- Description: User shopping lists
-- Relationships: N:1 with users, 1:N with shopping_list_items
-- -------------------------------------------------------------
CREATE TABLE shopping_lists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_shopping_lists_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    INDEX idx_shopping_lists_user_id (user_id),
    INDEX idx_shopping_lists_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: shopping_list_items
-- Description: Individual items in shopping lists
-- Relationships: N:1 with shopping_lists, N:1 with ingredients (optional)
-- -------------------------------------------------------------
CREATE TABLE shopping_list_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shopping_list_id BIGINT NOT NULL,
    ingredient_id BIGINT,
    ingredient_name VARCHAR(100) NOT NULL,
    quantity DECIMAL(10,2),
    unit VARCHAR(50),
    is_purchased BOOLEAN DEFAULT FALSE,
    category VARCHAR(50),
    
    CONSTRAINT fk_shopping_list_items_shopping_list 
        FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_shopping_list_items_ingredient 
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) 
        ON DELETE SET NULL,
    
    INDEX idx_shopping_list_items_shopping_list_id (shopping_list_id),
    INDEX idx_shopping_list_items_ingredient_id (ingredient_id),
    INDEX idx_shopping_list_items_is_purchased (is_purchased),
    INDEX idx_shopping_list_items_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: recommendations
-- Description: AI-generated recipe recommendations
-- Relationships: N:1 with users, N:1 with recipes
-- -------------------------------------------------------------
CREATE TABLE recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recommendations_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_recommendations_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,

    INDEX idx_recommendations_recipe_id (recipe_id),
    INDEX idx_recommendations_user_id (user_id),
    INDEX idx_recommendations_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: ingredient_substitutions
-- Description: Ingredient substitution suggestions
-- Relationships: N:1 with recipes
-- -------------------------------------------------------------
CREATE TABLE ingredient_substitutions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient VARCHAR(100) NOT NULL,
    substitution VARCHAR(100) NOT NULL,
    use_ai BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_ingredient_substitutions_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) 
        ON DELETE CASCADE,

    INDEX idx_ingredient_substitutions_recipe_id (recipe_id),
    INDEX idx_ingredient_substitutions_ingredient (ingredient)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- LOOKUP / REFERENCE TABLES
-- ==============================================================

-- -------------------------------------------------------------
-- Table: dietary_restrictions
-- Description: Master list of dietary restrictions
-- Relationships: 1:N with user_dietary_restrictions
-- -------------------------------------------------------------
CREATE TABLE dietary_restrictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_dietary_restrictions_name (name),
    INDEX idx_dietary_restrictions_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: user_dietary_restrictions
-- Description: Junction table linking users to dietary restrictions
-- Relationships: N:1 with users, N:1 with dietary_restrictions
-- -------------------------------------------------------------
CREATE TABLE user_dietary_restrictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dietary_restriction_id BIGINT NOT NULL,
    strictness_level VARCHAR(50) DEFAULT 'MODERATE',
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_dietary_restrictions_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_user_dietary_restrictions_dietary_restriction 
        FOREIGN KEY (dietary_restriction_id) REFERENCES dietary_restrictions(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_user_dietary_restrictions_strictness 
        CHECK (strictness_level IN ('FLEXIBLE', 'MODERATE', 'STRICT', 'VERY_STRICT')),
    
    UNIQUE KEY uk_user_dietary_restrictions (user_id, dietary_restriction_id),
    
    INDEX idx_user_dietary_restrictions_user_id (user_id),
    INDEX idx_user_dietary_restrictions_dietary_restriction_id (dietary_restriction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: cuisine_types
-- Description: Master list of cuisine types
-- Relationships: Referenced by recipes.cuisine_type (string)
-- -------------------------------------------------------------
CREATE TABLE cuisine_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    region VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_cuisine_types_name (name),
    INDEX idx_cuisine_types_region (region),
    INDEX idx_cuisine_types_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Table: meal_types
-- Description: Master list of meal types (breakfast, lunch, etc.)
-- Relationships: Referenced by recipes.meal_type (string)
-- -------------------------------------------------------------
CREATE TABLE meal_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    typical_time VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_meal_types_name (name),
    INDEX idx_meal_types_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- SEED DATA FOR LOOKUP TABLES
-- ==============================================================

-- Insert dietary restrictions
INSERT INTO dietary_restrictions (name, description, is_active) VALUES
('Vegetarian', 'Does not consume meat, poultry, or fish', TRUE),
('Vegan', 'Does not consume any animal products', TRUE),
('Gluten-Free', 'Avoids gluten-containing grains', TRUE),
('Dairy-Free', 'Avoids dairy and lactose products', TRUE),
('Nut-Free', 'Avoids all types of nuts', TRUE),
('Soy-Free', 'Avoids soy and soy-based products', TRUE),
('Egg-Free', 'Avoids eggs and egg products', TRUE),
('Shellfish-Free', 'Avoids shellfish and crustaceans', TRUE),
('Low-Carb', 'Limits carbohydrate intake', TRUE),
('Keto', 'High-fat, very low-carb diet', TRUE),
('Paleo', 'Based on foods similar to what might have been eaten during the Paleolithic era', TRUE),
('Halal', 'Food prepared according to Islamic law', TRUE),
('Kosher', 'Food prepared according to Jewish dietary law', TRUE);

-- Insert cuisine types
INSERT INTO cuisine_types (name, description, region, is_active) VALUES
('Italian', 'Traditional Italian cooking with pasta, pizza, and Mediterranean flavors', 'Europe', TRUE),
('Chinese', 'Diverse regional Chinese cooking styles', 'Asia', TRUE),
('Mexican', 'Traditional Mexican cuisine with rich flavors and spices', 'North America', TRUE),
('Indian', 'Rich and aromatic spices with diverse regional variations', 'Asia', TRUE),
('Japanese', 'Traditional Japanese cooking emphasizing fresh ingredients', 'Asia', TRUE),
('Thai', 'Balance of sweet, sour, salty, and spicy flavors', 'Asia', TRUE),
('French', 'Classical French cooking techniques and flavors', 'Europe', TRUE),
('Mediterranean', 'Healthy cuisine from the Mediterranean region', 'Europe', TRUE),
('American', 'Traditional American comfort food', 'North America', TRUE),
('Korean', 'Korean cuisine featuring fermented foods and bold flavors', 'Asia', TRUE),
('Vietnamese', 'Fresh herbs and balanced flavors', 'Asia', TRUE),
('Middle Eastern', 'Cuisine from the Middle East region', 'Middle East', TRUE),
('Greek', 'Traditional Greek cuisine with olive oil and fresh vegetables', 'Europe', TRUE),
('Spanish', 'Spanish cooking with tapas and paella', 'Europe', TRUE),
('Caribbean', 'Island cuisine with tropical flavors', 'Caribbean', TRUE);

-- Insert meal types
INSERT INTO meal_types (name, description, typical_time, is_active) VALUES
('Breakfast', 'First meal of the day', 'Morning (6 AM - 10 AM)', TRUE),
('Brunch', 'Late breakfast or early lunch', 'Late Morning (10 AM - 12 PM)', TRUE),
('Lunch', 'Midday meal', 'Afternoon (12 PM - 2 PM)', TRUE),
('Dinner', 'Evening meal', 'Evening (6 PM - 9 PM)', TRUE),
('Snack', 'Light meal between main meals', 'Anytime', TRUE),
('Dessert', 'Sweet course at the end of a meal', 'After meals', TRUE),
('Appetizer', 'Small dish before the main course', 'Before main meal', TRUE),
('Beverage', 'Drinks and beverages', 'Anytime', TRUE);

-- Insert sample ingredients (common ingredients)
INSERT INTO ingredients (name, category) VALUES
-- Proteins
('Chicken Breast', 'Protein'),
('Ground Beef', 'Protein'),
('Salmon', 'Protein'),
('Eggs', 'Protein'),
('Tofu', 'Protein'),
-- Vegetables
('Onion', 'Vegetable'),
('Garlic', 'Vegetable'),
('Tomato', 'Vegetable'),
('Bell Pepper', 'Vegetable'),
('Carrot', 'Vegetable'),
('Broccoli', 'Vegetable'),
('Spinach', 'Vegetable'),
-- Grains
('Rice', 'Grain'),
('Pasta', 'Grain'),
('Bread', 'Grain'),
-- Dairy
('Milk', 'Dairy'),
('Cheese', 'Dairy'),
('Butter', 'Dairy'),
-- Spices & Seasonings
('Salt', 'Spice'),
('Black Pepper', 'Spice'),
('Olive Oil', 'Oil'),
('Soy Sauce', 'Condiment'),
-- Fruits
('Lemon', 'Fruit'),
('Apple', 'Fruit'),
('Banana', 'Fruit');

-- ==============================================================
-- ADDITIONAL INDEXES FOR PERFORMANCE
-- ==============================================================

-- Create additional composite indexes for common queries
CREATE INDEX idx_recipes_cuisine_meal ON recipes(cuisine_type, meal_type);
CREATE INDEX idx_recipes_dietary_filter ON recipes(is_vegetarian, is_vegan, is_gluten_free);
CREATE INDEX idx_recipes_rating_views ON recipes(average_rating DESC, view_count DESC);

-- ==============================================================
-- SCHEMA COMPLETION
-- ==============================================================

-- Display table count and statistics
SELECT 
    'Schema creation complete!' AS status,
    (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'cookmate') AS total_tables,
    (SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_schema = 'cookmate' AND constraint_type = 'FOREIGN KEY') AS foreign_keys,
    (SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = 'cookmate') AS total_indexes;

-- Show all tables
SHOW TABLES;
