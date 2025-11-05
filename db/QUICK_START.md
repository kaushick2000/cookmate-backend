# Quick Reference - Apply Schema Changes

## 🚀 Fastest Way to Apply Changes

### Option 1: Update Existing Database (RECOMMENDED - No Data Loss)

```bash
# 1. Connect to MySQL
mysql -u root -p

# 2. Run the update script
source /Users/apple/Documents/Cookmate-Repo/cookmate-backend/db/update_existing_database.sql

# 3. Exit
exit
```

✅ **Safe:** Only adds new columns and constraints  
✅ **Fast:** Takes ~1 second  
✅ **Data:** All existing data preserved

---

### Option 2: Fresh Database (Development Only)

⚠️ **WARNING: Deletes all data!**

```bash
mysql -u root -p < /Users/apple/Documents/Cookmate-Repo/cookmate-backend/db/cookmate_schema_enhanced.sql
```

---

## 🔍 Quick Verification

After applying changes, verify in MySQL:

```sql
USE cookmate;

-- Check new columns exist
DESCRIBE user_preferences;

-- Should see:
-- | cuisine_preferences | varchar(1000) | YES  |
-- | meal_types         | varchar(500)  | YES  |
```

---

## 🎯 Test Profile Update

1. **Start Backend:**
   ```bash
   cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend
   java -jar target/cookmate-backend-0.0.1-SNAPSHOT.jar
   ```

2. **Start Frontend:**
   ```bash
   cd /Users/apple/Documents/Cookmate-Repo/cookmate-frontend
   npm run dev
   ```

3. **Test:**
   - Login → Profile → Edit
   - Select cuisines and meal types
   - Save
   - Check database:
     ```sql
     SELECT cuisine_preferences, meal_types FROM user_preferences WHERE user_id = 1;
     ```

---

## 📁 Files You Need

| File | Purpose | When to Use |
|------|---------|-------------|
| `update_existing_database.sql` | ⭐ Add new columns only | **Use this first** |
| `cookmate_schema_enhanced.sql` | Full schema with sample data | Fresh setup |
| `cookmate_schema_aligned.sql` | Base version (no extras) | Minimal changes |
| `MIGRATION_GUIDE.md` | Detailed instructions | Read if issues |
| `README.md` | Complete overview | Reference |

---

## ✅ Checklist

- [ ] Backup database (optional but recommended)
- [ ] Run `update_existing_database.sql`
- [ ] Restart backend server
- [ ] Test profile update in UI
- [ ] Verify data in MySQL

---

## 🆘 Issues?

### "Column already exists" error
✅ **Safe to ignore** - Script checks before adding

### "Profile updated successfully" but no data in DB
🔍 **Check:** Are you looking at database `cookmate` (not `cookmate_db`)?

### Backend won't start
🔧 **Fix:** Check MySQL is running and application.properties is correct

---

## 📞 What Changed

✨ **New Features:**
- Profile now saves cuisine preferences (e.g., Italian, Thai)
- Profile now saves meal types (e.g., Breakfast, Dinner)
- Prevents duplicate dietary restrictions per user

🔧 **Backend Updates:**
- `UserPreferences.java` - Added 2 new fields
- `AuthService.java` - Added persistence logic

📊 **Database Updates:**
- `user_preferences` table - Added 2 new columns
- Lookup tables populated with sample data

---

**That's it! Choose Option 1 above and you're done in 30 seconds.** 🎉
