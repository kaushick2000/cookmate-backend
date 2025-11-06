-- Comprehensive Recipe Data for All Combinations
-- This script adds recipes for various cuisine, meal type, and difficulty combinations
USE cookmate;

-- Delete existing recipes to start fresh
DELETE FROM recipes;

-- Reset auto-increment
ALTER TABLE recipes AUTO_INCREMENT = 1;

-- Insert comprehensive recipe data covering all combinations
INSERT INTO recipes (title, description, cuisine_type, meal_type, difficulty_level, prep_time, cook_time, total_time, servings, calories, protein, carbs, fat, fiber, image_url, is_vegetarian, is_vegan, is_gluten_free, is_dairy_free, average_rating, total_reviews, view_count, created_at, updated_at) VALUES

-- AMERICAN RECIPES
('Classic Pancakes', 'Fluffy buttermilk pancakes with maple syrup', 'American', 'Breakfast', 'Easy', 10, 15, 25, 4, 320, 8.0, 58.0, 8.0, 2.0, 'https://images.unsplash.com/photo-1528207776546-365bb710ee93?w=500', true, false, false, false, 4.3, 145, 1650, NOW(), NOW()),
('Breakfast Burrito', 'Scrambled eggs, cheese, and bacon in a tortilla', 'American', 'Breakfast', 'Easy', 10, 10, 20, 2, 450, 22.0, 38.0, 24.0, 3.0, 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=500', false, false, false, false, 4.4, 98, 1200, NOW(), NOW()),
('Eggs Benedict', 'Poached eggs with hollandaise on English muffin', 'American', 'Breakfast', 'Hard', 15, 20, 35, 2, 520, 18.0, 32.0, 36.0, 2.0, 'https://images.unsplash.com/photo-1608039755401-742074f0548d?w=500', false, false, false, false, 4.7, 167, 1890, NOW(), NOW()),

('Caesar Salad', 'Fresh romaine lettuce with Caesar dressing and croutons', 'American', 'Lunch', 'Easy', 15, 0, 15, 2, 320, 12.0, 18.0, 22.0, 3.0, 'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=500', true, false, false, false, 4.2, 80, 900, NOW(), NOW()),
('Club Sandwich', 'Triple-decker sandwich with turkey, bacon, and veggies', 'American', 'Lunch', 'Easy', 10, 5, 15, 2, 520, 32.0, 45.0, 22.0, 4.0, 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=500', false, false, false, false, 4.5, 123, 1450, NOW(), NOW()),
('Lobster Roll', 'Fresh lobster meat in a toasted bun', 'American', 'Lunch', 'Medium', 20, 10, 30, 2, 380, 28.0, 32.0, 14.0, 2.0, 'https://images.unsplash.com/photo-1619040840879-3f0e23a61e3e?w=500', false, false, false, false, 4.8, 234, 2100, NOW(), NOW()),

('BBQ Ribs', 'Slow-cooked pork ribs with BBQ sauce', 'American', 'Dinner', 'Medium', 15, 180, 195, 4, 620, 42.0, 28.0, 38.0, 3.0, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=500', false, false, true, true, 4.6, 189, 2200, NOW(), NOW()),
('Grilled Salmon', 'Fresh salmon fillet with lemon and herbs', 'American', 'Dinner', 'Easy', 10, 12, 22, 2, 420, 42.0, 2.0, 24.0, 0.5, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=500', false, false, true, true, 4.8, 180, 2100, NOW(), NOW()),
('Beef Wellington', 'Beef tenderloin wrapped in puff pastry', 'American', 'Dinner', 'Hard', 40, 45, 85, 6, 720, 48.0, 35.0, 42.0, 3.0, 'https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=500', false, false, false, false, 4.9, 312, 3500, NOW(), NOW()),

('Buffalo Wings', 'Spicy chicken wings with blue cheese', 'American', 'Appetizer', 'Easy', 10, 25, 35, 4, 480, 32.0, 8.0, 34.0, 1.0, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=500', false, false, true, false, 4.6, 156, 1780, NOW(), NOW()),
('Veggie Burger', 'Plant-based burger with all the fixings', 'American', 'Lunch', 'Easy', 10, 12, 22, 4, 380, 22.0, 45.0, 14.0, 8.0, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=500', true, true, false, false, 4.1, 160, 1700, NOW(), NOW()),

-- ITALIAN RECIPES
('Frittata Italiana', 'Italian-style omelet with vegetables', 'Italian', 'Breakfast', 'Easy', 10, 15, 25, 4, 280, 16.0, 12.0, 18.0, 3.0, 'https://images.unsplash.com/photo-1608096299230-c3e5b52ecf94?w=500', true, false, true, false, 4.3, 87, 980, NOW(), NOW()),
('Breakfast Bruschetta', 'Toasted bread with tomatoes and eggs', 'Italian', 'Breakfast', 'Medium', 15, 10, 25, 2, 340, 14.0, 38.0, 14.0, 4.0, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=500', true, false, false, false, 4.4, 76, 850, NOW(), NOW()),

('Caprese Salad', 'Fresh tomatoes, mozzarella, and basil', 'Italian', 'Lunch', 'Easy', 10, 0, 10, 4, 220, 12.0, 8.0, 16.0, 2.0, 'https://images.unsplash.com/photo-1592417817098-8fd3d9eb14a5?w=500', true, false, true, false, 4.4, 125, 1450, NOW(), NOW()),
('Minestrone Soup', 'Hearty Italian vegetable soup', 'Italian', 'Lunch', 'Medium', 20, 40, 60, 6, 180, 8.0, 32.0, 4.0, 8.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', true, true, true, true, 4.5, 142, 1620, NOW(), NOW()),

('Spaghetti Carbonara', 'Classic pasta with eggs, cheese, and bacon', 'Italian', 'Dinner', 'Easy', 10, 15, 25, 4, 450, 18.5, 52.0, 20.0, 3.0, 'https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500', false, false, false, false, 4.5, 120, 1500, NOW(), NOW()),
('Margherita Pizza', 'Classic pizza with tomato, mozzarella, and basil', 'Italian', 'Dinner', 'Medium', 20, 15, 35, 4, 520, 22.0, 68.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500', true, false, false, false, 4.6, 300, 4500, NOW(), NOW()),
('Osso Buco', 'Braised veal shanks with vegetables', 'Italian', 'Dinner', 'Hard', 30, 150, 180, 4, 580, 52.0, 24.0, 28.0, 4.0, 'https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8?w=500', false, false, true, false, 4.8, 198, 2250, NOW(), NOW()),

('Bruschetta', 'Grilled bread with tomatoes and garlic', 'Italian', 'Appetizer', 'Easy', 10, 5, 15, 4, 180, 6.0, 28.0, 6.0, 3.0, 'https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?w=500', true, true, false, true, 4.3, 167, 1890, NOW(), NOW()),
('Mushroom Risotto', 'Creamy Italian rice dish with mushrooms', 'Italian', 'Dinner', 'Hard', 10, 35, 45, 4, 390, 12.0, 62.0, 12.0, 3.0, 'https://images.unsplash.com/photo-1476124369491-c2be09a48880?w=500', true, false, true, false, 4.7, 140, 1600, NOW(), NOW()),

-- CHINESE RECIPES
('Congee', 'Rice porridge with toppings', 'Chinese', 'Breakfast', 'Easy', 10, 60, 70, 4, 240, 8.0, 48.0, 3.0, 2.0, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500', true, true, true, true, 4.2, 89, 1020, NOW(), NOW()),
('Jianbing', 'Chinese savory breakfast crepe', 'Chinese', 'Breakfast', 'Medium', 15, 10, 25, 2, 380, 14.0, 52.0, 14.0, 3.0, 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=500', false, false, false, false, 4.5, 112, 1280, NOW(), NOW()),

('Hot and Sour Soup', 'Spicy and tangy Chinese soup', 'Chinese', 'Lunch', 'Easy', 15, 15, 30, 4, 120, 8.0, 14.0, 4.0, 2.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', false, false, true, true, 4.3, 98, 1120, NOW(), NOW()),
('Kung Pao Chicken', 'Spicy stir-fried chicken with peanuts', 'Chinese', 'Lunch', 'Medium', 20, 15, 35, 4, 420, 32.0, 28.0, 22.0, 4.0, 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=500', false, false, true, true, 4.6, 187, 2150, NOW(), NOW()),

('Vegetable Stir Fry', 'Colorful mix of vegetables in savory sauce', 'Chinese', 'Dinner', 'Easy', 15, 10, 25, 4, 180, 8.0, 28.0, 6.0, 6.0, 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=500', true, true, true, true, 4.3, 150, 1800, NOW(), NOW()),
('Mapo Tofu', 'Spicy Sichuan tofu with ground pork', 'Chinese', 'Dinner', 'Medium', 15, 20, 35, 4, 320, 18.0, 24.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1545048702-79362596cdc9?w=500', false, false, true, true, 4.7, 156, 1780, NOW(), NOW()),
('Peking Duck', 'Crispy roasted duck with pancakes', 'Chinese', 'Dinner', 'Hard', 30, 90, 120, 6, 680, 48.0, 32.0, 42.0, 2.0, 'https://images.unsplash.com/photo-1567621443394-8976b88ae252?w=500', false, false, false, false, 4.9, 267, 3100, NOW(), NOW()),

('Spring Rolls', 'Crispy fried vegetable rolls', 'Chinese', 'Appetizer', 'Medium', 25, 10, 35, 6, 220, 6.0, 32.0, 8.0, 3.0, 'https://images.unsplash.com/photo-1541529086526-db283c563270?w=500', true, true, false, true, 4.4, 178, 2020, NOW(), NOW()),

-- MEXICAN RECIPES
('Huevos Rancheros', 'Fried eggs on tortillas with salsa', 'Mexican', 'Breakfast', 'Easy', 10, 15, 25, 2, 380, 18.0, 38.0, 18.0, 6.0, 'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=500', true, false, true, false, 4.5, 134, 1520, NOW(), NOW()),
('Chilaquiles', 'Tortilla chips with salsa and cheese', 'Mexican', 'Breakfast', 'Medium', 15, 15, 30, 4, 420, 16.0, 45.0, 22.0, 5.0, 'https://images.unsplash.com/photo-1569288063643-5d29ad64df09?w=500', true, false, true, false, 4.6, 145, 1650, NOW(), NOW()),

('Chicken Tortilla Soup', 'Hearty soup with tortilla strips', 'Mexican', 'Lunch', 'Easy', 15, 30, 45, 6, 280, 22.0, 28.0, 10.0, 5.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', false, false, true, true, 4.4, 112, 1280, NOW(), NOW()),
('Fish Tacos', 'Grilled fish in soft tortillas with slaw', 'Mexican', 'Lunch', 'Medium', 20, 15, 35, 4, 380, 28.0, 42.0, 14.0, 6.0, 'https://images.unsplash.com/photo-1552332386-f8dd00dc2f85?w=500', false, false, false, false, 4.7, 198, 2250, NOW(), NOW()),

('Beef Tacos', 'Seasoned ground beef in soft tortillas', 'Mexican', 'Dinner', 'Easy', 15, 15, 30, 4, 480, 28.0, 42.0, 22.0, 6.0, 'https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?w=500', false, false, false, false, 4.3, 200, 2500, NOW(), NOW()),
('Chicken Enchiladas', 'Rolled tortillas with chicken and sauce', 'Mexican', 'Dinner', 'Medium', 25, 30, 55, 6, 520, 32.0, 48.0, 22.0, 6.0, 'https://images.unsplash.com/photo-1534352956036-cd81e27dd615?w=500', false, false, false, false, 4.6, 187, 2140, NOW(), NOW()),
('Mole Poblano', 'Chicken in complex chocolate chili sauce', 'Mexican', 'Dinner', 'Hard', 45, 120, 165, 6, 580, 42.0, 38.0, 32.0, 8.0, 'https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=500', false, false, true, false, 4.8, 234, 2680, NOW(), NOW()),

('Guacamole', 'Fresh avocado dip with lime and cilantro', 'Mexican', 'Appetizer', 'Easy', 10, 0, 10, 4, 180, 3.0, 12.0, 16.0, 7.0, 'https://images.unsplash.com/photo-1593759608136-45eb2ad9507d?w=500', true, true, true, true, 4.7, 289, 3200, NOW(), NOW()),

-- INDIAN RECIPES
('Masala Dosa', 'Crispy rice crepe with spiced potato filling', 'Indian', 'Breakfast', 'Medium', 20, 15, 35, 2, 350, 10.0, 58.0, 10.0, 5.0, 'https://images.unsplash.com/photo-1630383249896-424e482df921?w=500', true, true, true, true, 4.6, 178, 2020, NOW(), NOW()),
('Poha', 'Flattened rice with vegetables and spices', 'Indian', 'Breakfast', 'Easy', 10, 10, 20, 4, 280, 6.0, 52.0, 8.0, 4.0, 'https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=500', true, true, true, true, 4.3, 123, 1400, NOW(), NOW()),

('Samosa', 'Crispy pastry filled with spiced potatoes', 'Indian', 'Lunch', 'Medium', 30, 20, 50, 6, 320, 8.0, 42.0, 14.0, 4.0, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=500', true, true, false, true, 4.5, 234, 2680, NOW(), NOW()),
('Chole Bhature', 'Spicy chickpeas with fried bread', 'Indian', 'Lunch', 'Hard', 30, 40, 70, 4, 520, 18.0, 72.0, 18.0, 12.0, 'https://images.unsplash.com/photo-1626074353765-517a681e40be?w=500', true, false, false, true, 4.7, 189, 2150, NOW(), NOW()),

('Chicken Tikka Masala', 'Tender chicken in creamy spiced tomato sauce', 'Indian', 'Dinner', 'Medium', 30, 25, 55, 6, 380, 32.0, 28.0, 15.0, 4.0, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=500', false, false, true, false, 4.7, 250, 3200, NOW(), NOW()),
('Palak Paneer', 'Indian cottage cheese in spinach gravy', 'Indian', 'Dinner', 'Easy', 15, 20, 35, 4, 320, 16.0, 18.0, 22.0, 6.0, 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500', true, false, true, false, 4.6, 178, 2020, NOW(), NOW()),
('Biryani', 'Fragrant spiced rice with meat or vegetables', 'Indian', 'Dinner', 'Hard', 45, 60, 105, 6, 580, 32.0, 78.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500', false, false, true, true, 4.8, 312, 3600, NOW(), NOW()),

('Pakora', 'Deep-fried vegetable fritters', 'Indian', 'Appetizer', 'Easy', 15, 15, 30, 4, 280, 8.0, 32.0, 14.0, 4.0, 'https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=500', true, true, true, true, 4.4, 167, 1890, NOW(), NOW()),

-- JAPANESE RECIPES
('Tamagoyaki', 'Japanese rolled omelet', 'Japanese', 'Breakfast', 'Medium', 10, 10, 20, 2, 240, 14.0, 12.0, 16.0, 1.0, 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=500', true, false, true, false, 4.5, 98, 1120, NOW(), NOW()),
('Onigiri', 'Japanese rice balls with fillings', 'Japanese', 'Breakfast', 'Easy', 15, 0, 15, 4, 180, 4.0, 38.0, 2.0, 1.0, 'https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=500', true, true, true, true, 4.3, 145, 1650, NOW(), NOW()),

('Miso Soup', 'Traditional Japanese soup with tofu', 'Japanese', 'Lunch', 'Easy', 5, 10, 15, 4, 80, 6.0, 8.0, 3.0, 2.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', true, true, true, true, 4.2, 156, 1780, NOW(), NOW()),
('Sushi Rolls', 'Fresh California rolls with avocado and crab', 'Japanese', 'Lunch', 'Medium', 30, 0, 30, 2, 280, 12.0, 42.0, 8.0, 3.0, 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=500', false, false, true, true, 4.7, 210, 2400, NOW(), NOW()),

('Teriyaki Chicken', 'Grilled chicken with sweet soy glaze', 'Japanese', 'Dinner', 'Easy', 15, 20, 35, 4, 380, 32.0, 42.0, 12.0, 2.0, 'https://images.unsplash.com/photo-1599084993091-1cb5c0721cc6?w=500', false, false, true, true, 4.5, 178, 2020, NOW(), NOW()),
('Ramen', 'Japanese noodle soup with pork and egg', 'Japanese', 'Dinner', 'Medium', 30, 30, 60, 2, 520, 28.0, 68.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=500', false, false, false, false, 4.8, 289, 3300, NOW(), NOW()),
('Kaiseki', 'Traditional multi-course Japanese dinner', 'Japanese', 'Dinner', 'Hard', 120, 90, 210, 2, 680, 42.0, 58.0, 28.0, 8.0, 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=500', false, false, true, true, 4.9, 342, 3900, NOW(), NOW()),

('Edamame', 'Steamed young soybeans', 'Japanese', 'Appetizer', 'Easy', 5, 5, 10, 4, 120, 12.0, 10.0, 5.0, 5.0, 'https://images.unsplash.com/photo-1583196872184-c5235ae8f3e7?w=500', true, true, true, true, 4.3, 178, 2020, NOW(), NOW()),

-- THAI RECIPES
('Thai Omelette', 'Fluffy Thai-style egg omelette', 'Thai', 'Breakfast', 'Easy', 5, 10, 15, 2, 280, 16.0, 8.0, 20.0, 1.0, 'https://images.unsplash.com/photo-1593252719532-b5f0c2e3077a?w=500', true, false, true, false, 4.2, 87, 990, NOW(), NOW()),
('Khao Tom', 'Thai rice soup with toppings', 'Thai', 'Breakfast', 'Medium', 15, 20, 35, 4, 320, 14.0, 52.0, 8.0, 3.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', false, false, true, true, 4.4, 112, 1280, NOW(), NOW()),

('Tom Yum Soup', 'Spicy and sour Thai soup with shrimp', 'Thai', 'Lunch', 'Medium', 15, 20, 35, 4, 180, 18.0, 12.0, 6.0, 2.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', false, false, true, true, 4.4, 130, 1500, NOW(), NOW()),
('Som Tam', 'Spicy green papaya salad', 'Thai', 'Lunch', 'Easy', 20, 0, 20, 4, 180, 6.0, 32.0, 4.0, 8.0, 'https://images.unsplash.com/photo-1559847844-5315695dadae?w=500', true, true, true, true, 4.5, 167, 1890, NOW(), NOW()),

('Pad Thai', 'Stir-fried rice noodles with peanuts', 'Thai', 'Dinner', 'Medium', 20, 15, 35, 4, 450, 16.0, 58.0, 18.0, 3.5, 'https://images.unsplash.com/photo-1559314809-0d155014e29e?w=500', false, false, true, true, 4.5, 220, 2800, NOW(), NOW()),
('Green Curry', 'Thai curry with coconut milk and vegetables', 'Thai', 'Dinner', 'Easy', 15, 20, 35, 4, 380, 18.0, 32.0, 22.0, 5.0, 'https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=500', false, false, true, true, 4.6, 198, 2250, NOW(), NOW()),
('Massaman Curry', 'Rich Thai curry with peanuts and potatoes', 'Thai', 'Dinner', 'Hard', 30, 90, 120, 6, 520, 28.0, 48.0, 28.0, 6.0, 'https://images.unsplash.com/photo-1604908815715-d2a587186545?w=500', false, false, true, true, 4.8, 234, 2680, NOW(), NOW()),

('Thai Spring Rolls', 'Fresh rice paper rolls with vegetables', 'Thai', 'Appetizer', 'Easy', 20, 0, 20, 4, 160, 6.0, 28.0, 4.0, 4.0, 'https://images.unsplash.com/photo-1559847844-5315695dadae?w=500', true, true, true, true, 4.4, 178, 2020, NOW(), NOW()),

-- FRENCH RECIPES
('French Toast', 'Classic breakfast with cinnamon and maple syrup', 'French', 'Breakfast', 'Easy', 5, 10, 15, 2, 350, 12.0, 48.0, 12.0, 2.0, 'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=500', true, false, false, false, 4.2, 110, 1300, NOW(), NOW()),
('Croissant', 'Buttery flaky French pastry', 'French', 'Breakfast', 'Hard', 240, 20, 260, 8, 420, 8.0, 46.0, 22.0, 2.0, 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=500', true, false, false, false, 4.7, 289, 3300, NOW(), NOW()),

('French Onion Soup', 'Rich soup with caramelized onions and cheese', 'French', 'Lunch', 'Medium', 15, 60, 75, 4, 380, 16.0, 38.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', true, false, false, false, 4.6, 187, 2140, NOW(), NOW()),
('Niçoise Salad', 'French salad with tuna, eggs, and olives', 'French', 'Lunch', 'Easy', 20, 10, 30, 4, 420, 32.0, 18.0, 26.0, 6.0, 'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=500', false, false, true, false, 4.5, 156, 1780, NOW(), NOW()),

('Coq au Vin', 'Chicken braised in red wine', 'French', 'Dinner', 'Hard', 30, 90, 120, 4, 520, 42.0, 18.0, 28.0, 3.0, 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=500', false, false, true, false, 4.8, 234, 2680, NOW(), NOW()),
('Ratatouille', 'Provençal vegetable stew', 'French', 'Dinner', 'Medium', 25, 45, 70, 6, 180, 6.0, 28.0, 8.0, 8.0, 'https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?w=500', true, true, true, true, 4.5, 178, 2020, NOW(), NOW()),
('Steak Frites', 'Grilled steak with French fries', 'French', 'Dinner', 'Easy', 10, 15, 25, 2, 680, 48.0, 52.0, 32.0, 4.0, 'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=500', false, false, true, true, 4.7, 289, 3300, NOW(), NOW()),

('Escargots', 'Snails in garlic butter', 'French', 'Appetizer', 'Medium', 15, 15, 30, 4, 280, 18.0, 8.0, 20.0, 1.0, 'https://images.unsplash.com/photo-1559847844-5315695dadae?w=500', false, false, true, false, 4.6, 167, 1890, NOW(), NOW()),

-- GREEK RECIPES
('Greek Yogurt Bowl', 'Yogurt with honey, nuts, and fruit', 'Greek', 'Breakfast', 'Easy', 5, 0, 5, 2, 320, 18.0, 38.0, 12.0, 4.0, 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500', true, false, true, false, 4.4, 167, 1890, NOW(), NOW()),
('Spanakopita', 'Greek spinach and feta pie', 'Greek', 'Breakfast', 'Medium', 20, 40, 60, 6, 380, 16.0, 32.0, 22.0, 4.0, 'https://images.unsplash.com/photo-1601000938259-9e92002320b2?w=500', true, false, false, false, 4.6, 145, 1650, NOW(), NOW()),

('Greek Salad', 'Fresh vegetables with feta cheese and olives', 'Greek', 'Lunch', 'Easy', 15, 0, 15, 4, 220, 8.0, 12.0, 16.0, 4.0, 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=500', true, false, true, false, 4.4, 95, 1100, NOW(), NOW()),
('Gyro', 'Greek meat wrap with tzatziki sauce', 'Greek', 'Lunch', 'Medium', 20, 15, 35, 4, 480, 32.0, 42.0, 20.0, 4.0, 'https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=500', false, false, false, false, 4.7, 234, 2680, NOW(), NOW()),

('Moussaka', 'Layered eggplant and meat casserole', 'Greek', 'Dinner', 'Hard', 40, 90, 130, 6, 520, 32.0, 38.0, 28.0, 6.0, 'https://images.unsplash.com/photo-1601000938259-9e92002320b2?w=500', false, false, true, false, 4.8, 289, 3300, NOW(), NOW()),
('Souvlaki', 'Greek grilled meat skewers', 'Greek', 'Dinner', 'Easy', 15, 15, 30, 4, 420, 38.0, 18.0, 24.0, 2.0, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=500', false, false, true, true, 4.6, 198, 2250, NOW(), NOW()),
('Lamb Kleftiko', 'Slow-roasted lamb with vegetables', 'Greek', 'Dinner', 'Medium', 30, 180, 210, 6, 620, 52.0, 24.0, 38.0, 4.0, 'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=500', false, false, true, true, 4.7, 234, 2680, NOW(), NOW()),

('Dolmades', 'Stuffed grape leaves', 'Greek', 'Appetizer', 'Medium', 40, 45, 85, 6, 280, 8.0, 38.0, 12.0, 4.0, 'https://images.unsplash.com/photo-1601000938259-9e92002320b2?w=500', true, false, true, true, 4.5, 178, 2020, NOW(), NOW()),

-- KOREAN RECIPES
('Korean Egg Roll', 'Rolled omelette with vegetables', 'Korean', 'Breakfast', 'Easy', 10, 10, 20, 2, 240, 14.0, 18.0, 14.0, 2.0, 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=500', true, false, true, false, 4.3, 98, 1120, NOW(), NOW()),
('Korean Pancake', 'Savory pancake with vegetables', 'Korean', 'Breakfast', 'Medium', 15, 15, 30, 4, 320, 12.0, 42.0, 12.0, 3.0, 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=500', true, false, false, true, 4.5, 134, 1520, NOW(), NOW()),

('Kimchi Jjigae', 'Spicy Korean kimchi stew', 'Korean', 'Lunch', 'Easy', 10, 25, 35, 4, 280, 18.0, 24.0, 12.0, 4.0, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500', false, false, true, true, 4.6, 178, 2020, NOW(), NOW()),
('Kimbap', 'Korean seaweed rice rolls', 'Korean', 'Lunch', 'Medium', 30, 0, 30, 4, 380, 14.0, 58.0, 10.0, 4.0, 'https://images.unsplash.com/photo-1562967914-608f82629710?w=500', false, false, true, true, 4.4, 156, 1780, NOW(), NOW()),

('Bibimbap', 'Korean rice bowl with vegetables and egg', 'Korean', 'Dinner', 'Medium', 25, 15, 40, 2, 520, 22.0, 68.0, 18.0, 8.0, 'https://images.unsplash.com/photo-1553163147-622ab57be1c7?w=500', false, false, true, true, 4.6, 185, 2100, NOW(), NOW()),
('Korean BBQ', 'Grilled marinated beef with sides', 'Korean', 'Dinner', 'Easy', 15, 15, 30, 4, 580, 42.0, 28.0, 32.0, 3.0, 'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=500', false, false, true, true, 4.7, 234, 2680, NOW(), NOW()),
('Galbi Jjim', 'Braised Korean short ribs', 'Korean', 'Dinner', 'Hard', 30, 120, 150, 4, 620, 48.0, 42.0, 32.0, 4.0, 'https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=500', false, false, true, true, 4.8, 267, 3100, NOW(), NOW()),

('Korean Fried Chicken', 'Crispy chicken with sweet and spicy glaze', 'Korean', 'Appetizer', 'Medium', 20, 25, 45, 4, 520, 32.0, 42.0, 24.0, 2.0, 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=500', false, false, false, false, 4.7, 289, 3300, NOW(), NOW()),

-- MEDITERRANEAN RECIPES
('Shakshuka', 'Eggs poached in tomato sauce', 'Mediterranean', 'Breakfast', 'Easy', 10, 20, 30, 4, 280, 16.0, 18.0, 16.0, 5.0, 'https://images.unsplash.com/photo-1587314168485-3236d6710814?w=500', true, false, true, false, 4.5, 178, 2020, NOW(), NOW()),
('Mediterranean Omelette', 'Egg omelette with feta and vegetables', 'Mediterranean', 'Breakfast', 'Medium', 10, 10, 20, 2, 320, 18.0, 12.0, 22.0, 3.0, 'https://images.unsplash.com/photo-1608096299230-c3e5b52ecf94?w=500', true, false, true, false, 4.4, 134, 1520, NOW(), NOW()),

('Buddha Bowl', 'Healthy bowl with quinoa and roasted vegetables', 'Mediterranean', 'Lunch', 'Easy', 20, 25, 45, 2, 420, 14.0, 52.0, 18.0, 12.0, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500', true, true, true, true, 4.5, 175, 1900, NOW(), NOW()),
('Falafel Wrap', 'Chickpea fritters in pita with tahini', 'Mediterranean', 'Lunch', 'Medium', 20, 15, 35, 4, 420, 16.0, 52.0, 18.0, 10.0, 'https://images.unsplash.com/photo-1529042410759-befb1204b468?w=500', true, true, false, true, 4.6, 198, 2250, NOW(), NOW()),

('Grilled Sea Bass', 'Mediterranean-style grilled fish', 'Mediterranean', 'Dinner', 'Easy', 15, 15, 30, 2, 380, 42.0, 8.0, 18.0, 2.0, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=500', false, false, true, true, 4.7, 234, 2680, NOW(), NOW()),
('Paella', 'Spanish rice dish with seafood', 'Mediterranean', 'Dinner', 'Hard', 30, 45, 75, 6, 580, 38.0, 68.0, 18.0, 4.0, 'https://images.unsplash.com/photo-1534080564583-6be75777b70a?w=500', false, false, true, true, 4.8, 289, 3300, NOW(), NOW()),
('Lamb Tagine', 'Moroccan slow-cooked lamb stew', 'Mediterranean', 'Dinner', 'Medium', 25, 120, 145, 6, 520, 42.0, 38.0, 24.0, 6.0, 'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=500', false, false, true, true, 4.7, 234, 2680, NOW(), NOW()),

('Hummus', 'Creamy chickpea dip', 'Mediterranean', 'Appetizer', 'Easy', 10, 0, 10, 6, 180, 8.0, 18.0, 10.0, 6.0, 'https://images.unsplash.com/photo-1571991187428-6d3c9b26d618?w=500', true, true, true, true, 4.6, 267, 3100, NOW(), NOW());

-- Verify the inserts
SELECT COUNT(*) as 'Total Recipes' FROM recipes;
SELECT CONCAT('Cuisine: ', cuisine_type, ' | Meal: ', meal_type, ' | Difficulty: ', difficulty_level, ' => Count: ', COUNT(*)) as 'Recipe Distribution'
FROM recipes 
GROUP BY cuisine_type, meal_type, difficulty_level 
ORDER BY cuisine_type, meal_type, difficulty_level;
