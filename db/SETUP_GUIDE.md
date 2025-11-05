# CookMate Database - Complete Setup & Implementation Guide

## 🎯 Executive Summary

This is the **authoritative, production-ready database schema** for CookMate, generated from a comprehensive analysis of:
- **18 JPA Entity classes** from Spring Boot backend
- **All frontend React components** and API calls
- **Complete relationship mapping** with proper foreign keys and constraints

### What's Included
- ✅ Complete MySQL 8.0 schema with all 18 tables
- ✅ All foreign key relationships and constraints
- ✅ Performance-optimized indexes (60+)
- ✅ Seed data for lookup tables (36 records)
- ✅ Full cascade delete/update rules
- ✅ Complete documentation

---

## 📊 Schema Overview

### Database Statistics
- **Total Tables:** 18
- **Foreign Keys:** 20+
- **Unique Constraints:** 12+
- **Indexes:** 60+ (including composite and fulltext)
- **Seed Records:** 36 (dietary restrictions, cuisines, meal types)
- **Character Set:** UTF-8 (utf8mb4) - full Unicode support

### Table Categories

#### 1. User Management (3 tables)
- `users` - Core user authentication and profile
- `user_preferences` - User cooking preferences (1:1 with users)
- `password_reset_tokens` - Password recovery tokens

#### 2. Recipe Management (7 tables)
- `recipes` - Core recipe data with nutritional info
- `instructions` - Step-by-step cooking instructions
- `ingredients` - Master ingredient list
- `recipe_ingredients` - Recipe-ingredient junction with quantities
- `reviews` - User reviews and ratings
- `favorites` - User favorite recipes
- `recently_viewed` - Recipe view tracking

#### 3. Meal Planning (2 tables)
- `meal_plans` - User meal plan containers
- `meal_plan_recipes` - Scheduled recipes in meal plans

#### 4. Shopping Lists (2 tables)
- `shopping_lists` - Shopping list containers
- `shopping_list_items` - Individual shopping items

#### 5. Lookup/Reference (4 tables)
- `dietary_restrictions` - Master dietary restriction list
- `user_dietary_restrictions` - User dietary preferences (junction)
- `cuisine_types` - Cuisine categories (reference data)
- `meal_types` - Meal type categories (reference data)

---

## 🗂️ File Structure

```
/Users/apple/Documents/GitHub/cookmate-backend/db/
│
├── 📄 cookmate_complete_schema.sql          ← PRIMARY SCHEMA FILE (RUN THIS!)
│   • Complete database creation script
│   • All 18 tables with relationships
│   • All indexes and constraints
│   • Seed data for lookup tables
│   • Production-ready
│
├── 📄 SCHEMA_DOCUMENTATION.md               ← COMPLETE TABLE REFERENCE
│   • Detailed documentation for all 18 tables
│   • Column specifications and data types
│   • Relationship mappings
│   • Business rules and constraints
│   • Index strategies
│
├── 📄 ER_DIAGRAM.md                         ← VISUAL RELATIONSHIPS
│   • ASCII ER diagrams for all tables
│   • Relationship visualization
│   • Design decision rationale
│   • Database statistics
│
├── 📄 SETUP_GUIDE.md                        ← THIS FILE
│   • Installation instructions
│   • Configuration steps
│   • Troubleshooting guide
│
├── 📄 update_database_safe.sql              ← INCREMENTAL UPDATE
│   • Safe updates for existing databases
│   • Adds new columns without data loss
│
└── 📄 MIGRATION_GUIDE.md                    ← Migration strategies
    • Update existing installations
    • Data migration scripts
```

---

## 🚀 Installation Steps

### Prerequisites
```bash
# Check installations
mysql --version        # Need MySQL 8.0+
java -version          # Need Java 19
node --version         # Need Node 18+
```

### Step 1: Install Fresh Database
```bash
# Start MySQL
brew services start mysql  # macOS
# or: mysql.server start

# Run complete schema
cd /Users/apple/Documents/GitHub/cookmate-backend/db
mysql -u root -p < cookmate_complete_schema.sql

# Verify (should show 18 tables)
mysql -u root -p cookmate -e "SHOW TABLES;"
```

### Step 2: Configure Backend
Edit `/Users/apple/Documents/GitHub/cookmate-backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/cookmate
spring.datasource.username=root
spring.datasource.password=your_password

# CRITICAL: Use 'none' to prevent Hibernate from modifying schema
spring.jpa.hibernate.ddl-auto=none

# Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Step 3: Start Backend
```bash
cd /Users/apple/Documents/GitHub/cookmate-backend
./mvnw clean package
./mvnw spring-boot:run
```

Expected output:
```
Hikari Pool-1 - Start completed.
Started CookmateBackendApplication in X.XXX seconds
```

### Step 4: Start Frontend
```bash
cd /Users/apple/Documents/GitHub/cookmate-frontend
npm install  # First time only
npm run dev
```

Access at: http://localhost:3000

---

## 🔄 Update Existing Database

### Option 1: Safe Update (Recommended)
For databases with existing data:
```bash
# Backup first!
mysqldump -u root -p cookmate > backup_$(date +%Y%m%d).sql

# Apply safe updates
mysql -u root -p cookmate < update_database_safe.sql
```

### Option 2: Complete Rebuild
⚠️ **WARNING: Deletes all data!**
```bash
# Backup
mysqldump -u root -p cookmate > backup_before_rebuild.sql

# Drop and recreate
mysql -u root -p -e "DROP DATABASE cookmate;"
mysql -u root -p < cookmate_complete_schema.sql
```

---

## 🔍 Verification

### Check Schema Completeness
```sql
USE cookmate;

-- Should return 18
SELECT COUNT(*) FROM information_schema.tables 
WHERE table_schema = 'cookmate';

-- Should return 20+
SELECT COUNT(*) FROM information_schema.table_constraints 
WHERE constraint_schema = 'cookmate' 
AND constraint_type = 'FOREIGN KEY';
```

### Check Seed Data
```sql
SELECT COUNT(*) FROM dietary_restrictions;  -- Should be 13
SELECT COUNT(*) FROM cuisine_types;         -- Should be 15
SELECT COUNT(*) FROM meal_types;            -- Should be 8
SELECT COUNT(*) FROM ingredients;           -- Should be 25
```

### Test Backend Connection
```bash
# Check backend logs for:
# - HikariCP connection pool started
# - Hibernate initialization
# - No schema validation errors

tail -f /path/to/backend/logs/spring-boot.log
```

Expected in logs:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started CookmateBackendApplication
```

---

## 📋 Key Relationships Explained

### 1. Users → User Preferences (1:1)
```sql
-- Each user has exactly one preferences record
users.id (1) ←→ (1) user_preferences.user_id
```

### 2. Users ↔ Recipes (N:M via Favorites)
```sql
-- Users can favorite multiple recipes
users.id (1) ──→ (N) favorites.user_id
recipes.id (1) ──→ (N) favorites.recipe_id
-- UNIQUE constraint prevents duplicate favorites
```

### 3. Recipes → Instructions (1:N)
```sql
-- Each recipe has multiple ordered steps
recipes.id (1) ──→ (N) instructions.recipe_id
-- Ordered by instructions.step_number
```

### 4. Recipes ↔ Ingredients (N:M via recipe_ingredients)
```sql
-- Recipes use multiple ingredients, ingredients in multiple recipes
recipes.id (1) ──→ (N) recipe_ingredients.recipe_id
ingredients.id (1) ──→ (N) recipe_ingredients.ingredient_id
-- Includes quantity, unit, notes
```

### 5. Meal Plans → Recipes (1:N:M)
```sql
-- Meal plans contain scheduled recipes
meal_plans.id (1) ──→ (N) meal_plan_recipes.meal_plan_id
recipes.id (1) ──→ (N) meal_plan_recipes.recipe_id
-- Includes planned_date, meal_time, servings
```

### 6. Dietary Restrictions (N:M)
```sql
-- Users can have multiple dietary restrictions
users.id (1) ──→ (N) user_dietary_restrictions.user_id
dietary_restrictions.id (1) ──→ (N) user_dietary_restrictions.dietary_restriction_id
-- UNIQUE constraint prevents duplicates
```

---

## 🎨 Special Design Features

### 1. String-Based Cuisine/Meal Types
**Design:** `recipes.cuisine_type` and `recipes.meal_type` are VARCHAR, not foreign keys.

**Rationale:**
- Allows custom entries not in lookup tables
- No ALTER TABLE needed for new types
- Lookup tables (`cuisine_types`, `meal_types`) provide UI dropdowns
- Recipes won't break if a type is deleted

### 2. Comma-Separated Preferences
**Design:** `user_preferences.cuisine_preferences` and `meal_types` store comma-separated strings.

**Rationale:**
- Simpler data model (no junction table needed)
- Easy to serialize/deserialize in Java
- Sufficient for simple multi-select preferences
- Frontend handles splitting/joining

### 3. Optional Ingredient Reference
**Design:** `shopping_list_items.ingredient_id` is optional (nullable FK).

**Rationale:**
- Users can add custom items not in master `ingredients` table
- `ingredient_name` is always populated (user-facing)
- `ingredient_id` provides link when available
- Flexible for manual list creation

### 4. Cascade Delete Strategy
**User Deletion:**
- Cascades to: preferences, favorites, reviews, meal plans, shopping lists, dietary restrictions
- Recipe creator: SET NULL (recipes remain even if creator is deleted)

**Recipe Deletion:**
- Cascades to: instructions, recipe_ingredients, reviews, favorites

**Meal Plan Deletion:**
- Cascades to: meal_plan_recipes

**Shopping List Deletion:**
- Cascades to: shopping_list_items

---

## 🔐 Security Considerations

### Create Application User
Don't use root for the application!

```sql
-- Create dedicated user
CREATE USER 'cookmate_app'@'localhost' IDENTIFIED BY 'secure_password';

-- Grant privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON cookmate.* TO 'cookmate_app'@'localhost';
FLUSH PRIVILEGES;
```

Update `application.properties`:
```properties
spring.datasource.username=cookmate_app
spring.datasource.password=secure_password
```

### Password Security
- User passwords stored with BCrypt encryption (handled by Spring Security)
- Password field is VARCHAR(255) to accommodate BCrypt hashes
- `@JsonIgnore` in User entity prevents password serialization

---

## 📈 Performance Optimization

### Key Indexes

#### Recipe Search Performance
```sql
-- Title and description search
FULLTEXT INDEX idx_recipes_search (title, description)

-- Dietary filtering
INDEX idx_recipes_dietary (is_vegetarian, is_vegan, is_gluten_free, is_dairy_free)

-- Rating/popularity sorting
INDEX idx_recipes_rating_views (average_rating DESC, view_count DESC)
```

#### User Queries
```sql
-- Favorites lookup
INDEX idx_favorites_user_id (user_id)
UNIQUE KEY uk_favorites_user_recipe (user_id, recipe_id)

-- Recently viewed
INDEX idx_recently_viewed_user_viewed (user_id, viewed_at DESC)

-- Reviews by recipe
INDEX idx_reviews_recipe_id (recipe_id)
```

#### Meal Planning
```sql
-- Date range queries
INDEX idx_meal_plans_dates (user_id, start_date, end_date)
INDEX idx_meal_plan_recipes_schedule (meal_plan_id, planned_date, meal_time)
```

### Query Tips
1. **Recipe Search:** Use FULLTEXT MATCH for title/description searches
2. **User Favorites:** Always filter by user_id first (indexed)
3. **Dietary Filters:** Use composite index on dietary flags
4. **Date Ranges:** Use BETWEEN on indexed date columns

---

## 🧪 Testing Queries

### Test User Registration
```sql
-- Insert test user
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES ('testuser', 'test@example.com', '$2a$10$...', 'Test', 'User', 'USER');

-- Create preferences for user
INSERT INTO user_preferences (user_id, cooking_skill_level, household_size)
VALUES (LAST_INSERT_ID(), 'INTERMEDIATE', 2);
```

### Test Recipe Creation
```sql
-- Insert test recipe
INSERT INTO recipes (title, description, cuisine_type, meal_type, prep_time, cook_time, servings, created_by)
VALUES ('Test Recipe', 'A delicious test', 'Italian', 'Dinner', 15, 30, 4, 1);

-- Add instructions
INSERT INTO instructions (recipe_id, step_number, instruction)
VALUES 
    (LAST_INSERT_ID(), 1, 'First step'),
    (LAST_INSERT_ID(), 2, 'Second step');
```

### Test Favorites
```sql
-- Add to favorites
INSERT INTO favorites (user_id, recipe_id)
VALUES (1, 1);

-- Query user's favorites
SELECT r.* 
FROM recipes r
JOIN favorites f ON r.id = f.recipe_id
WHERE f.user_id = 1
ORDER BY f.created_at DESC;
```

---

## 🛠️ Troubleshooting

### Issue: Backend won't connect to database
**Check:**
```properties
# Correct URL format?
spring.datasource.url=jdbc:mysql://localhost:3306/cookmate

# Correct credentials?
spring.datasource.username=root
spring.datasource.password=your_actual_password

# Database exists?
mysql -u root -p -e "SHOW DATABASES LIKE 'cookmate';"
```

### Issue: Hibernate validation errors
**Solution:**
```properties
# MUST be set to 'none' to use our schema
spring.jpa.hibernate.ddl-auto=none
```

### Issue: "Duplicate entry" on favorites
**Solution:**
```sql
-- Unique constraint should prevent this
-- If error occurs, check constraint exists:
SHOW INDEX FROM favorites WHERE Key_name = 'uk_favorites_user_recipe';

-- Add if missing:
ALTER TABLE favorites 
ADD UNIQUE KEY uk_favorites_user_recipe (user_id, recipe_id);
```

### Issue: Foreign key constraint fails
**Check:**
```sql
-- View all foreign keys
SELECT 
    constraint_name, table_name, column_name,
    referenced_table_name, referenced_column_name
FROM information_schema.key_column_usage
WHERE constraint_schema = 'cookmate'
  AND referenced_table_name IS NOT NULL;
```

---

## 📦 Backup & Restore

### Regular Backups
```bash
# Daily backup (add to cron)
mysqldump -u root -p cookmate | gzip > ~/backups/cookmate_$(date +%Y%m%d).sql.gz

# Backup without data (schema only)
mysqldump -u root -p --no-data cookmate > cookmate_schema_only.sql
```

### Restore from Backup
```bash
# From compressed backup
gunzip < cookmate_20240101.sql.gz | mysql -u root -p cookmate

# From regular backup
mysql -u root -p cookmate < cookmate_20240101.sql
```

---

## ✅ Success Checklist

- [ ] MySQL 8.0+ installed and running
- [ ] Database `cookmate` created with all 18 tables
- [ ] Seed data populated (verify counts)
- [ ] All foreign keys created (verify with query)
- [ ] Backend `application.properties` configured correctly
- [ ] Backend starts without errors (check for HikariCP messages)
- [ ] Frontend connects to backend
- [ ] Can register new user
- [ ] User preferences saved correctly
- [ ] Can create recipe
- [ ] Can favorite recipe
- [ ] Can create meal plan
- [ ] Can generate shopping list

---

## 📚 Documentation Reference

| Document | Purpose | When to Read |
|----------|---------|--------------|
| **cookmate_complete_schema.sql** | Primary schema file | Before installation |
| **SCHEMA_DOCUMENTATION.md** | Complete table reference | Understanding structure |
| **ER_DIAGRAM.md** | Visual relationships | Understanding relationships |
| **SETUP_GUIDE.md** | This document | Installation and setup |
| **MIGRATION_GUIDE.md** | Update strategies | Updating existing DB |

---

## 🎯 Quick Commands Reference

```bash
# Fresh install
mysql -u root -p < cookmate_complete_schema.sql

# Verify installation
mysql -u root -p cookmate -e "SHOW TABLES; SELECT COUNT(*) FROM dietary_restrictions;"

# Start backend
cd /path/to/cookmate-backend && ./mvnw spring-boot:run

# Start frontend
cd /path/to/cookmate-frontend && npm run dev

# Backup database
mysqldump -u root -p cookmate > backup_$(date +%Y%m%d).sql

# Check backend logs
tail -f /path/to/backend/logs/spring-boot.log
```

---

## 🆘 Support

### Common Errors and Solutions

1. **"Table doesn't exist"** → Run complete schema script
2. **"Column not found"** → Run update_database_safe.sql
3. **"Access denied"** → Check MySQL credentials
4. **"Connection refused"** → Check MySQL is running
5. **"Duplicate entry"** → Check unique constraints

### Validation SQL
```sql
-- Comprehensive health check
USE cookmate;

SELECT 
    'Tables' AS check_type, 
    COUNT(*) AS count, 
    18 AS expected,
    CASE WHEN COUNT(*) = 18 THEN '✅ PASS' ELSE '❌ FAIL' END AS status
FROM information_schema.tables WHERE table_schema = 'cookmate'

UNION ALL

SELECT 
    'Foreign Keys', 
    COUNT(*), 
    20, 
    CASE WHEN COUNT(*) >= 20 THEN '✅ PASS' ELSE '❌ FAIL' END
FROM information_schema.table_constraints 
WHERE constraint_schema = 'cookmate' AND constraint_type = 'FOREIGN KEY'

UNION ALL

SELECT 
    'Dietary Restrictions', 
    COUNT(*), 
    13, 
    CASE WHEN COUNT(*) = 13 THEN '✅ PASS' ELSE '❌ FAIL' END
FROM dietary_restrictions

UNION ALL

SELECT 
    'Cuisine Types', 
    COUNT(*), 
    15, 
    CASE WHEN COUNT(*) = 15 THEN '✅ PASS' ELSE '❌ FAIL' END
FROM cuisine_types

UNION ALL

SELECT 
    'Meal Types', 
    COUNT(*), 
    8, 
    CASE WHEN COUNT(*) = 8 THEN '✅ PASS' ELSE '❌ FAIL' END
FROM meal_types;
```

---

**Schema Version:** 1.0 (Complete Ground-Up Rebuild)  
**Generated From:** Complete analysis of 18 JPA entities + frontend  
**Compatible With:** Spring Boot 3.1.5, Hibernate 6.2.13, MySQL 8.0, React 18  
**Last Updated:** Based on current backend codebase analysis  

**Ready to deploy!** 🚀
