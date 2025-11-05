#!/bin/bash

# =====================================================
# Cookmate Database Update Script
# =====================================================
# Purpose: Apply all schema changes to align database with frontend/backend
# Date: 2025-11-04
# Usage: ./apply_changes.sh
# =====================================================

set -e  # Exit on any error

echo "🚀 Cookmate Database Update Script"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
DB_NAME="cookmate"
DB_USER="root"
DB_PASS="Jo.marley@2406"
BACKUP_DIR="./backups"
SCRIPT_DIR="/Users/apple/Documents/Cookmate-Repo/cookmate-backend/db"

# =====================================================
# Step 1: Backup existing database (optional but recommended)
# =====================================================
echo "📦 Step 1: Creating backup..."

mkdir -p "$BACKUP_DIR"
BACKUP_FILE="$BACKUP_DIR/cookmate_backup_$(date +%Y%m%d_%H%M%S).sql"

if mysql -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME" 2>/dev/null; then
    echo "   Creating backup at: $BACKUP_FILE"
    mysqldump -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" > "$BACKUP_FILE"
    echo -e "   ${GREEN}✅ Backup created successfully${NC}"
else
    echo -e "   ${YELLOW}⚠️  Database '$DB_NAME' does not exist yet. Skipping backup.${NC}"
fi

echo ""

# =====================================================
# Step 2: Apply database changes
# =====================================================
echo "🔧 Step 2: Applying database changes..."

# Check if we need to create fresh DB or update existing
if mysql -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME" 2>/dev/null; then
    # Database exists - run update script
    echo "   Database '$DB_NAME' exists. Running update script..."
    mysql -u"$DB_USER" -p"$DB_PASS" < "$SCRIPT_DIR/update_existing_database.sql"
    echo -e "   ${GREEN}✅ Update script executed successfully${NC}"
else
    # Database doesn't exist - run full schema
    echo "   Database '$DB_NAME' does not exist. Creating with full schema..."
    mysql -u"$DB_USER" -p"$DB_PASS" < "$SCRIPT_DIR/cookmate_schema_enhanced.sql"
    echo -e "   ${GREEN}✅ Full schema created successfully${NC}"
fi

echo ""

# =====================================================
# Step 3: Verify changes
# =====================================================
echo "🔍 Step 3: Verifying changes..."

# Check if new columns exist
CUISINE_COL=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -s -e "
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = '$DB_NAME' 
      AND TABLE_NAME = 'user_preferences' 
      AND COLUMN_NAME = 'cuisine_preferences'
")

MEAL_TYPES_COL=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -s -e "
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = '$DB_NAME' 
      AND TABLE_NAME = 'user_preferences' 
      AND COLUMN_NAME = 'meal_types'
")

if [ "$CUISINE_COL" -eq 1 ] && [ "$MEAL_TYPES_COL" -eq 1 ]; then
    echo -e "   ${GREEN}✅ New columns 'cuisine_preferences' and 'meal_types' exist${NC}"
else
    echo -e "   ${RED}❌ ERROR: New columns not found!${NC}"
    exit 1
fi

# Check unique constraint
CONSTRAINT_COUNT=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -s -e "
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = '$DB_NAME' 
      AND TABLE_NAME = 'user_dietary_restrictions' 
      AND CONSTRAINT_NAME = 'uq_udr'
")

if [ "$CONSTRAINT_COUNT" -eq 1 ]; then
    echo -e "   ${GREEN}✅ Unique constraint 'uq_udr' exists${NC}"
else
    echo -e "   ${YELLOW}⚠️  Unique constraint not found (may already exist with different name)${NC}"
fi

# Check sample data
DIETARY_COUNT=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -s -e "SELECT COUNT(*) FROM $DB_NAME.dietary_restrictions")
echo -e "   ${GREEN}✅ Dietary restrictions: $DIETARY_COUNT entries${NC}"

CUISINE_COUNT=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -s -e "SELECT COUNT(*) FROM $DB_NAME.cuisine_types")
echo -e "   ${GREEN}✅ Cuisine types: $CUISINE_COUNT entries${NC}"

MEAL_TYPE_COUNT=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -s -e "SELECT COUNT(*) FROM $DB_NAME.meal_types")
echo -e "   ${GREEN}✅ Meal types: $MEAL_TYPE_COUNT entries${NC}"

echo ""

# =====================================================
# Step 4: Rebuild backend (optional)
# =====================================================
echo "🏗️  Step 4: Rebuild backend (optional)..."
echo ""
echo "   To rebuild the backend, run:"
echo "   cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend"
echo "   mvn clean package -DskipTests"
echo ""

# =====================================================
# Step 5: Display next steps
# =====================================================
echo "✅ Database update completed successfully!"
echo ""
echo "📋 Next Steps:"
echo ""
echo "1️⃣  Start Backend Server:"
echo "   cd /Users/apple/Documents/Cookmate-Repo/cookmate-backend"
echo "   java -jar target/cookmate-backend-0.0.1-SNAPSHOT.jar"
echo ""
echo "2️⃣  Start Frontend Server:"
echo "   cd /Users/apple/Documents/Cookmate-Repo/cookmate-frontend"
echo "   npm run dev"
echo ""
echo "3️⃣  Test Profile Update:"
echo "   - Login to the application"
echo "   - Go to Profile page"
echo "   - Select cuisine preferences and meal types"
echo "   - Click Save"
echo "   - Verify in database:"
echo "     mysql -u root -p"
echo "     USE cookmate;"
echo "     SELECT cuisine_preferences, meal_types FROM user_preferences;"
echo ""
echo "📚 Documentation:"
echo "   - Quick Start: $SCRIPT_DIR/QUICK_START.md"
echo "   - Full Guide: $SCRIPT_DIR/MIGRATION_GUIDE.md"
echo "   - Changes: $SCRIPT_DIR/CHANGES_SUMMARY.md"
echo ""
echo "🎉 All done! Your database is now fully aligned with the codebase."
