-- =====================================================
-- Cookmate Database Update Script (Compatible Version)
-- =====================================================
-- Purpose: Add missing columns to existing cookmate database
-- Date: 2025-11-05
-- Safe: Yes - Uses error suppression for re-runs
-- =====================================================

USE cookmate;

-- =====================================================
-- 1. Add new columns to user_preferences table
-- =====================================================

-- Add cuisine_preferences column (ignore if exists)
SET @sql = 'ALTER TABLE user_preferences 
ADD COLUMN cuisine_preferences VARCHAR(1000) 
COMMENT ''Comma-separated list of preferred cuisines (e.g., Italian, Thai, Mexican)'' 
AFTER food_allergies';

SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'cookmate' 
      AND TABLE_NAME = 'user_preferences' 
      AND COLUMN_NAME = 'cuisine_preferences'
);

SET @sql = IF(@column_exists = 0, @sql, 'SELECT "Column cuisine_preferences already exists" AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add meal_types column (ignore if exists)
SET @sql = 'ALTER TABLE user_preferences 
ADD COLUMN meal_types VARCHAR(500) 
COMMENT ''Comma-separated list of preferred meal types (e.g., Breakfast, Lunch, Dinner)'' 
AFTER cuisine_preferences';

SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'cookmate' 
      AND TABLE_NAME = 'user_preferences' 
      AND COLUMN_NAME = 'meal_types'
);

SET @sql = IF(@column_exists = 0, @sql, 'SELECT "Column meal_types already exists" AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 2. Add unique constraint to user_dietary_restrictions
-- =====================================================

SET @constraint_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = 'cookmate' 
      AND TABLE_NAME = 'user_dietary_restrictions' 
      AND CONSTRAINT_NAME = 'uq_udr'
);

SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE user_dietary_restrictions ADD CONSTRAINT uq_udr UNIQUE (user_id, dietary_restriction_id)',
    'SELECT "Constraint uq_udr already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 3. Add unique constraint to favorites (if missing)
-- =====================================================

SET @constraint_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = 'cookmate' 
      AND TABLE_NAME = 'favorites' 
      AND CONSTRAINT_NAME = 'uq_favorite'
);

SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE favorites ADD CONSTRAINT uq_favorite UNIQUE (user_id, recipe_id)',
    'SELECT "Constraint uq_favorite already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 4. Populate lookup tables with sample data
-- =====================================================

-- Populate dietary_restrictions
INSERT IGNORE INTO dietary_restrictions (name, description) VALUES
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
INSERT IGNORE INTO cuisine_types (name, description, region) VALUES
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
INSERT IGNORE INTO meal_types (name, description, typical_time) VALUES
('Breakfast', 'Morning meal', '7:00 AM - 10:00 AM'),
('Lunch', 'Midday meal', '12:00 PM - 2:00 PM'),
('Dinner', 'Evening meal', '6:00 PM - 8:00 PM'),
('Snack', 'Light snack between meals', 'Anytime'),
('Dessert', 'Sweet course after a meal', 'After main meal');

-- =====================================================
-- 5. Verification
-- =====================================================

SELECT '✅ Database update completed successfully!' AS Status;

SELECT 'New columns in user_preferences:' AS Info;
SELECT COLUMN_NAME, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'cookmate' 
  AND TABLE_NAME = 'user_preferences' 
  AND COLUMN_NAME IN ('cuisine_preferences', 'meal_types');

SELECT 'Lookup table counts:' AS Info;
SELECT 
    (SELECT COUNT(*) FROM dietary_restrictions) AS dietary_restrictions,
    (SELECT COUNT(*) FROM cuisine_types) AS cuisine_types,
    (SELECT COUNT(*) FROM meal_types) AS meal_types;
