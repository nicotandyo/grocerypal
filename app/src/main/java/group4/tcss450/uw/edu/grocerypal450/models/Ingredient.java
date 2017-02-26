package group4.tcss450.uw.edu.grocerypal450.models;

import java.io.Serializable;

/**
 * Encapsulates a tuple from the Ingredient table.
 */
public class Ingredient implements Serializable {

    private final String mIngredient;
    private final int mQuantity;
    private final boolean mIsInventory;

    //0 if shopping, 1 if inventory
    public Ingredient(String name, int quantity, int isInventory) {
        if(isInventory != 1 || isInventory != 0 || quantity < 0 || name.length() > 127 || name.length() < 2) {
            throw new IllegalArgumentException();
        }
        mIngredient = name.toLowerCase();
        mQuantity = quantity;
        mIsInventory = (isInventory == 1);
    }

    public String getIngredient() {
        return mIngredient;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public boolean isInventory() {
        return mIsInventory;
    }

}
