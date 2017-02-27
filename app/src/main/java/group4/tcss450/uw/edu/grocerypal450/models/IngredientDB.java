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


public class IngredientDB {

    public static final int DB_VERSION = 1;
    private final String DB_NAME;
    private final String INGREDIENT_TABLE;
    private final String[] COLUMN_NAMES;

    private IngredientDBHelper mIngredientDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public IngredientDB(Context context) {

        COLUMN_NAMES = context.getResources().getStringArray(R.array.DB_COLUMN_NAMES);
        DB_NAME = context.getString(R.string.DB_NAME);
        INGREDIENT_TABLE = context.getString(R.string.INGREDIENT_TABLE);

        mIngredientDBHelper = new IngredientDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mIngredientDBHelper.getWritableDatabase();
    }

    public boolean insertIngredient(String ingredient, int quantity, int isInventory) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAMES[0], ingredient);
        contentValues.put(COLUMN_NAMES[1], quantity);
        contentValues.put(COLUMN_NAMES[2], isInventory);
        Log.d("CREATE ITEM", "WORKS");

        long rowId = mSQLiteDatabase.insert(INGREDIENT_TABLE, null, contentValues);
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
     * Delete all item in the shopping list.
     */
    public void deleteAllShoplist() {
        Log.d("CLEAR ALL", "WORKS");
        mSQLiteDatabase.delete(INGREDIENT_TABLE, "isInventory = 0 ", null);
    }

    /**
     * Returns the list of Ingredient objects from the local Ingredients table.
     * @return list
     */
    public List<Ingredient> getIngredients() {


        Cursor c = mSQLiteDatabase.query(
                INGREDIENT_TABLE,  // The table to query
                COLUMN_NAMES,                               // The COLUMN_NAMES to return
                null,                                // The COLUMN_NAMES for the WHERE clause
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

}

