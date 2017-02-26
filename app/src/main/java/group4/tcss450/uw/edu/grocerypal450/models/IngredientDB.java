package group4.tcss450.uw.edu.grocerypal450.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public boolean insertColor(String ingredient, int quantity, int isInventory) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAMES[0], ingredient);
        contentValues.put(COLUMN_NAMES[1], quantity);
        contentValues.put(COLUMN_NAMES[2], isInventory);

        long rowId = mSQLiteDatabase.insert(INGREDIENT_TABLE, null, contentValues);
        return rowId != -1;
    }

    public void closeDB() {
        mSQLiteDatabase.close();
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
            String ingredient = c.getString(1);
            int quantity = c.getInt(2);
            int isInventory = c.getInt(3);
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

