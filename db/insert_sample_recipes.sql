-- Insert Sample Recipes for Testing
USE cookmate;

-- Insert some sample recipes
INSERT INTO recipes (title, description, cuisine_type, meal_type, difficulty_level, prep_time, cook_time, total_time, servings, calories, protein, carbs, fat, fiber, image_url, is_vegetarian, is_vegan, is_gluten_free, is_dairy_free, average_rating, total_reviews, view_count, created_by, created_at, updated_at) VALUES
('Spaghetti Carbonara', 'Classic Italian pasta dish with eggs, cheese, and bacon', 'Italian', 'Dinner', 'Easy', 10, 15, 25, 4, 450, 18.5, 52.0, 20.0, 3.0, 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500', false, false, false, false, 4.5, 120, 1500, 1, NOW(), NOW()),

('Chicken Tikka Masala', 'Tender chicken in a creamy, spiced tomato sauce', 'Indian', 'Dinner', 'Medium', 30, 25, 55, 6, 380, 32.0, 28.0, 15.0, 4.0, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=500', false, false, true, false, 4.7, 250, 3200, 1, NOW(), NOW()),

('Caesar Salad', 'Fresh romaine lettuce with Caesar dressing, croutons, and parmesan', 'American', 'Lunch', 'Easy', 15, 0, 15, 2, 320, 12.0, 18.0, 22.0, 3.0, 'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=500', true, false, false, false, 4.2, 80, 900, 1, NOW(), NOW()),

('Vegetable Stir Fry', 'Colorful mix of vegetables in a savory sauce', 'Chinese', 'Dinner', 'Easy', 15, 10, 25, 4, 180, 8.0, 28.0, 6.0, 6.0, 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=500', true, true, true, true, 4.3, 150, 1800, 1, NOW(), NOW()),

('Margherita Pizza', 'Classic Italian pizza with tomato, mozzarella, and basil', 'Italian', 'Dinner', 'Medium', 20, 15, 35, 4, 520, 22.0, 68.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500', true, false, false, false, 4.6, 300, 4500, 1, NOW(), NOW()),

('Grilled Salmon', 'Fresh salmon fillet with lemon and herbs', 'American', 'Dinner', 'Easy', 10, 12, 22, 2, 420, 42.0, 2.0, 24.0, 0.5, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=500', false, false, true, true, 4.8, 180, 2100, 1, NOW(), NOW()),

('Pad Thai', 'Thai stir-fried rice noodles with peanuts and vegetables', 'Thai', 'Dinner', 'Medium', 20, 15, 35, 4, 450, 16.0, 58.0, 18.0, 3.5, 'https://images.unsplash.com/photo-1559314809-0d155014e29e?w=500', false, false, true, true, 4.5, 220, 2800, 1, NOW(), NOW()),

('Greek Salad', 'Fresh vegetables with feta cheese and olives', 'Greek', 'Lunch', 'Easy', 15, 0, 15, 4, 220, 8.0, 12.0, 16.0, 4.0, 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=500', true, false, true, false, 4.4, 95, 1100, 1, NOW(), NOW()),

('Beef Tacos', 'Seasoned ground beef in soft tortillas with toppings', 'Mexican', 'Dinner', 'Easy', 15, 15, 30, 4, 480, 28.0, 42.0, 22.0, 6.0, 'https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?w=500', false, false, false, false, 4.3, 200, 2500, 1, NOW(), NOW()),

('Mushroom Risotto', 'Creamy Italian rice dish with mushrooms', 'Italian', 'Dinner', 'Hard', 10, 35, 45, 4, 390, 12.0, 62.0, 12.0, 3.0, 'https://images.unsplash.com/photo-1476124369491-c2be09a48880?w=500', true, false, true, false, 4.7, 140, 1600, 1, NOW(), NOW()),

('Buddha Bowl', 'Healthy bowl with quinoa, roasted vegetables, and tahini', 'Mediterranean', 'Lunch', 'Easy', 20, 25, 45, 2, 420, 14.0, 52.0, 18.0, 12.0, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500', true, true, true, true, 4.5, 175, 1900, 1, NOW(), NOW()),

('French Toast', 'Classic breakfast with cinnamon and maple syrup', 'French', 'Breakfast', 'Easy', 5, 10, 15, 2, 350, 12.0, 48.0, 12.0, 2.0, 'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=500', true, false, false, false, 4.2, 110, 1300, 1, NOW(), NOW()),

('Chicken Fajitas', 'Sizzling chicken with peppers and onions', 'Mexican', 'Dinner', 'Easy', 15, 15, 30, 4, 420, 35.0, 38.0, 14.0, 5.0, 'https://images.unsplash.com/photo-1599974579688-8dbdd335e16e?w=500', false, false, true, false, 4.6, 190, 2300, 1, NOW(), NOW()),

('Tom Yum Soup', 'Spicy and sour Thai soup with shrimp', 'Thai', 'Lunch', 'Medium', 15, 20, 35, 4, 180, 18.0, 12.0, 6.0, 2.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', false, false, true, true, 4.4, 130, 1500, 1, NOW(), NOW()),

('Veggie Burger', 'Plant-based burger with all the fixings', 'American', 'Lunch', 'Easy', 10, 12, 22, 4, 380, 22.0, 45.0, 14.0, 8.0, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=500', true, true, false, false, 4.1, 160, 1700, 1, NOW(), NOW());

-- Verify the inserts
SELECT COUNT(*) as 'Total Recipes Inserted' FROM recipes;
SELECT title, cuisine_type, meal_type, difficulty_level FROM recipes LIMIT 5;
