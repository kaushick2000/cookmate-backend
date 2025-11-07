-- Start transaction for safety
START TRANSACTION;

-- Use the correct database
USE cookmate;

-- Check current state
SELECT 'Current Image Storage Status' as Message;
SELECT 
    COUNT(*) as total_recipes,
    SUM(CASE WHEN image_data IS NOT NULL THEN 1 ELSE 0 END) as recipes_with_blob,
    SUM(CASE WHEN image_url IS NOT NULL THEN 1 ELSE 0 END) as recipes_with_url
FROM recipes;

-- Ensure image_url column has sufficient length
ALTER TABLE recipes MODIFY COLUMN image_url VARCHAR(500);

-- Add index for image URL lookups if it doesn't exist
-- First check if index exists
SELECT 'Checking for existing index' as Message;
SELECT COUNT(*) INTO @index_exists 
FROM information_schema.statistics 
WHERE table_schema = 'cookmate' 
AND table_name = 'recipes' 
AND index_name = 'idx_recipe_image_url';

-- Create index if it doesn't exist
SET @create_index = IF(@index_exists > 0, 
    'SELECT "Index already exists" as Message', 
    'CREATE INDEX idx_recipe_image_url ON recipes(image_url)');
PREPARE create_index_stmt FROM @create_index;
EXECUTE create_index_stmt;
DEALLOCATE PREPARE create_index_stmt;

-- Show sample of recipes that will need migration
SELECT 'Recipes needing migration (sample)' as Message;
SELECT 
    id,
    title,
    CASE 
        WHEN image_data IS NOT NULL THEN 'Yes'
        ELSE 'No'
    END as has_blob_data,
    CASE 
        WHEN image_url IS NOT NULL THEN 'Yes'
        ELSE 'No'
    END as has_url
FROM recipes
WHERE image_data IS NOT NULL
LIMIT 5;

-- The following commands are commented out for safety
-- Only uncomment and run after verifying all images are migrated

/*
-- After migration is complete and verified, run these to clean up:
ALTER TABLE recipes DROP COLUMN image_data;
ALTER TABLE recipes DROP COLUMN image_filename;
ALTER TABLE recipes DROP COLUMN image_content_type;
*/

-- Commit the changes
COMMIT;

-- Final verification
SELECT 'Final Database Structure' as Message;
SHOW CREATE TABLE recipes;