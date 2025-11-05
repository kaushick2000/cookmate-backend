# Cookmate Database Migration Guide

**Date:** November 4, 2025  
**Target Database:** `cookmate` (matching `application.properties`)

## Overview

This guide helps you migrate from your original schema to the aligned schema that matches the current frontend and backend code.

## Key Differences Between Original and Aligned Schema

### ✅ What's CORRECT and Unchanged
- All table names match JPA entities exactly
- Column names, types, and constraints align perfectly
- Foreign key relationships are preserved
- Indices are appropriate for query patterns

### 🔧 What's Been Fixed

1. **Database Name**
   - **Original:** `DROP DATABASE IF EXISTS cookmate;` (then recreates it)
   - **Aligned:** Same - uses `cookmate` (matches `spring.datasource.url=jdbc:mysql://localhost:3306/cookmate`)
   - ✅ **Action:** Ensure your `application.properties` points to `cookmate`, not `cookmate_db`

2. **user_dietary_restrictions**
   - **Original:** Missing UNIQUE constraint on `(user_id, dietary_restriction_id)`
   - **Aligned:** Added `CONSTRAINT uq_udr UNIQUE (user_id, dietary_restriction_id)`
   - ✅ **Benefit:** Prevents duplicate dietary restriction entries per user

3. **Foreign Key Naming**
   - **Original:** Unnamed foreign keys (e.g., `FOREIGN KEY (user_id) REFERENCES users(id)`)
   - **Aligned:** Named constraints for easier management (e.g., `CONSTRAINT fk_user_preferences_user FOREIGN KEY...`)
   - ✅ **Benefit:** Better error messages and easier schema modifications

4. **favorites Table**
   - **Original:** Named unique key as `unique_favorite`
   - **Aligned:** Named as `uq_favorite` for consistency
   - ✅ **Benefit:** Consistent naming convention across all tables

## Current Backend Behavior vs Schema

### UserPreferences Persistence
**Current Code Behavior:**
```java
// AuthService.updateProfile() maps:
- DTO.cookingSkillLevel → Entity.cookingSkillLevel (ENUM stored as VARCHAR)
- DTO.dietaryRestrictions → Entity.foodAllergies (appended string, NOT normalized table)
- DTO.preferredPrepTime → Entity.preferredPrepTime
- DTO.preferredCookTime → Entity.preferredCookTime
- DTO.householdSize → Entity.householdSize
- DTO.budgetPreference → Entity.budgetPreference (ENUM stored as VARCHAR)
- DTO.healthGoals → Entity.healthGoals
- DTO.foodAllergies → Entity.foodAllergies (appended)
- DTO.cookingEquipment → Entity.cookingEquipment
- DTO.mealPlanningFrequency → Entity.mealPlanningFrequency (ENUM stored as VARCHAR)
```

**Schema Tables:**
- ✅ `user_preferences` - **USED** (stores all above fields)
- ⚠️  `dietary_restrictions` - **NOT CURRENTLY USED** by code (lookup table exists but not populated)
- ⚠️  `user_dietary_restrictions` - **NOT CURRENTLY USED** by code (junction table exists but not populated)

**Note:** The normalized `dietary_restrictions` and `user_dietary_restrictions` tables are available for future enhancement if you want proper many-to-many relationships instead of storing dietary restrictions as comma-separated strings in `food_allergies`.

### Frontend Profile.jsx Sends:
```javascript
preferences: {
  dietaryRestrictions: [],      // Array of strings → stored in user_preferences.food_allergies
  cuisinePreferences: [],       // Array of strings → NOT persisted (no column yet)
  difficultyLevel: 'medium',    // String → stored in user_preferences.cooking_skill_level
  mealTypes: []                 // Array of strings → NOT persisted (no column yet)
}
```

**Missing Backend Persistence:**
- ❌ `cuisinePreferences` - Frontend sends, but no corresponding column in `user_preferences`
- ❌ `mealTypes` - Frontend sends, but no corresponding column in `user_preferences`

## Migration Steps

### Option 1: Fresh Database (Recommended for Development)

⚠️ **WARNING: This will DELETE all existing data!**

```bash
# 1. Open MySQL client
mysql -u root -p

# 2. Run the aligned schema
source /Users/apple/Documents/Cookmate-Repo/cookmate-backend/db/cookmate_schema_aligned.sql

# 3. Verify
USE cookmate;
SHOW TABLES;
DESCRIBE user_preferences;

# 4. Exit
exit
```

### Option 2: Update Existing Database (Preserves Data)

```sql
-- Connect to your database
USE cookmate;

-- Add missing UNIQUE constraint to user_dietary_restrictions
ALTER TABLE user_dietary_restrictions 
ADD CONSTRAINT uq_udr UNIQUE (user_id, dietary_restriction_id);

-- Add missing UNIQUE constraint to favorites (if not exists)
ALTER TABLE favorites 
ADD CONSTRAINT uq_favorite UNIQUE (user_id, recipe_id);

-- Verify changes
SHOW CREATE TABLE user_dietary_restrictions;
SHOW CREATE TABLE favorites;
```

### Option 3: Schema Comparison Tool

```bash
# Use mysqldiff or similar tool
mysqldiff --server1=root:password@localhost:3306 \
  cookmate:cookmate_aligned

# Or use a GUI tool like MySQL Workbench's "Compare Schemas" feature
```

## Post-Migration Verification

### 1. Check Database Configuration
```bash
# Verify application.properties
grep "spring.datasource.url" /Users/apple/Documents/Cookmate-Repo/cookmate-backend/src/main/resources/application.properties

# Should output: spring.datasource.url=jdbc:mysql://localhost:3306/cookmate
```

### 2. Test Backend Startup
```bash
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend
java -jar target/cookmate-backend-0.0.1-SNAPSHOT.jar

# Watch for:
# ✅ Hibernate: Successful connection to cookmate
# ✅ No schema errors
# ⚠️  Warnings about MySQL8Dialect (non-critical)
```

### 3. Test Profile Update Flow
```bash
# Start frontend
cd /Users/apple/Documents/Cookmate-Repo/cookmate-frontend
npm run dev

# 1. Login
# 2. Go to Profile
# 3. Edit profile and save
# 4. Verify in MySQL:

mysql -u root -p
USE cookmate;
SELECT * FROM user_preferences WHERE user_id = 1;
```

## Known Issues and Recommendations

### Issue 1: Cuisine Preferences and Meal Types Not Persisted

**Problem:** Frontend sends `cuisinePreferences` and `mealTypes` arrays, but backend doesn't save them.

**Solution Options:**

#### A. Add Columns to user_preferences (Simple)
```sql
ALTER TABLE user_preferences 
ADD COLUMN cuisine_preferences VARCHAR(1000) AFTER food_allergies,
ADD COLUMN meal_types VARCHAR(500) AFTER cuisine_preferences;
```

Then update `UserPreferences.java`:
```java
@Column(name = "cuisine_preferences", length = 1000)
private String cuisinePreferences;

@Column(name = "meal_types", length = 500)
private String mealTypes;
```

#### B. Create Normalized Tables (Complex, Better)
```sql
CREATE TABLE user_cuisine_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    cuisine_type_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cuisine_type_id) REFERENCES cuisine_types(id) ON DELETE CASCADE,
    UNIQUE (user_id, cuisine_type_id)
);

CREATE TABLE user_meal_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    meal_type_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (meal_type_id) REFERENCES meal_types(id) ON DELETE CASCADE,
    UNIQUE (user_id, meal_type_id)
);
```

### Issue 2: Hibernate Warnings

**Warning:** `HHH90000026: MySQL8Dialect has been deprecated`

**Fix:**
```properties
# In application.properties, change:
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# To:
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**Warning:** `spring.jpa.open-in-view is enabled by default`

**Fix (if you want lazy loading to work properly):**
```properties
# Add to application.properties:
spring.jpa.open-in-view=false
```

Then ensure all repository methods use proper fetching strategies.

### Issue 3: Database Name Confusion

**Symptom:** "Values saved successfully" but not showing in database

**Cause:** Two databases exist (`cookmate` and `cookmate_db`), and you're checking the wrong one

**Fix:**
```sql
-- Check which database has your user
SELECT TABLE_SCHEMA, COUNT(*) 
FROM information_schema.TABLES 
WHERE TABLE_NAME = 'users' 
GROUP BY TABLE_SCHEMA;

-- If both exist, decide which to keep
-- Then update application.properties to match
```

## Testing Checklist

After migration, test these flows:

- [ ] User Registration → Check `users` table
- [ ] User Login → Verify JWT works
- [ ] Profile Update (basic fields) → Check `users` table for firstName, lastName, username
- [ ] Profile Update (preferences) → Check `user_preferences` table for all fields
- [ ] Forgot Password → Check `password_reset_tokens` table
- [ ] Reset Password → Verify token consumed and password changed
- [ ] Create Recipe → Check `recipes`, `recipe_ingredients`, `instructions` tables
- [ ] Add to Favorites → Check `favorites` table with unique constraint
- [ ] Add Review → Check `reviews` table
- [ ] Create Meal Plan → Check `meal_plans` and `meal_plan_recipes` tables
- [ ] Create Shopping List → Check `shopping_lists` and `shopping_list_items` tables
- [ ] View Recent Recipes → Check `recently_viewed` table

## Rollback Plan

If migration causes issues:

```sql
-- 1. Backup current database
mysqldump -u root -p cookmate > cookmate_backup_$(date +%Y%m%d_%H%M%S).sql

-- 2. If needed to rollback
mysql -u root -p cookmate < cookmate_backup_YYYYMMDD_HHMMSS.sql
```

## Next Steps

1. ✅ Run the aligned schema script
2. ✅ Verify `application.properties` points to correct DB
3. ✅ Test profile update flow end-to-end
4. ⚠️  Consider adding `cuisine_preferences` and `meal_types` columns or tables
5. ⚠️  Optionally normalize `dietary_restrictions` usage (currently stored as string)
6. ⚠️  Fix Hibernate dialect warning
7. 🎯 Deploy with confidence!

## Support

If you encounter issues:
- Check Hibernate SQL logs: `logging.level.org.hibernate.SQL=DEBUG`
- Verify table structure: `DESCRIBE table_name;`
- Check constraints: `SHOW CREATE TABLE table_name;`
- Monitor backend logs for serialization errors

---

**Schema Version:** 2025-11-04 (Aligned with current codebase)  
**Compatible with:** Spring Boot 3.1.5, Hibernate 6.x, MySQL 8.0+
