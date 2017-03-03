
package group4.tcss450.uw.edu.grocerypal450.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;


public class GroceryDB {

    public static final int DB_VERSION = 1;
    private final String DB_NAME;
    private final String INGREDIENT_TABLE;
    private final String RECIPE_TABLE;
    private final String[] INGREDIENT_COLUMN_NAMES;
    private final String[] RECIPE_COLUMN_NAMES;

    private IngredientDBHelper mIngredientDBHelper;
    private RecipeDBHelper mRecipeDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public GroceryDB(Context context) {

        INGREDIENT_COLUMN_NAMES = context.getResources().getStringArray(R.array.DB_INGREDIENT_NAMES);
        RECIPE_COLUMN_NAMES = context.getResources().getStringArray(R.array.DB_RECIPE_NAMES);

        DB_NAME = context.getString(R.string.DB_NAME);

        INGREDIENT_TABLE = context.getString(R.string.INGREDIENT_TABLE);
        RECIPE_TABLE = context.getString(R.string.RECIPE_TABLE);

        mIngredientDBHelper = new IngredientDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mIngredientDBHelper.getWritableDatabase();

        mRecipeDBHelper = new RecipeDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mRecipeDBHelper.getWritableDatabase();
    }

    public boolean insertIngredient(String ingredient, int quantity, int isInventory) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(INGREDIENT_COLUMN_NAMES[0], ingredient);
        contentValues.put(INGREDIENT_COLUMN_NAMES[1], quantity);
        contentValues.put(INGREDIENT_COLUMN_NAMES[2], isInventory);
        Log.d("CREATE ITEM", "WORKS");

        long rowId = mSQLiteDatabase.insert(INGREDIENT_TABLE, null, contentValues);
        return rowId != -1;
    }

    public boolean insertRecipe(Recipe recipe) {
        ContentValues contentValues = new ContentValues();
        int isFavorite = recipe.getIsFav() ? 1 : 0;

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
        contentValues.put(RECIPE_COLUMN_NAMES[9], recipe.getDate().toString());
        Log.d("CREATE RECIPE", "WORKS");

        long rowId = mSQLiteDatabase.insert(RECIPE_TABLE, null, contentValues);
        return rowId != -1;
    }



    public void closeDB() {
        mSQLiteDatabase.close();
    }


    /**
     * Increment item quantity by 1.
     * @param ingredient
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
        int numRow = mSQLiteDatabase.update(INGREDIENT_TABLE, cv, "ingredient = '" + ingredient.getIngredient().toLowerCase() + "' AND isInventory = " + type,
                null);
        return numRow > 0;

    }

    /**
     * Decrement quantity by 1.
     * @param ingredient
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
        int numRow = mSQLiteDatabase.update(INGREDIENT_TABLE, cv, "ingredient = '" + ingredient.getIngredient().toLowerCase() + "' AND isInventory = " + type,
                null);
        return numRow > 0;
    }

    /**
     * Delete item in the shopping list.
     * @param ingredient
     * @return
     */
    public boolean deleteItemShoplist(String ingredient) {
        Log.d("DELETE ITEM", "WORKS");
        return mSQLiteDatabase.delete(INGREDIENT_TABLE, "ingredient = ? AND isInventory = 0 ",
                new String[] {String.valueOf(ingredient)}) > 0;
    }

    /**
     * Delete item in the inventory list.
     * @param  ingredient
     * @return true if item deleted
     */
    public boolean deleteItemInventory(String ingredient) {
        Log.d("DELETE ITEM", "WORKS");
        return mSQLiteDatabase.delete(INGREDIENT_TABLE, "ingredient = ? AND isInventory = 1 ",
                new String[] {String.valueOf(ingredient)}) > 0;
    }

    /**

     * Move item from the shopping list to the inventory.
     * @param ingredient
     * @return
     */
    public boolean moveItemShoplistToInven(Ingredient ingredient) {
        ContentValues cv = new ContentValues();
        cv.put("isInventory", 1);
        Log.d("MOVE TO INVEN ", "WORKS");
        int numRow = mSQLiteDatabase.update(INGREDIENT_TABLE, cv, "ingredient = '"
                + ingredient.getIngredient().toLowerCase() + "' AND quantity = " +
                ingredient.getQuantity(),
                null);
        return numRow > 0;
    }

    /**
     * Move the item from the inventory to the shopping list.
     * @param ingredient
     * @return
     */
    public boolean moveItemInvenToShoplist(Ingredient ingredient) {
        ContentValues cv = new ContentValues();
        cv.put("isInventory", 0);
        Log.d("MOVE TO SHOPLIST ", "WORKS");
        int numRow = mSQLiteDatabase.update(INGREDIENT_TABLE, cv, "ingredient = '"
                        + ingredient.getIngredient().toLowerCase() + "' AND quantity = " +
                        ingredient.getQuantity(),
                null);
        return numRow > 0;
    }

    /**

     * Delete all item in the shopping list.
     */
    public void deleteAllShoplist() {
        Log.d("CLEAR ALL", "WORKS");
        mSQLiteDatabase.delete(INGREDIENT_TABLE, "isInventory = 0 ", null);
    }

    /**
     * Delete all item in the inventory list.
     */
    public void deleteAllInventory() {
        Log.d("CLEAR ALL", "WORKS");
        mSQLiteDatabase.delete(INGREDIENT_TABLE, "isInventory = 1 ", null);
    }

    /**
     * Returns the list of Ingredient objects from the local Ingredients table.
     * @return list
     */
    public List<Ingredient> getIngredients() {


        Cursor c = mSQLiteDatabase.query(
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

    public List<Recipe> getRecipes() {


        Cursor c = mSQLiteDatabase.query(
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
//            String name = c.getString(0);
//            String recipeId = c.getString(1);
//            String ingredients = c.getString(2);
//            String img = c.getString(3);
//            int isFav = c.getInt(4);
//            String date = c.getString(5);

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
            list.add(recipe);

            c.moveToNext();
        }
        c.close();
        return list;
    }

    //helper method to convert CSV ingredients to ArrayList
    private static ArrayList<String> stringToList(final String input) {
        String[] elements = input.substring(1, input.length() - 1).split(",");
        ArrayList<String> result = new ArrayList<String>(elements.length);
        for (String item : elements) {
            result.add(item);
        }
        return result;
    }

    public boolean updateFavorite(Recipe recipe) {
        ContentValues cv = new ContentValues();
        int isFavorite = recipe.getIsFav() ? 1 : 0;
        cv.put("isFavorite", isFavorite);
        int numRow = mSQLiteDatabase.update(RECIPE_TABLE, cv, "recipeId = '" + recipe.getRecipeId() + "'", null);
        return numRow > 0;
    }

    public boolean updateDate(Recipe recipe) {
        ContentValues cv = new ContentValues();
        String date = recipe.getDate().toString();
        cv.put("date", date);
        int numRow = mSQLiteDatabase.update(RECIPE_TABLE, cv, "recipeId = '" + recipe.getRecipeId() + "'", null);
        return numRow > 0;
    }

    class IngredientDBHelper extends SQLiteOpenHelper {

        private final String CREATE_INGREDIENTS_SQL;

        private final String DROP_INGREDIENTS_SQL;

        public IngredientDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_INGREDIENTS_SQL = context.getString(R.string.CREATE_INGREDIENTS_SQL);
            DROP_INGREDIENTS_SQL = context.getString(R.string.DROP_INGREDIENTS_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_INGREDIENTS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_INGREDIENTS_SQL);
            onCreate(sqLiteDatabase);
        }
    }

    class RecipeDBHelper extends SQLiteOpenHelper {

        private final String CREATE_RECIPES_SQL;

        private final String DROP_RECIPES_SQL;

        public RecipeDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_RECIPES_SQL = context.getString(R.string.CREATE_RECIPES_SQL);
            DROP_RECIPES_SQL = context.getString(R.string.DROP_RECIPES_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_RECIPES_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_RECIPES_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}

