# 📁 Database Files Index

All files for Cookmate database schema alignment and migration.

## 🚀 Quick Start

**Just want to apply the changes? Run this:**

```bash
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend/db
./apply_changes.sh
```

Or manually:

```bash
mysql -u root -p < update_existing_database.sql
```

---

## 📄 File Overview

### 🎯 Action Files (Use These)

| File | Size | Purpose | When to Use |
|------|------|---------|-------------|
| **apply_changes.sh** | 5.7K | Automated update script | 🌟 **RECOMMENDED** - Handles everything automatically |
| **update_existing_database.sql** | 5.2K | Safe update for existing DB | When you have data to preserve |
| **cookmate_schema_enhanced.sql** | 15K | Full schema with sample data | Fresh setup or recreation |
| **cookmate_schema_aligned.sql** | 13K | Base schema without extras | Minimal setup (no sample data) |

### 📚 Documentation Files (Read These)

| File | Size | Purpose | When to Read |
|------|------|---------|--------------|
| **QUICK_START.md** | 2.9K | 30-second guide | 🌟 **START HERE** - Quick reference |
| **README.md** | 9.7K | Complete overview | Understanding changes |
| **MIGRATION_GUIDE.md** | 9.8K | Detailed instructions | Step-by-step help |
| **CHANGES_SUMMARY.md** | 7.5K | Before/after comparison | Technical details |

---

## 🎯 Which File Should I Use?

### Scenario 1: "I have data in my database and want to add the new features"
→ **Use:** `update_existing_database.sql` or `apply_changes.sh`

### Scenario 2: "I'm starting fresh and want everything"
→ **Use:** `cookmate_schema_enhanced.sql`

### Scenario 3: "I want minimal changes, no sample data"
→ **Use:** `cookmate_schema_aligned.sql`

### Scenario 4: "I'm not sure, just make it work!"
→ **Use:** `apply_changes.sh` (it decides for you!)

---

## 📖 File Details

### apply_changes.sh ⭐
**Automated bash script that:**
- Creates backup of existing database
- Detects if database exists
- Applies appropriate schema (full or update)
- Verifies changes
- Shows next steps

**Usage:**
```bash
chmod +x apply_changes.sh
./apply_changes.sh
```

---

### update_existing_database.sql ⭐
**Safe update for existing databases:**
- ✅ Adds `cuisine_preferences` column
- ✅ Adds `meal_types` column
- ✅ Adds unique constraint to `user_dietary_restrictions`
- ✅ Adds unique constraint to `favorites`
- ✅ Populates lookup tables (INSERT IGNORE)
- ✅ No data loss
- ✅ Can run multiple times safely

**Usage:**
```bash
mysql -u root -p < update_existing_database.sql
```

---

### cookmate_schema_enhanced.sql
**Complete schema with enhancements:**
- All 18 tables with proper constraints
- Named foreign keys for clarity
- New columns for full frontend support
- Sample data in lookup tables (10 dietary restrictions, 12 cuisines, 5 meal types)

**Usage:**
```bash
# WARNING: Drops and recreates database!
mysql -u root -p < cookmate_schema_enhanced.sql
```

---

### cookmate_schema_aligned.sql
**Base schema without sample data:**
- All 18 tables with proper constraints
- Named foreign keys
- New columns for frontend support
- Empty lookup tables (no sample data)

**Usage:**
```bash
# WARNING: Drops and recreates database!
mysql -u root -p < cookmate_schema_aligned.sql
```

---

### QUICK_START.md
**30-second reference guide:**
- Fastest way to apply changes
- Quick verification commands
- Testing checklist
- Troubleshooting common issues

---

### README.md
**Complete overview:**
- What changed and why
- Before/after comparison
- Feature list
- Data flow diagrams
- Testing instructions
- Future enhancements

---

### MIGRATION_GUIDE.md
**Detailed migration guide:**
- Step-by-step instructions
- Multiple migration strategies
- Verification procedures
- Known issues and solutions
- Rollback procedures
- Production considerations

---

### CHANGES_SUMMARY.md
**Technical change log:**
- Database schema changes (before/after SQL)
- Backend code changes (before/after Java)
- Data flow diagrams
- Safety analysis
- Impact assessment

---

## 🔧 What Gets Changed

### Database Changes
```
user_preferences table:
  + cuisine_preferences VARCHAR(1000)  [NEW]
  + meal_types VARCHAR(500)            [NEW]

user_dietary_restrictions table:
  + UNIQUE constraint (user_id, dietary_restriction_id) [NEW]

favorites table:
  + UNIQUE constraint (user_id, recipe_id) [IMPROVED]

Lookup tables:
  + Sample data populated [OPTIONAL]
```

### Backend Changes
```
UserPreferences.java:
  + cuisinePreferences field
  + mealTypes field
  + Getters and setters

AuthService.java:
  + Save cuisinePreferences logic
  + Save mealTypes logic
```

### Frontend Changes
```
None required - already compatible! ✅
```

---

## ✅ Success Criteria

After running any script, verify:

```sql
-- Check columns exist
DESCRIBE user_preferences;
-- Should show cuisine_preferences and meal_types

-- Check constraints
SHOW CREATE TABLE user_dietary_restrictions;
-- Should show UNIQUE constraint

-- Check sample data (if using enhanced schema)
SELECT COUNT(*) FROM dietary_restrictions;  -- Should be 10
SELECT COUNT(*) FROM cuisine_types;         -- Should be 12
SELECT COUNT(*) FROM meal_types;            -- Should be 5
```

---

## 🆘 Troubleshooting

### "Permission denied" on apply_changes.sh
```bash
chmod +x apply_changes.sh
```

### "Access denied for user 'root'"
```bash
# Update password in apply_changes.sh or use -p flag:
mysql -u root -p < update_existing_database.sql
```

### "Column already exists"
✅ Safe to ignore - means column was already added

### "Can't DROP database 'cookmate'; database doesn't exist"
✅ Normal if first time - script creates it

---

## 📞 File Dependencies

```
apply_changes.sh
  └─ Uses: update_existing_database.sql (if DB exists)
  └─ Uses: cookmate_schema_enhanced.sql (if DB doesn't exist)

update_existing_database.sql
  └─ Requires: Existing 'cookmate' database

cookmate_schema_enhanced.sql
  └─ Creates: Fresh 'cookmate' database

cookmate_schema_aligned.sql
  └─ Creates: Fresh 'cookmate' database (minimal)
```

---

## 🎓 Learning Path

1. **Read:** QUICK_START.md (2 min)
2. **Run:** apply_changes.sh (1 min)
3. **Test:** Profile update in UI (3 min)
4. **Verify:** Check database (1 min)
5. **Deep dive:** README.md if curious (10 min)

---

## 📊 File Statistics

- **Total Files:** 8
- **Total Size:** ~68 KB
- **SQL Files:** 3
- **Documentation:** 4
- **Scripts:** 1
- **Lines of SQL:** ~800
- **Lines of Docs:** ~1,400

---

## 🎉 Quick Win

Want the absolute fastest way? Copy and paste this:

```bash
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend/db
mysql -u root -p < update_existing_database.sql
```

That's it! Your database is now aligned with your frontend and backend. 🚀

---

**Last Updated:** November 4, 2025  
**Schema Version:** Enhanced v1.0  
**Compatibility:** Spring Boot 3.1.5, MySQL 8.0+, React 18+
