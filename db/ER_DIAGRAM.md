# CookMate Database - Entity Relationship Diagram

## Visual ER Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                  COOKMATE DATABASE SCHEMA                                │
│                            18 Tables | 20+ Foreign Keys | MySQL 8.0                      │
└─────────────────────────────────────────────────────────────────────────────────────────┘


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                   USER MANAGEMENT                                        ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

        ┌─────────────────────────────────┐
        │          users                  │
        ├─────────────────────────────────┤
        │ PK  id (BIGINT)                 │
        │ UQ  username (VARCHAR 50)       │
        │ UQ  email (VARCHAR 100)         │
        │     password (VARCHAR 255)      │
        │     first_name                  │
        │     last_name                   │
        │     role (USER/ADMIN)           │
        │     provider (LOCAL/GOOGLE...)  │
        │     provider_id                 │
        │     image_url                   │
        │     is_enabled                  │
        │     is_locked                   │
        │     created_at                  │
        │     updated_at                  │
        └────────────┬────────────────────┘
                     │
                     │ 1:1
                     ▼
        ┌─────────────────────────────────┐
        │      user_preferences           │
        ├─────────────────────────────────┤
        │ PK  id (BIGINT)                 │
        │ FK  user_id (UNIQUE)            │
        │     cooking_skill_level         │
        │     preferred_prep_time         │
        │     preferred_cook_time         │
        │     household_size              │
        │     budget_preference           │
        │     health_goals                │
        │     food_allergies              │
        │     cooking_equipment           │
        │     meal_planning_frequency     │
        │     cuisine_preferences         │
        │     meal_types                  │
        │     created_at                  │
        │     updated_at                  │
        └─────────────────────────────────┘


        users (1) ──────┐                     ┌──────────────────────────────┐
                        │ 1:N                 │  password_reset_tokens       │
                        └────────────────────►│──────────────────────────────│
                                              │ PK  id (BIGINT)              │
                                              │ UQ  token (VARCHAR 255)      │
                                              │ FK  user_id                  │
                                              │     expiry_date              │
                                              │     created_at               │
                                              └──────────────────────────────┘


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                              DIETARY RESTRICTIONS (N:M)                                  ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

┌──────────────────────────┐           ┌─────────────────────────────┐          ┌───────────────────────────┐
│  dietary_restrictions    │           │user_dietary_restrictions    │          │        users              │
│  (Lookup Table)          │           │   (Junction Table)          │          │                           │
├──────────────────────────┤    1      ├─────────────────────────────┤     N    ├───────────────────────────┤
│ PK  id                   │◄──────────┤ PK  id                      ├──────────│ PK  id                    │
│ UQ  name                 │           │ FK  user_id                 │          │     ...                   │
│     description          │           │ FK  dietary_restriction_id  │          └───────────────────────────┘
│     is_active            │           │     strictness_level        │
│     created_at           │           │     notes                   │
└──────────────────────────┘           │     created_at              │
                                       │ UNIQUE (user_id,            │
Seed Data:                             │         dietary_restriction)│
• Vegetarian                           └─────────────────────────────┘
• Vegan
• Gluten-Free
• Dairy-Free
• Nut-Free
• etc. (13 total)


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                  RECIPE MANAGEMENT                                       ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

                    ┌───────────────────────────────────────────┐
                    │             recipes                       │
                    ├───────────────────────────────────────────┤
                    │ PK  id (BIGINT)                           │
                    │     title (VARCHAR 200) ★                 │
                    │     description (TEXT)                    │
                    │     cuisine_type ─────► [cuisine_types]   │
                    │     meal_type ────────► [meal_types]      │
                    │     difficulty_level                      │
                    │     prep_time, cook_time, total_time      │
                    │     servings (default 4)                  │
                    │ ┌───────────────────────────────────────┐ │
                    │ │   NUTRITIONAL INFO                    │ │
                    │ │   calories, protein, carbs, fat, fiber│ │
                    │ └───────────────────────────────────────┘ │
                    │     image_url, video_url                  │
                    │ ┌───────────────────────────────────────┐ │
                    │ │   DIETARY FLAGS                       │ │
                    │ │   is_vegetarian, is_vegan             │ │
                    │ │   is_gluten_free, is_dairy_free       │ │
                    │ └───────────────────────────────────────┘ │
                    │     average_rating (DECIMAL 3,2)          │
                    │     total_reviews (INT)                   │
                    │     view_count (INT)                      │
                    │ FK  created_by → users(id)                │
                    │     created_at, updated_at                │
                    └──────────┬──────────┬──────────┬──────────┘
                               │          │          │
                   ┌───────────┘          │          └─────────────┐
                   │ 1:N                  │ 1:N                    │ 1:N
                   ▼                      ▼                        ▼
    ┌──────────────────────┐  ┌────────────────────┐  ┌───────────────────────┐
    │   instructions       │  │  recipe_ingredients│  │      reviews          │
    ├──────────────────────┤  ├────────────────────┤  ├───────────────────────┤
    │ PK  id               │  │ PK  id             │  │ PK  id                │
    │ FK  recipe_id        │  │ FK  recipe_id      │  │ FK  recipe_id         │
    │     step_number      │  │ FK  ingredient_id  │  │ FK  user_id           │
    │     instruction      │  │     quantity       │  │     rating (1-5) ★    │
    │     timer_minutes    │  │     unit           │  │     comment           │
    │     image_url        │  │     notes          │  │     created_at        │
    └──────────────────────┘  └──────┬─────────────┘  │     updated_at        │
                                     │ N              └───────────────────────┘
                                     │
                                     │ 1
                                     ▼
                          ┌──────────────────────┐
                          │    ingredients       │
                          │    (Master Table)    │
                          ├──────────────────────┤
                          │ PK  id               │
                          │ UQ  name             │
                          │     category         │
                          │     created_at       │
                          └──────────────────────┘


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                            USER INTERACTIONS WITH RECIPES                                ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

                        users (1) ─────────┐
                                           │ N
                                           ▼
                              ┌──────────────────────┐
                              │      favorites       │
                              ├──────────────────────┤
                              │ PK  id               │
                              │ FK  user_id          │─┐
                              │ FK  recipe_id        │ │
                              │     created_at       │ │ UNIQUE
                              │                      │ │ (user_id,
                              │ UNIQUE (user_id,     │◄┘  recipe_id)
                              │         recipe_id)   │
                              └──────────────────────┘
                                           │
                                           │ N
                              recipes (1) ─┘


                        users (1) ─────────┐
                                           │ N
                                           ▼
                              ┌──────────────────────┐
                              │   recently_viewed    │
                              ├──────────────────────┤
                              │ PK  id               │
                              │ FK  user_id          │
                              │ FK  recipe_id        │
                              │     viewed_at ★      │
                              └──────────────────────┘
                                           │
                                           │ N
                              recipes (1) ─┘


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                   MEAL PLANNING                                          ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

                        users (1) ─────────┐
                                           │ N
                                           ▼
                              ┌──────────────────────┐
                              │     meal_plans       │
                              ├──────────────────────┤
                              │ PK  id               │
                              │ FK  user_id          │
                              │     name             │
                              │     start_date       │
                              │     end_date         │
                              │     created_at       │
                              │     updated_at       │
                              └──────────┬───────────┘
                                        │ 1
                                        │
                                        │ N
                                        ▼
                              ┌──────────────────────┐
                              │ meal_plan_recipes    │
                              │  (Junction Table)    │
                              ├──────────────────────┤
                              │ PK  id               │
                              │ FK  meal_plan_id     │
                              │ FK  recipe_id        │
                              │     planned_date ★   │
                              │     meal_time        │
                              │     servings         │
                              └──────────┬───────────┘
                                        │ N
                                        │
                                        │ 1
                              recipes ──┘


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                  SHOPPING LISTS                                          ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

                        users (1) ─────────┐
                                           │ N
                                           ▼
                              ┌──────────────────────┐
                              │   shopping_lists     │
                              ├──────────────────────┤
                              │ PK  id               │
                              │ FK  user_id          │
                              │     name             │
                              │     created_at       │
                              │     updated_at       │
                              └──────────┬───────────┘
                                        │ 1
                                        │
                                        │ N
                                        ▼
                              ┌──────────────────────┐
                              │shopping_list_items   │
                              ├──────────────────────┤
                              │ PK  id               │
                              │ FK  shopping_list_id │
                              │ FK  ingredient_id    │─────► ingredients (optional)
                              │     ingredient_name ★│
                              │     quantity         │
                              │     unit             │
                              │     is_purchased     │
                              │     category         │
                              └──────────────────────┘


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                   LOOKUP TABLES                                          ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

┌────────────────────────┐           ┌────────────────────────┐
│    cuisine_types       │           │      meal_types        │
│   (Reference Data)     │           │   (Reference Data)     │
├────────────────────────┤           ├────────────────────────┤
│ PK  id                 │           │ PK  id                 │
│ UQ  name               │           │ UQ  name               │
│     description        │           │     description        │
│     region             │           │     typical_time       │
│     is_active          │           │     is_active          │
│     created_at         │           │     created_at         │
└────────────────────────┘           └────────────────────────┘
           │                                    │
           │ Referenced by                      │ Referenced by
           │ recipes.cuisine_type (STRING)      │ recipes.meal_type (STRING)
           ▼                                    ▼
    recipes.cuisine_type              recipes.meal_type
    (Not enforced FK)                 (Not enforced FK)


Seed Data:                           Seed Data:
• Italian (Europe)                   • Breakfast (Morning)
• Chinese (Asia)                     • Brunch (Late Morning)
• Mexican (North America)            • Lunch (Afternoon)
• Indian (Asia)                      • Dinner (Evening)
• Japanese (Asia)                    • Snack (Anytime)
• Thai (Asia)                        • Dessert (After meals)
• French (Europe)                    • Appetizer (Before meals)
• Mediterranean (Europe)             • Beverage (Anytime)
• American (North America)
• Korean (Asia)
• Vietnamese (Asia)
• Middle Eastern (Middle East)
• Greek (Europe)
• Spanish (Europe)
• Caribbean (Caribbean)


┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                      LEGEND                                              ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

PK    = Primary Key
FK    = Foreign Key
UQ    = Unique Constraint
★     = Indexed/Important field
1:1   = One-to-One relationship
1:N   = One-to-Many relationship
N:M   = Many-to-Many relationship (requires junction table)
───►  = References (foreign key)
──┐   = Relationship connector


CASCADE DELETE Rules:
━━━━━━━━━━━━━━━━━━━━
• Deleting a user → Deletes all user_preferences, favorites, reviews, meal_plans, 
                    shopping_lists, password_reset_tokens, user_dietary_restrictions
• Deleting a recipe → Deletes all instructions, recipe_ingredients, reviews, favorites
• Deleting a meal_plan → Deletes all meal_plan_recipes
• Deleting a shopping_list → Deletes all shopping_list_items


SET NULL Rules:
━━━━━━━━━━━━━━━
• Deleting a user → Sets recipes.created_by to NULL (recipe remains)
• Deleting an ingredient → Sets shopping_list_items.ingredient_id to NULL
                          (shopping_list_items.ingredient_name remains)


INDEX HIGHLIGHTS:
━━━━━━━━━━━━━━━━━
• users: username, email (UNIQUE), role, created_at
• recipes: title, cuisine_type, meal_type, average_rating, FULLTEXT(title, description)
• favorites: UNIQUE(user_id, recipe_id)
• reviews: recipe_id, user_id, rating, created_at
• meal_plan_recipes: (meal_plan_id, planned_date, meal_time) composite
• shopping_list_items: is_purchased, category
• recently_viewed: (user_id, viewed_at DESC) composite
```

---

## Key Design Decisions

### 1. String-Based Cuisine/Meal Types
**Decision:** Store cuisine_type and meal_type as VARCHAR in recipes, not foreign keys.
**Rationale:** 
- Flexibility for custom entries
- No ALTER TABLE needed for new types
- Lookup tables provide UI options but don't enforce constraints
- Avoids orphaned recipes if types are deleted

### 2. Comma-Separated Preferences
**Decision:** Store cuisine_preferences and meal_types in user_preferences as comma-separated strings.
**Rationale:**
- Simpler data model (no additional junction tables)
- Easy to serialize/deserialize in backend
- Sufficient for simple multi-select preferences
- Frontend handles splitting/joining

### 3. Optional Ingredient Reference
**Decision:** shopping_list_items has optional FK to ingredients.
**Rationale:**
- Users can add custom items not in master list
- ingredient_name is always populated (user-facing)
- ingredient_id provides link when available
- Flexible for manual list creation

### 4. Recipe Creator Can Be NULL
**Decision:** recipes.created_by can be NULL (SET NULL on user delete).
**Rationale:**
- Preserve recipes even if creator account is deleted
- Community recipes outlive individual users
- Created_at timestamp still tracks when recipe was added

### 5. Separate Reviews and Ratings
**Decision:** Unified reviews table with rating + comment.
**Rationale:**
- Simpler than separate tables
- Comment is optional (rating-only reviews allowed)
- Single source of truth for aggregating average_rating

---

## Database Statistics

| Metric | Count |
|--------|-------|
| **Total Tables** | 18 |
| **Core Tables** | 3 (users, user_preferences, password_reset_tokens) |
| **Recipe Tables** | 7 (recipes, instructions, ingredients, recipe_ingredients, reviews, favorites, recently_viewed) |
| **Meal Planning Tables** | 2 (meal_plans, meal_plan_recipes) |
| **Shopping Tables** | 2 (shopping_lists, shopping_list_items) |
| **Lookup Tables** | 4 (dietary_restrictions, user_dietary_restrictions, cuisine_types, meal_types) |
| **Foreign Keys** | 20+ |
| **Unique Constraints** | 12+ |
| **Indexes** | 60+ |
| **Seed Records** | 36 (13 dietary + 15 cuisines + 8 meal types) |

---

**Generated from:** Complete Spring Boot 3.1.5 + React 18 codebase analysis
**Version:** 1.0
**Last Updated:** Based on current entity structure with cuisine_preferences and meal_types fields
