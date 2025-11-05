-- =====================================================
-- Cookmate Database Update Script
-- =====================================================
-- Purpose: Add missing columns to existing cookmate database
-- Date: 2025-11-04
-- Safe: Yes - Only adds new columns, no data loss
-- =====================================================

USE cookmate;

-- =====================================================
-- 1. Add new columns to user_preferences table
-- =====================================================
-- Check if columns exist before adding to prevent errors on re-run

-- Add cuisine_preferences column
ALTER TABLE user_preferences 
ADD COLUMN IF NOT EXISTS cuisine_preferences VARCHAR(1000) 
COMMENT 'Comma-separated list of preferred cuisines (e.g., Italian, Thai, Mexican)' 
AFTER food_allergies;

-- Add meal_types column
ALTER TABLE user_preferences 
ADD COLUMN IF NOT EXISTS meal_types VARCHAR(500) 
COMMENT 'Comma-separated list of preferred meal types (e.g., Breakfast, Lunch, Dinner)' 
AFTER cuisine_preferences;

-- =====================================================
-- 2. Add unique constraint to user_dietary_restrictions
-- =====================================================
-- Prevent duplicate dietary restriction entries per user

-- Check if constraint already exists
SET @constraint_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = 'cookmate' 
      AND TABLE_NAME = 'user_dietary_restrictions' 
      AND CONSTRAINT_NAME = 'uq_udr'
);

-- Add constraint only if it doesn't exist
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
-- 4. Optional: Populate lookup tables with sample data
-- =====================================================
-- Only insert if tables are empty

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

-- Show updated user_preferences structure
SELECT 'user_preferences table structure:' AS info;
DESCRIBE user_preferences;

-- Show constraints on user_dietary_restrictions
SELECT 'user_dietary_restrictions constraints:' AS info;
SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE 
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
WHERE TABLE_SCHEMA = 'cookmate' 
  AND TABLE_NAME = 'user_dietary_restrictions';

-- Show counts of lookup tables
SELECT 'Lookup table counts:' AS info;
SELECT 
    (SELECT COUNT(*) FROM dietary_restrictions) AS dietary_restrictions_count,
    (SELECT COUNT(*) FROM cuisine_types) AS cuisine_types_count,
    (SELECT COUNT(*) FROM meal_types) AS meal_types_count;

-- =====================================================
-- DONE!
-- =====================================================
SELECT '✅ Database update completed successfully!' AS status;
SELECT 'You can now restart your backend server.' AS next_step;
