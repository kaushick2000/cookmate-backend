# 🚀 Apply Database Changes - Visual Guide

## ⚡ The Fastest Way (30 seconds)

```bash
# Step 1: Open terminal and navigate to db folder
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend/db

# Step 2: Run the update script
mysql -u root -p < update_existing_database.sql

# Step 3: Enter your MySQL password when prompted
# Password: Jo.marley@2406

# ✅ Done! Skip to "Test It" section below
```

---

## 📋 What Just Happened?

```
┌─────────────────────────────────────────┐
│  Your Database (Before)                 │
├─────────────────────────────────────────┤
│  user_preferences                       │
│    ├─ cooking_skill_level ✅           │
│    ├─ preferred_prep_time ✅           │
│    ├─ food_allergies ✅                │
│    ├─ cuisine_preferences ❌ MISSING   │
│    └─ meal_types ❌ MISSING            │
└─────────────────────────────────────────┘
                   ↓
       [Run update script]
                   ↓
┌─────────────────────────────────────────┐
│  Your Database (After)                  │
├─────────────────────────────────────────┤
│  user_preferences                       │
│    ├─ cooking_skill_level ✅           │
│    ├─ preferred_prep_time ✅           │
│    ├─ food_allergies ✅                │
│    ├─ cuisine_preferences ✅ NEW!      │
│    └─ meal_types ✅ NEW!               │
└─────────────────────────────────────────┘
```

---

## 🧪 Test It

### 1. Verify Database Changes

```bash
mysql -u root -p
```

```sql
USE cookmate;

-- Check new columns exist
DESCRIBE user_preferences;

-- You should see:
-- | cuisine_preferences | varchar(1000) | YES  | NULL | NULL    |
-- | meal_types          | varchar(500)  | YES  | NULL | NULL    |
```

### 2. Start Backend

```bash
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend
java -jar target/cookmate-backend-0.0.1-SNAPSHOT.jar
```

**Expected Output:**
```
✅ Started CookmateBackendApplication
✅ Tomcat started on port 8080
✅ No errors about missing columns
```

### 3. Start Frontend

```bash
cd /Users/apple/Documents/Cookmate-Repo/cookmate-frontend
npm run dev
```

**Expected Output:**
```
✅ VITE ready
✅ Local: http://localhost:3000/
```

### 4. Test Profile Update

1. **Open browser:** http://localhost:3000
2. **Login** with your account
3. **Go to Profile** (click your avatar/name)
4. **Click "Edit Profile"**
5. **Select options:**
   - ✅ Favorite Cuisines: Italian, Thai
   - ✅ Preferred Meal Types: Breakfast, Dinner
6. **Click "Save Changes"**
7. **See toast:** "Profile updated successfully!" ✅

### 5. Verify in Database

```sql
-- In MySQL
SELECT 
    username,
    cuisine_preferences,
    meal_types
FROM user_preferences up
JOIN users u ON up.user_id = u.id;
```

**Expected Result:**
```
+----------+----------------+-----------------+
| username | cuisine_prefs  | meal_types      |
+----------+----------------+-----------------+
| youruser | Italian, Thai  | Breakfast, Din… |
+----------+----------------+-----------------+
```

✅ **SUCCESS!** Your profile preferences are now saving correctly!

---

## 🎯 Complete Flow Diagram

```
┌──────────────┐
│   Frontend   │  User clicks "Save Changes"
│ Profile.jsx  │
└──────┬───────┘
       │ formData = {
       │   preferences: {
       │     cuisinePreferences: ['Italian', 'Thai'],
       │     mealTypes: ['Breakfast', 'Dinner']
       │   }
       │ }
       ↓
┌──────────────┐
│  authApi.js  │  PUT /auth/profile
└──────┬───────┘
       │
       ↓
┌──────────────┐
│   Backend    │  AuthController.updateProfile()
│ AuthService  │
└──────┬───────┘
       │ String.join(", ", cuisinePreferences)
       │ → "Italian, Thai"
       │
       │ preferences.setCuisinePreferences("Italian, Thai")
       │ preferences.setMealTypes("Breakfast, Dinner")
       │
       ↓
┌──────────────┐
│   Database   │  UPDATE user_preferences SET
│   cookmate   │    cuisine_preferences = 'Italian, Thai',
└──────────────┘    meal_types = 'Breakfast, Dinner'
                  WHERE user_id = 1
```

---

## ⚠️ Troubleshooting

### ❌ "ERROR 1146: Table 'cookmate.user_preferences' doesn't exist"

**Solution:** Database doesn't exist. Create it first:

```bash
mysql -u root -p < cookmate_schema_enhanced.sql
```

---

### ❌ "ERROR 1062: Duplicate column name 'cuisine_preferences'"

**Solution:** Column already exists. This is good! ✅ Skip the update.

---

### ❌ "Access denied for user 'root'"

**Solution:** Check your MySQL password:

```bash
# Test connection
mysql -u root -p

# If password wrong, update in apply_changes.sh
```

---

### ❌ Backend shows: "Column 'cuisine_preferences' not found"

**Solution:** Backend running old code. Restart it:

```bash
# Kill backend
lsof -ti:8080 | xargs kill -9

# Restart
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend
java -jar target/cookmate-backend-0.0.1-SNAPSHOT.jar
```

---

### ✅ "Profile updated successfully!" but no data in database

**Solution:** Check you're looking at the correct database:

```sql
-- Make sure you're in 'cookmate' not 'cookmate_db'
USE cookmate;
SELECT DATABASE();  -- Should show 'cookmate'
```

---

## 🎉 Success Checklist

After completing, you should have:

- [x] ✅ New columns in `user_preferences` table
- [x] ✅ Backend starts without errors
- [x] ✅ Frontend starts without errors
- [x] ✅ Profile save shows success message
- [x] ✅ Data appears in database
- [x] ✅ No console errors in browser
- [x] ✅ No errors in backend logs

---

## 📚 What's Next?

Now that your database is aligned:

1. **Test other features** (recipes, favorites, meal plans)
2. **Deploy to production** (backup first!)
3. **Add more preferences** (see MIGRATION_GUIDE.md for ideas)
4. **Monitor logs** for any issues

---

## 🆘 Still Having Issues?

### Check these files for more help:

| Issue | Read This File |
|-------|---------------|
| Need step-by-step | `MIGRATION_GUIDE.md` |
| Want to understand changes | `README.md` |
| See exactly what changed | `CHANGES_SUMMARY.md` |
| Quick reference | `QUICK_START.md` |
| All files overview | `INDEX.md` |

---

## 🔄 Rollback (If Needed)

Made a mistake? No problem:

```bash
# If you created a backup
mysql -u root -p cookmate < ./backups/cookmate_backup_YYYYMMDD_HHMMSS.sql

# If no backup, just drop the columns
mysql -u root -p
```

```sql
USE cookmate;
ALTER TABLE user_preferences DROP COLUMN cuisine_preferences;
ALTER TABLE user_preferences DROP COLUMN meal_types;
```

---

## 🎯 One-Liner Commands

Copy-paste these for common tasks:

```bash
# Apply changes
mysql -u root -p < update_existing_database.sql

# Verify columns exist
mysql -u root -p -e "DESCRIBE cookmate.user_preferences" | grep -E "(cuisine|meal)"

# Check sample data
mysql -u root -p -e "SELECT COUNT(*) FROM cookmate.dietary_restrictions"

# Start backend
cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend && java -jar target/*.jar

# Start frontend
cd /Users/apple/Documents/Cookmate-Repo/cookmate-frontend && npm run dev

# Check backend logs
tail -f /Users/apple/Documents/Cookmate-Repo/cookmate-backend/logs/app.log
```

---

**You're done! 🎉 Your database now matches your frontend and backend perfectly.**
