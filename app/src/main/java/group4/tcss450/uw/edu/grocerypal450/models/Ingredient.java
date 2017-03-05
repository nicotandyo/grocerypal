
package group4.tcss450.uw.edu.grocerypal450.models;

import java.io.Serializable;

/**
 * Encapsulates a tuple from the Ingredient table.
 */
public class Ingredient implements Serializable {
    /** The ingredient. */
    private final String mIngredient;
    /** The quantity. */
    private final int mQuantity;
    /** The isInventory status. */
    private final boolean mIsInventory;

    /**
     * Constructor for the Ingredient class.
     * @param name is the name
     * @param quantity is the quantity
     * @param isInventory 0 if shopping, 1 if inventory
     */
    public Ingredient(String name, int quantity, int isInventory) {
        if((isInventory != 1 && isInventory != 0) || quantity < 0 || name.length() > 127 || name.length() < 1) {
            throw new IllegalArgumentException();
        }
        mIngredient = name.toLowerCase();
        mQuantity = quantity;
        mIsInventory = (isInventory == 1);
    }

    /**
     * Get the ingredient name.
     * @return the name
     */
    public String getIngredient() {
        return mIngredient;
    }

    /**
     * Get the ingredient quantity.
     * @return the quantity
     */
    public int getQuantity() {
        return mQuantity;
    }

    /**
     * Get the isInventory status
     * @return the isInventory
     */
    public boolean isInventory() {
        return mIsInventory;
    }

    /**
     * Get the string representation.
     * @return the string representation
     */
    @Override
    public String toString() {
        return mIngredient + " " + mQuantity + " " + mIsInventory;
    }

}

