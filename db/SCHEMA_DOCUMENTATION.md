# CookMate Complete Database Schema Documentation

## Overview
This document provides comprehensive documentation for the CookMate database schema, generated from a complete analysis of the Spring Boot backend and React frontend codebase.

## Database Information
- **Database Name:** cookmate
- **Database Engine:** MySQL 8.0
- **Character Set:** utf8mb4 (full Unicode support)
- **Collation:** utf8mb4_unicode_ci
- **Total Tables:** 18
- **Backend Framework:** Spring Boot 3.1.5 with Hibernate 6.2.13
- **Frontend:** React 18 with Vite

---

## Table of Contents
1. [Entity Relationship Overview](#entity-relationship-overview)
2. [Core Tables](#core-tables)
3. [Recipe Management](#recipe-management)
4. [Meal Planning](#meal-planning)
5. [Shopping Lists](#shopping-lists)
6. [Lookup Tables](#lookup-tables)
7. [Relationship Diagram](#relationship-diagram)
8. [Migration Guide](#migration-guide)

---

## Entity Relationship Overview

### User Domain
```
users (core user data)
  ├─ 1:1 → user_preferences (cooking preferences)
  ├─ 1:N → password_reset_tokens (password recovery)
  ├─ 1:N → reviews (recipe reviews)
  ├─ 1:N → favorites (favorite recipes)
  ├─ 1:N → meal_plans (meal planning)
  ├─ 1:N → shopping_lists (shopping lists)
  ├─ 1:N → recently_viewed (browsing history)
  └─ N:M → dietary_restrictions (via user_dietary_restrictions)
```

### Recipe Domain
```
recipes (recipe data)
  ├─ N:1 → users (creator)
  ├─ 1:N → instructions (cooking steps)
  ├─ 1:N → reviews (user ratings)
  ├─ 1:N → favorites (favorited by users)
  ├─ 1:N → recently_viewed (view tracking)
  └─ N:M → ingredients (via recipe_ingredients)
```

### Meal Planning Domain
```
meal_plans (plan container)
  ├─ N:1 → users (owner)
  └─ 1:N → meal_plan_recipes (scheduled recipes)
       └─ N:1 → recipes (recipe reference)
```

### Shopping Domain
```
shopping_lists (list container)
  ├─ N:1 → users (owner)
  └─ 1:N → shopping_list_items (items)
       └─ N:1 → ingredients (optional reference)
```

---

## Core Tables

### 1. users
**Purpose:** Core authentication and user profile management

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| username | VARCHAR(50) | NOT NULL, UNIQUE | Unique username |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Email address |
| password | VARCHAR(255) | NOT NULL | Encrypted password (BCrypt) |
| first_name | VARCHAR(50) | | User's first name |
| last_name | VARCHAR(50) | | User's last name |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | USER or ADMIN |
| provider | VARCHAR(20) | DEFAULT 'LOCAL' | Auth provider (LOCAL, GOOGLE, FACEBOOK, etc.) |
| provider_id | VARCHAR(100) | | External provider user ID |
| image_url | VARCHAR(500) | | Profile image URL |
| is_enabled | BOOLEAN | DEFAULT TRUE | Account enabled status |
| is_locked | BOOLEAN | DEFAULT FALSE | Account locked status |
| created_at | DATETIME | NOT NULL | Account creation timestamp |
| updated_at | DATETIME | NOT NULL | Last update timestamp |

**Relationships:**
- 1:1 with `user_preferences`
- 1:N with `favorites`, `reviews`, `meal_plans`, `shopping_lists`, `password_reset_tokens`, `recently_viewed`
- N:M with `dietary_restrictions` (via `user_dietary_restrictions`)

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (username)
- UNIQUE (email)
- INDEX (role)
- INDEX (created_at)

**Business Rules:**
- Username must be 3-50 characters
- Email must be valid format
- Password must be at least 6 characters (encrypted with BCrypt)
- Role can only be 'USER' or 'ADMIN'
- Provider can be LOCAL, GOOGLE, FACEBOOK, TWITTER, or INSTAGRAM

---

### 2. user_preferences
**Purpose:** Store user cooking preferences and profile settings (1:1 with users)

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, UNIQUE, FK | References users(id) |
| cooking_skill_level | VARCHAR(50) | | BEGINNER, INTERMEDIATE, ADVANCED, EXPERT |
| preferred_prep_time | INT | | Preferred prep time in minutes |
| preferred_cook_time | INT | | Preferred cook time in minutes |
| household_size | INT | | Number of people cooking for |
| budget_preference | VARCHAR(50) | | LOW, MODERATE, HIGH, NO_PREFERENCE |
| health_goals | VARCHAR(1000) | | Free text health goals |
| food_allergies | VARCHAR(1000) | | Free text allergy information |
| cooking_equipment | VARCHAR(1000) | | Available cooking equipment |
| meal_planning_frequency | VARCHAR(50) | | DAILY, WEEKLY, MONTHLY, RARELY, NEVER |
| cuisine_preferences | VARCHAR(1000) | | Comma-separated cuisine names |
| meal_types | VARCHAR(500) | | Comma-separated meal type names |
| created_at | DATETIME | NOT NULL | Creation timestamp |
| updated_at | DATETIME | NOT NULL | Last update timestamp |

**Relationships:**
- 1:1 with `users` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (user_id)

**Business Rules:**
- Each user can have only one preferences record
- Deleting a user automatically deletes their preferences (CASCADE)
- cuisine_preferences and meal_types are stored as comma-separated strings for flexibility

---

### 3. password_reset_tokens
**Purpose:** Manage password reset tokens for email-based password recovery

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| token | VARCHAR(255) | NOT NULL, UNIQUE | Unique reset token |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| expiry_date | DATETIME | NOT NULL | Token expiration timestamp |
| created_at | DATETIME | NOT NULL | Token creation timestamp |

**Relationships:**
- N:1 with `users` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (token)
- INDEX (user_id)
- INDEX (expiry_date)

**Business Rules:**
- Tokens are typically valid for 24 hours (configured in backend)
- Expired tokens should be cleaned up periodically
- One user can have multiple active tokens
- Token is single-use (deleted after successful password reset)

---

## Recipe Management

### 4. recipes
**Purpose:** Store recipe information with nutritional data and metadata

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| title | VARCHAR(200) | NOT NULL | Recipe title |
| description | TEXT | | Recipe description |
| cuisine_type | VARCHAR(50) | | Cuisine category (Italian, Chinese, etc.) |
| meal_type | VARCHAR(50) | | Meal category (Breakfast, Lunch, etc.) |
| difficulty_level | VARCHAR(20) | | Recipe difficulty |
| prep_time | INT | | Preparation time in minutes |
| cook_time | INT | | Cooking time in minutes |
| total_time | INT | | Total time in minutes |
| servings | INT | DEFAULT 4 | Number of servings |
| calories | INT | | Calories per serving |
| protein | DECIMAL(10,2) | | Protein in grams |
| carbs | DECIMAL(10,2) | | Carbohydrates in grams |
| fat | DECIMAL(10,2) | | Fat in grams |
| fiber | DECIMAL(10,2) | | Fiber in grams |
| image_url | VARCHAR(500) | | Recipe image URL |
| video_url | VARCHAR(500) | | Recipe video URL |
| is_vegetarian | BOOLEAN | DEFAULT FALSE | Vegetarian flag |
| is_vegan | BOOLEAN | DEFAULT FALSE | Vegan flag |
| is_gluten_free | BOOLEAN | DEFAULT FALSE | Gluten-free flag |
| is_dairy_free | BOOLEAN | DEFAULT FALSE | Dairy-free flag |
| average_rating | DECIMAL(3,2) | DEFAULT 0.00 | Average user rating (0-5) |
| total_reviews | INT | DEFAULT 0 | Total number of reviews |
| view_count | INT | DEFAULT 0 | Number of times viewed |
| created_by | BIGINT | FK | References users(id) |
| created_at | DATETIME | NOT NULL | Creation timestamp |
| updated_at | DATETIME | NOT NULL | Last update timestamp |

**Relationships:**
- N:1 with `users` (creator - SET NULL on delete)
- 1:N with `instructions`, `recipe_ingredients`, `reviews`, `favorites`
- N:M with `ingredients` (via `recipe_ingredients`)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (title)
- INDEX (cuisine_type)
- INDEX (meal_type)
- INDEX (difficulty_level)
- INDEX (created_by)
- INDEX (average_rating)
- INDEX (created_at)
- COMPOSITE (is_vegetarian, is_vegan, is_gluten_free, is_dairy_free) - for dietary filtering
- FULLTEXT (title, description) - for search functionality

**Business Rules:**
- title is required and limited to 200 characters
- average_rating is calculated from reviews and ranges from 0.00 to 5.00
- total_reviews is incremented when a new review is added
- view_count is incremented each time a recipe is viewed
- Dietary flags (is_vegetarian, etc.) are set by recipe creator

---

### 5. instructions
**Purpose:** Store step-by-step cooking instructions for recipes

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| recipe_id | BIGINT | NOT NULL, FK | References recipes(id) |
| step_number | INT | NOT NULL | Sequential step number |
| instruction | TEXT | NOT NULL | Step instruction text |
| timer_minutes | INT | | Optional timer duration |
| image_url | VARCHAR(500) | | Optional step image |

**Relationships:**
- N:1 with `recipes` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (recipe_id)
- COMPOSITE (recipe_id, step_number) - for ordered retrieval

**Business Rules:**
- Instructions are ordered by step_number
- Deleting a recipe deletes all its instructions (CASCADE)
- step_number determines the display order

---

### 6. ingredients
**Purpose:** Master list of all ingredients

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Ingredient name |
| category | VARCHAR(50) | | Ingredient category |
| created_at | DATETIME | NOT NULL | Creation timestamp |

**Relationships:**
- 1:N with `recipe_ingredients`, `shopping_list_items`

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (name)
- INDEX (category)

**Business Rules:**
- Each ingredient name must be unique
- Categories include: Protein, Vegetable, Grain, Dairy, Spice, Fruit, etc.
- Central ingredient repository for consistency

---

### 7. recipe_ingredients
**Purpose:** Junction table linking recipes to ingredients with quantities

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| recipe_id | BIGINT | NOT NULL, FK | References recipes(id) |
| ingredient_id | BIGINT | NOT NULL, FK | References ingredients(id) |
| quantity | DECIMAL(10,2) | | Amount of ingredient |
| unit | VARCHAR(50) | | Measurement unit (cup, tbsp, etc.) |
| notes | TEXT | | Additional notes |

**Relationships:**
- N:1 with `recipes` (CASCADE DELETE)
- N:1 with `ingredients` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (recipe_id)
- INDEX (ingredient_id)

**Business Rules:**
- Each recipe can have multiple ingredients
- Each ingredient can be used in multiple recipes
- Deleting a recipe deletes all its recipe_ingredients entries

---

### 8. reviews
**Purpose:** User reviews and ratings for recipes

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| recipe_id | BIGINT | NOT NULL, FK | References recipes(id) |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| rating | INT | NOT NULL, 1-5 | Star rating |
| comment | TEXT | | Review text |
| created_at | DATETIME | NOT NULL | Creation timestamp |
| updated_at | DATETIME | NOT NULL | Last update timestamp |

**Relationships:**
- N:1 with `recipes` (CASCADE DELETE)
- N:1 with `users` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (recipe_id)
- INDEX (user_id)
- INDEX (rating)
- INDEX (created_at)

**Business Rules:**
- Rating must be between 1 and 5
- Users can update their reviews (updated_at tracks changes)
- When a review is added/updated/deleted, recipe.average_rating and recipe.total_reviews are recalculated

---

### 9. favorites
**Purpose:** Track user favorite recipes

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| recipe_id | BIGINT | NOT NULL, FK | References recipes(id) |
| created_at | DATETIME | NOT NULL | Timestamp when favorited |

**Relationships:**
- N:1 with `users` (CASCADE DELETE)
- N:1 with `recipes` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (user_id, recipe_id) - prevents duplicate favorites
- INDEX (user_id)
- INDEX (recipe_id)
- INDEX (created_at)

**Business Rules:**
- Each user can favorite a recipe only once (unique constraint)
- Favoriting is a toggle operation in the frontend

---

### 10. recently_viewed
**Purpose:** Track recently viewed recipes for each user

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| recipe_id | BIGINT | NOT NULL, FK | References recipes(id) |
| viewed_at | DATETIME | NOT NULL | Last viewed timestamp |

**Relationships:**
- N:1 with `users` (CASCADE DELETE)
- N:1 with `recipes` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (user_id)
- INDEX (recipe_id)
- INDEX (viewed_at)
- COMPOSITE (user_id, viewed_at DESC) - for efficient recent queries

**Business Rules:**
- viewed_at is updated each time the user views the recipe
- Can be used to show "Recently Viewed" section in UI
- Old entries can be archived/deleted after a certain period

---

## Meal Planning

### 11. meal_plans
**Purpose:** Container for user meal plans with date ranges

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| name | VARCHAR(100) | | Plan name (e.g., "Week 1") |
| start_date | DATE | NOT NULL | Plan start date |
| end_date | DATE | NOT NULL | Plan end date |
| created_at | DATETIME | NOT NULL | Creation timestamp |
| updated_at | DATETIME | NOT NULL | Last update timestamp |

**Relationships:**
- N:1 with `users` (CASCADE DELETE)
- 1:N with `meal_plan_recipes`

**Indexes:**
- PRIMARY KEY (id)
- INDEX (user_id)
- INDEX (start_date)
- INDEX (end_date)
- COMPOSITE (user_id, start_date, end_date)

**Business Rules:**
- Each user can have multiple meal plans
- end_date should be >= start_date
- Plans can overlap

---

### 12. meal_plan_recipes
**Purpose:** Junction table linking meal plans to recipes with scheduling

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| meal_plan_id | BIGINT | NOT NULL, FK | References meal_plans(id) |
| recipe_id | BIGINT | NOT NULL, FK | References recipes(id) |
| planned_date | DATE | NOT NULL | Date to cook recipe |
| meal_time | VARCHAR(20) | | Breakfast, Lunch, Dinner, etc. |
| servings | INT | DEFAULT 1 | Number of servings |

**Relationships:**
- N:1 with `meal_plans` (CASCADE DELETE)
- N:1 with `recipes` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (meal_plan_id)
- INDEX (recipe_id)
- INDEX (planned_date)
- COMPOSITE (meal_plan_id, planned_date, meal_time)

**Business Rules:**
- Allows scheduling specific recipes on specific dates
- Same recipe can be scheduled multiple times
- servings can differ from recipe default

---

## Shopping Lists

### 13. shopping_lists
**Purpose:** Container for user shopping lists

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| name | VARCHAR(100) | | List name |
| created_at | DATETIME | NOT NULL | Creation timestamp |
| updated_at | DATETIME | NOT NULL | Last update timestamp |

**Relationships:**
- N:1 with `users` (CASCADE DELETE)
- 1:N with `shopping_list_items`

**Indexes:**
- PRIMARY KEY (id)
- INDEX (user_id)
- INDEX (created_at)

**Business Rules:**
- Each user can have multiple shopping lists
- Lists can be generated from meal plans or created manually

---

### 14. shopping_list_items
**Purpose:** Individual items in shopping lists

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| shopping_list_id | BIGINT | NOT NULL, FK | References shopping_lists(id) |
| ingredient_id | BIGINT | FK | Optional: references ingredients(id) |
| ingredient_name | VARCHAR(100) | NOT NULL | Item name |
| quantity | DECIMAL(10,2) | | Amount needed |
| unit | VARCHAR(50) | | Measurement unit |
| is_purchased | BOOLEAN | DEFAULT FALSE | Purchase status |
| category | VARCHAR(50) | | Item category for organization |

**Relationships:**
- N:1 with `shopping_lists` (CASCADE DELETE)
- N:1 with `ingredients` (SET NULL on delete - optional)

**Indexes:**
- PRIMARY KEY (id)
- INDEX (shopping_list_id)
- INDEX (ingredient_id)
- INDEX (is_purchased)
- INDEX (category)

**Business Rules:**
- ingredient_id is optional (for custom items)
- ingredient_name is always required (user-facing)
- is_purchased tracks whether item has been bought
- category helps organize items in shopping list UI

---

## Lookup Tables

### 15. dietary_restrictions
**Purpose:** Master list of dietary restrictions

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Restriction name |
| description | VARCHAR(500) | | Description |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| created_at | DATETIME | NOT NULL | Creation timestamp |

**Relationships:**
- 1:N with `user_dietary_restrictions`

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (name)
- INDEX (is_active)

**Seed Data:**
- Vegetarian, Vegan, Gluten-Free, Dairy-Free, Nut-Free, Soy-Free, Egg-Free, Shellfish-Free, Low-Carb, Keto, Paleo, Halal, Kosher

---

### 16. user_dietary_restrictions
**Purpose:** Junction table linking users to dietary restrictions

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| user_id | BIGINT | NOT NULL, FK | References users(id) |
| dietary_restriction_id | BIGINT | NOT NULL, FK | References dietary_restrictions(id) |
| strictness_level | VARCHAR(50) | DEFAULT 'MODERATE' | FLEXIBLE, MODERATE, STRICT, VERY_STRICT |
| notes | VARCHAR(500) | | Additional notes |
| created_at | DATETIME | NOT NULL | Creation timestamp |

**Relationships:**
- N:1 with `users` (CASCADE DELETE)
- N:1 with `dietary_restrictions` (CASCADE DELETE)

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (user_id, dietary_restriction_id) - prevents duplicates
- INDEX (user_id)
- INDEX (dietary_restriction_id)

**Business Rules:**
- Each user can select multiple dietary restrictions
- strictness_level allows granular control
- Used for recipe filtering and recommendations

---

### 17. cuisine_types
**Purpose:** Master list of cuisine types

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Cuisine name |
| description | VARCHAR(500) | | Description |
| region | VARCHAR(100) | | Geographic region |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| created_at | DATETIME | NOT NULL | Creation timestamp |

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (name)
- INDEX (region)
- INDEX (is_active)

**Seed Data:**
- Italian, Chinese, Mexican, Indian, Japanese, Thai, French, Mediterranean, American, Korean, Vietnamese, Middle Eastern, Greek, Spanish, Caribbean

**Note:** 
- This is a lookup table; recipes store cuisine_type as a string for flexibility
- Frontend can display available cuisines from this table

---

### 18. meal_types
**Purpose:** Master list of meal types

**Columns:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Meal type name |
| description | VARCHAR(500) | | Description |
| typical_time | VARCHAR(100) | | When typically eaten |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| created_at | DATETIME | NOT NULL | Creation timestamp |

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (name)
- INDEX (is_active)

**Seed Data:**
- Breakfast, Brunch, Lunch, Dinner, Snack, Dessert, Appetizer, Beverage

**Note:** 
- This is a lookup table; recipes store meal_type as a string for flexibility
- Frontend can display available meal types from this table

---

## Relationship Diagram

```
                    ┌──────────────────────┐
                    │       users          │
                    │  (id, username,      │
                    │   email, password)   │
                    └──────────┬───────────┘
                               │
                 ┌─────────────┼─────────────┐
                 │             │             │
        ┌────────▼─────┐  ┌───▼───────┐  ┌──▼──────────────┐
        │user_preferences│ │ favorites │  │ password_reset  │
        │  (1:1)        │  │  (N:M)    │  │   _tokens       │
        └───────────────┘  └───┬───────┘  └─────────────────┘
                               │
                               │
                     ┌─────────▼────────────┐
                     │      recipes         │
                     │  (id, title, ...     │
                     │   cuisine, meal)     │
                     └─────────┬────────────┘
                               │
         ┌─────────────────────┼──────────────────────┐
         │                     │                      │
    ┌────▼──────┐      ┌──────▼──────┐      ┌───────▼────────┐
    │instructions│      │recipe_      │      │   reviews      │
    │  (1:N)     │      │ingredients  │      │   (N:M)        │
    └────────────┘      │  (N:M)      │      └───────┬────────┘
                        └──────┬──────┘              │
                               │                     │
                        ┌──────▼──────┐             │
                        │ ingredients │             │
                        │  (master)   │             │
                        └─────────────┘             │
                                                    │
    ┌───────────────┐                        ┌─────▼─────────┐
    │  meal_plans   │◄───────────────────────┤    users      │
    │   (1:N)       │                        │               │
    └───────┬───────┘                        └───────┬───────┘
            │                                        │
    ┌───────▼───────────┐                    ┌──────▼────────┐
    │meal_plan_recipes  │                    │shopping_lists │
    │    (N:M)          │                    │    (1:N)      │
    └───────────────────┘                    └───────┬───────┘
                                                     │
                                             ┌───────▼─────────┐
                                             │shopping_list_   │
                                             │    items        │
                                             │    (1:N)        │
                                             └─────────────────┘

    ┌─────────────────────┐         ┌──────────────────────┐
    │dietary_restrictions │◄────────┤user_dietary_         │
    │     (lookup)        │         │restrictions (N:M)    │
    └─────────────────────┘         └──────────────────────┘
```

---

## Migration Guide

### Fresh Installation
1. Ensure MySQL 8.0+ is installed
2. Run the complete schema script:
   ```bash
   mysql -u root -p < cookmate_complete_schema.sql
   ```
3. Verify table creation:
   ```sql
   USE cookmate;
   SHOW TABLES;
   ```

### Existing Database Update
If you have an existing database, use one of the provided migration scripts:

1. **Safe Update Script** (recommended):
   ```bash
   mysql -u root -p cookmate < db/update_database_safe.sql
   ```

2. **Backup first** (always):
   ```bash
   mysqldump -u root -p cookmate > backup_$(date +%Y%m%d_%H%M%S).sql
   ```

### Verification Queries
```sql
-- Check all tables exist
SELECT COUNT(*) FROM information_schema.tables 
WHERE table_schema = 'cookmate';
-- Expected: 18

-- Check foreign keys
SELECT COUNT(*) FROM information_schema.table_constraints 
WHERE constraint_schema = 'cookmate' 
AND constraint_type = 'FOREIGN KEY';
-- Expected: 20+

-- Check seed data
SELECT COUNT(*) FROM dietary_restrictions;
-- Expected: 13

SELECT COUNT(*) FROM cuisine_types;
-- Expected: 15

SELECT COUNT(*) FROM meal_types;
-- Expected: 8
```

---

## Performance Optimization Notes

### Indexes
All tables have appropriate indexes for:
- Primary key access
- Foreign key relationships
- Common query patterns (filtering, sorting)
- Full-text search on recipes

### Query Optimization Tips
1. **Recipe Search**: Use FULLTEXT index on title/description
2. **Dietary Filtering**: Use composite index on dietary flags
3. **User Recipes**: Always filter by created_by with INDEX
4. **Recently Viewed**: Use composite index (user_id, viewed_at DESC)
5. **Meal Plans**: Use date range indexes

### Cascade Delete Strategy
- User deletion: Cascades to all related data (preferences, favorites, reviews, etc.)
- Recipe deletion: Cascades to instructions, ingredients, reviews, favorites
- Meal plan deletion: Cascades to meal_plan_recipes
- Shopping list deletion: Cascades to shopping_list_items

---

## Additional Notes

### Enums in Code vs. Database
The backend uses Java enums (e.g., `Role.USER`, `CookingSkillLevel.BEGINNER`), but the database stores these as VARCHAR to maintain flexibility and avoid ALTER TABLE statements for new enum values.

### String-based References
`cuisine_type` and `meal_type` in recipes are VARCHAR fields (not foreign keys) to allow flexibility. The lookup tables (`cuisine_types`, `meal_types`) provide options for dropdowns in the frontend but don't enforce referential integrity.

### Comma-Separated Values
Fields like `cuisine_preferences` and `meal_types` in `user_preferences` store comma-separated values as VARCHAR. This design choice allows flexible storage of multiple selections without additional junction tables.

### JSON vs. Relational
The schema uses relational design rather than JSON columns to maintain compatibility with older MySQL versions and to enable efficient querying and indexing.

---

## Schema Metadata
- **Generated:** From complete backend/frontend analysis
- **Entities Analyzed:** 18 JPA entities
- **Foreign Keys:** 20+
- **Indexes:** 60+
- **Seed Records:** 36 (dietary restrictions, cuisines, meal types)
- **Character Set:** UTF-8 (utf8mb4) for full Unicode support including emojis

---

**End of Documentation**
