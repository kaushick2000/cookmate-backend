-- Migration: Add recipe tracking to shopping list items
-- This allows ingredients to be grouped by their source recipe

-- Add source_recipe_id column to track which recipe an ingredient came from
ALTER TABLE shopping_list_items 
ADD COLUMN source_recipe_id BIGINT NULL;

-- Add foreign key constraint to reference recipes table
ALTER TABLE shopping_list_items 
ADD CONSTRAINT fk_shopping_list_items_source_recipe 
    FOREIGN KEY (source_recipe_id) REFERENCES recipes(id) 
    ON DELETE SET NULL;

-- Add index for better query performance
ALTER TABLE shopping_list_items 
ADD INDEX idx_shopping_list_items_source_recipe_id (source_recipe_id);

-- Optional: Add source_recipe_title column for denormalized storage (improves performance)
-- This prevents the need to join with recipes table every time
ALTER TABLE shopping_list_items 
ADD COLUMN source_recipe_title VARCHAR(255) NULL;