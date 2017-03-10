
package group4.tcss450.uw.edu.grocerypal450.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;

/**
 * The class for the Ingredient and Recipe table.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class GroceryDB {
    /** The version of the DB. */
    public static final int DB_VERSION = 3;
    /** The database name. */
    private final String DB_NAME;
    /** The ingredient table name. */
    private final String INGREDIENT_TABLE;
    /** The recipe table name. */
    private final String RECIPE_TABLE;
    /** The ingredient column names. */
    private final String[] INGREDIENT_COLUMN_NAMES;
    /** The recipe column names. */
    private final String[] RECIPE_COLUMN_NAMES;
    /** The username. */
    private String mUsername;
    /** Helper for GroceryDB. */
    private GroceryDBHelper mDBHelper;
    /** The SQLiteDatabase. */
    private SQLiteDatabase mDB;

    /**
     * The constructor for the GroceryDB class.
     * @param context is the contexy
     * @param user is the user
     */
    public GroceryDB(Context context, final String user) {

        INGREDIENT_COLUMN_NAMES = context.getResources().getStringArray(R.array.DB_INGREDIENT_NAMES);
        RECIPE_COLUMN_NAMES = context.getResources().getStringArray(R.array.DB_RECIPE_NAMES);
        mUsername = user;

        DB_NAME = mUsername + "_" + context.getString(R.string.DB_NAME);

        INGREDIENT_TABLE = mUsername + "_" + context.getString(R.string.INGREDIENT_TABLE);
        RECIPE_TABLE = mUsername + "_" + context.getString(R.string.RECIPE_TABLE);

        mDBHelper = new GroceryDBHelper(
                context, DB_NAME, null, DB_VERSION, mUsername);
        mDB = mDBHelper.getWritableDatabase();
    }

    /**
     * Insert new ingredient to the ingredient table.
     * @param ingredient is the ingredient
     * @param quantity is the quantity
     * @param isInventory is the inventory
     * @return true or false
     */
    public boolean insertIngredient(String ingredient, int quantity, int isInventory) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(INGREDIENT_COLUMN_NAMES[0], ingredient);
        contentValues.put(INGREDIENT_COLUMN_NAMES[1], quantity);
        contentValues.put(INGREDIENT_COLUMN_NAMES[2], isInventory);
        Log.d("CREATE ITEM", "WORKS");

        long rowId = mDB.insert(INGREDIENT_TABLE, null, contentValues);
        return rowId != -1;
    }

    /**
     * Insert new recipe to the recipe table.
     * @param recipe is the recipe
     * @return true or false
     */
    public boolean insertRecipe(Recipe recipe) {
        ContentValues contentValues = new ContentValues();
        int isFavorite = recipe.getIsFav() ? 1 : 0;

        int year = recipe.mDate.get(Calendar.YEAR);
        int month = recipe.mDate.get(Calendar.MONTH);
        int day = recipe.mDate.get(Calendar.DAY_OF_MONTH);
        StringBuilder str = new StringBuilder();
        str.append(year + "-" + month + "-" + day);

        contentValues.put(RECIPE_COLUMN_NAMES[0], recipe.getRecipeName());
        contentValues.put(RECIPE_COLUMN_NAMES[1], recipe.getRecipeId());
        //store ingredients as CSV
        contentValues.put(RECIPE_COLUMN_NAMES[2], android.text.TextUtils.join(",", recipe.getIngredients()));
        contentValues.put(RECIPE_COLUMN_NAMES[3], recipe.getImgUrl());
        contentValues.put(RECIPE_COLUMN_NAMES[4], recipe.getNumServings());
        contentValues.put(RECIPE_COLUMN_NAMES[5], recipe.getTotalTime());
        contentValues.put(RECIPE_COLUMN_NAMES[6], recipe.getCuisine());
        contentValues.put(RECIPE_COLUMN_NAMES[7], recipe.getRating());
        contentValues.put(RECIPE_COLUMN_NAMES[8], isFavorite);
        contentValues.put(RECIPE_COLUMN_NAMES[9], str.toString());
        Log.d("CREATE RECIPE", "WORKS");

        long rowId = mDB.insert(RECIPE_TABLE, null, contentValues);
        return rowId != -1;
    }

    /**
     * Remove recipe from the recipe table.
     * @param recipe is the recipe
     * @return true or false
     */
    public boolean removeRecipe(Recipe recipe) {
        return mDB.delete(RECIPE_TABLE, "recipeId = ? ",
                new String[] {String.valueOf(recipe.getRecipeId())}) > 0;

    }


    /**
     * Close the database.
     */
    public void closeDB() {
        mDB.close();
    }


    /**
     * Increment item quantity by 1.
     * @param ingredient is the ingredient
     * @return true if incremented
     */
    public boolean incrementIngredient(Ingredient ingredient) {
        ContentValues cv = new ContentValues();
        int type = 0;
        if(ingredient.isInventory()) {
            type = 1;
        }
        cv.put("quantity", ingredient.getQuantity() + 1);
        System.out.println("Increment: " + ingredient.getQuantity());
        //Log.d("INCREMENT", "WORKS");
        int numRow = mDB.update(INGREDIENT_TABLE, cv, "ingredient = '" + ingredient.getIngredient().toLowerCase() + "' AND isInventory = " + type,
                null);
        return numRow > 0;

    }

    /**
     * Decrement quantity by 1.
     * @param ingredient is the ingredient
     * @return true if decremented
     */
    public boolean decrementIngredient(Ingredient ingredient) {
        ContentValues cv = new ContentValues();
        cv.put("quantity", ingredient.getQuantity() - 1);
        int type = 0;
        if(ingredient.isInventory()) {
            type = 1;
        }
        //Log.d("DECREMENT 1", "WORKS");
        int numRow = mDB.update(INGREDIENT_TABLE, cv, "ingredient = '" + ingredient.getIngredient().toLowerCase() + "' AND isInventory = " + type,
                null);
        return numRow > 0;
    }

    /**
     * Delete item in the shopping list.
     * @param ingredient is the ingredient
     * @return true or false
     */
    public boolean deleteItemShoplist(String ingredient) {
        Log.d("DELETE ITEM", "WORKS");
        return mDB.delete(INGREDIENT_TABLE, "ingredient = ? AND isInventory = 0 ",
                new String[] {String.valueOf(ingredient)}) > 0;
    }

    /**
     * Delete item in the inventory list.
     * @param  ingredient is the ingredient
     * @return true if item deleted, false otherwise
     */
    public boolean deleteItemInventory(String ingredient) {
        Log.d("DELETE ITEM", "WORKS");
        return mDB.delete(INGREDIENT_TABLE, "ingredient = ? AND isInventory = 1 ",
                new String[] {String.valueOf(ingredient)}) > 0;
    }

    /**

     * Move item from the shopping list to the inventory.
     * @param ingredient is the ingredient
     * @return true or false
     */
    public boolean moveItemShoplistToInven(Ingredient ingredient) {
        int numRow;
        ContentValues cv = new ContentValues();
        cv.put("isInventory", 1);

        String query = "SELECT quantity FROM " + mUsername +"_Ingredients WHERE ingredient = '" + ingredient.getIngredient()
                + "' AND isInventory = 1";
        Cursor  cursor = mDB.rawQuery(query,null);
        if(!(cursor.moveToFirst()) || cursor.getCount() ==0) {
            numRow = mDB.update(INGREDIENT_TABLE, cv, "ingredient = '"
                            + ingredient.getIngredient().toLowerCase() + "' AND quantity = " + ingredient.getQuantity(),
                    null);

        } else {
            cursor.moveToFirst();
            int quantityc = cursor.getInt(0);
            int quantity = ingredient.getQuantity();
            int quantityt = quantityc + quantity;

            ContentValues cv1 = new ContentValues();
            Log.d("Q", quantityt + "");
            cv1.put("quantity", quantityt);
            numRow = mDB.update(INGREDIENT_TABLE, cv1, "ingredient = '"
                            + ingredient.getIngredient().toLowerCase() + "' AND isInventory = 1",
                    null);
            deleteItemShoplist(ingredient.getIngredient());
        }
        cursor.close();





        return numRow > 0;
    }

    /**
     * Move the item from the inventory to the shopping list.
     * @param ingredient is the ingredient
     * @return true or false
     */
    public boolean moveItemInvenToShoplist(Ingredient ingredient) {
        int numRow;
        ContentValues cv = new ContentValues();
        cv.put("isInventory", 0);

        String query = "SELECT quantity FROM " + mUsername + "_Ingredients WHERE ingredient = '" + ingredient.getIngredient()
                + "' AND isInventory = 0";
        Cursor  cursor = mDB.rawQuery(query,null);
        if(!(cursor.moveToFirst()) || cursor.getCount() ==0) {
            numRow = mDB.update(INGREDIENT_TABLE, cv, "ingredient = '"
                            + ingredient.getIngredient().toLowerCase() + "' AND quantity = " + ingredient.getQuantity(),
                    null);

        } else {
            cursor.moveToFirst();
            int quantityc = cursor.getInt(0);
            int quantity = ingredient.getQuantity();
            int quantityt = quantityc + quantity;

            ContentValues cv1 = new ContentValues();
            Log.d("Q", quantityt + "");
            cv1.put("quantity", quantityt);
            numRow = mDB.update(INGREDIENT_TABLE, cv1, "ingredient = '"
                            + ingredient.getIngredient().toLowerCase() + "' AND isInventory = 0",
                    null);
            deleteItemInventory(ingredient.getIngredient());
        }
        cursor.close();
        return numRow > 0;
    }

    /**
     * Delete all item in the shopping list.
     */
    public void deleteAllShoplist() {
        Log.d("CLEAR ALL", "WORKS");
        mDB.delete(INGREDIENT_TABLE, "isInventory = 0 ", null);
    }

    /**
     * Delete all item in the inventory list.
     */
    public void deleteAllInventory() {
        Log.d("CLEAR ALL", "WORKS");
        mDB.delete(INGREDIENT_TABLE, "isInventory = 1 ", null);
    }

    /**
     * Returns the list of Ingredient objects from the local Ingredients table.
     * @return list
     */
    public List<Ingredient> getIngredients() {


        Cursor c = mDB.query(
                INGREDIENT_TABLE,  // The table to query
                INGREDIENT_COLUMN_NAMES,                               // The INGREDIENT_COLUMN_NAMES to return
                null,                                // The INGREDIENT_COLUMN_NAMES for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Ingredient> list = new ArrayList<Ingredient>();
        for (int i=0; i<c.getCount(); i++) {
            String ingredient = c.getString(0);
            int quantity = c.getInt(1);
            int isInventory = c.getInt(2);
            list.add(new Ingredient(ingredient, quantity, isInventory));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    /**
     * Returns a list of Recipe objects from the local Recipe table.
     * @return list
     */
    public List<Recipe> getRecipes() {


        Cursor c = mDB.query(
                RECIPE_TABLE,  // The table to query
                RECIPE_COLUMN_NAMES,                      // The RECIPE_COLUMN_NAMES to return
                null,                                // The RECIPE_COLUMN_NAMES for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Recipe> list = new ArrayList<Recipe>();
        for (int i=0; i<c.getCount(); i++) {
            Recipe recipe = new Recipe();
            recipe.setRecipeName(c.getString(0));
            recipe.setRecipeId(c.getString(1));
            recipe.setIngredients(stringToList(c.getString(2)));
            recipe.setImage(c.getString(3));
            recipe.setNumServings(c.getInt(4));
            recipe.setTotalTime(c.getInt(5));
            recipe.setCuisine(c.getString(6));
            recipe.setRating(c.getFloat(7));
            recipe.setIsFav((c.getInt(8) == 1) ? true : false);
            String[] date = c.getString(9).split("-");
            recipe.setDate(new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])));
            list.add(recipe);

            c.moveToNext();
        }
        c.close();
        return list;
    }


    /**
     * helper method to convert CSV ingredients to ArrayList
     * @param input is the string input
     * @return the result
     */
    private static ArrayList<String> stringToList(final String input) {
        String[] elements = input.substring(1, input.length() - 1).split(",");
        ArrayList<String> result = new ArrayList<String>(elements.length);
        for (String item : elements) {
            result.add(item);
        }
        return result;
    }

    /**
     * Update the favorite.
     * @param recipe is the recipe
     * @return true or false
     */
    public boolean updateFavorite(Recipe recipe) {
        ContentValues cv = new ContentValues();
        int isFavorite = recipe.getIsFav() ? 1 : 0;
        cv.put("isFavorite", isFavorite);
        int numRow = mDB.update(RECIPE_TABLE, cv, "recipeId = '" + recipe.getRecipeId() + "'", null);
        return numRow > 0;
    }

    /**
     * Update the date.
     * @param recipe is the recipe
     * @return true or false
     */
    public boolean updateDate(Recipe recipe) {
        ContentValues cv = new ContentValues();

        int year = recipe.mDate.get(Calendar.YEAR);
        int month = recipe.mDate.get(Calendar.MONTH);
        int day = recipe.mDate.get(Calendar.DAY_OF_MONTH);
        StringBuilder str = new StringBuilder();
        str.append(year + "-" + month + "-" + day);
        cv.put("date", str.toString());
        int numRow = mDB.update(RECIPE_TABLE, cv, "recipeId = '" + recipe.getRecipeId() + "'", null);
        return numRow > 0;
    }

    /**
     * Check if a given recipe's ID exists within the Recipes table.
     * @param recipe
     * @return
     */
    public boolean isRecipeExist(Recipe recipe) {
        String query = "SELECT * FROM " + mUsername + "_Recipes WHERE recipeId = '" + recipe.getRecipeId() + "'";
        Cursor  cursor = mDB.rawQuery(query,null);
        boolean exist = cursor.moveToFirst();
        cursor.close();
        return exist;
    }

    /**
     * Helper class for the GroceryDB.
     */
    class GroceryDBHelper extends SQLiteOpenHelper {
        /** Create ingredients table. */
        private final String CREATE_INGREDIENTS_SQL;
        /** Drop ingredients table. */
        private final String DROP_INGREDIENTS_SQL;
        /** Create recipes table. */
        private final String CREATE_RECIPES_SQL;
        /** Drop recipes table. */
        private final String DROP_RECIPES_SQL;

        /**
         * Constructor for the GroceryDBHelper.
         * @param context is the app context
         * @param name is the name
         * @param factory is the CursorFactory
         * @param version is the version
         * @param user is the user
         */
       public GroceryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, String user) {
            super(context, name, factory, version);
            CREATE_INGREDIENTS_SQL = String.format(context.getString(R.string.CREATE_INGREDIENTS_SQL), user);
            DROP_INGREDIENTS_SQL = String.format(context.getString(R.string.DROP_INGREDIENTS_SQL), user);
            CREATE_RECIPES_SQL = String.format(context.getString(R.string.CREATE_RECIPES_SQL), user);
            DROP_RECIPES_SQL = String.format(context.getString(R.string.DROP_RECIPES_SQL), user);

        }

        /**
         * {@inheritDoc}
         * @param sqLiteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_INGREDIENTS_SQL);
            sqLiteDatabase.execSQL(CREATE_RECIPES_SQL);
        }

        /**
         * {@inheritDoc}
         * @param sqLiteDatabase
         * @param i
         * @param i1
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_INGREDIENTS_SQL);
            sqLiteDatabase.execSQL(DROP_RECIPES_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}

