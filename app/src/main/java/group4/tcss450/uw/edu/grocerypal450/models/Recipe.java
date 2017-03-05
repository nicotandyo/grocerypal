
package group4.tcss450.uw.edu.grocerypal450.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class will be used to model a recipe returned as JSON
 * from a Yummly API call to a Java object for handling within the app.
 */
public class Recipe implements Serializable {

    /**
     * Name of the recipe.
     */
    public String mRecipeName;
    /**
     * ID of the recipe from Yummly.
     */
    public String mRecipeId;
    /**
     * List of ingredients associated with this recipe.
     */
    public ArrayList<String> mIngredients;
    /**
     * Image URL to load image from.
     */
    public String mImage;


    public boolean isFavorite;

    public Date mDate;

    public int mNumServings;

    public int mTotalTime;

    public String mCuisine;

    public float mRating;



    /**
     * Construct empty Recipe object.
     */
    public Recipe () {

    }

    /**
     * Return the recipe's name.
     * @return String recipeName
     */
    public String getRecipeName() {
        return mRecipeName;
    }

    /**
     * Return the recipe's id.
     * @return String recipeId.
     */
    public String getRecipeId() {
        return mRecipeId;
    }

    /**
     * Return list of ingredients for this recipe.
     * @return ArrayList<String> ingredients
     */
    public ArrayList<String> getIngredients() {
        return mIngredients;
    }

    /**
     * Return image url.
     * @return String imageurl.
     */
    public String getImgUrl() {
        return mImage;
    }

    public boolean getIsFav() { return isFavorite; }
    /**
     * Set the recipe name.
     * @param recipeName
     */
    public void setRecipeName(String recipeName) {
        mRecipeName = recipeName;
    }

    /**
     * Set the recipe id.
     * @param recipeId
     */
    public void setRecipeId(String recipeId) {
        mRecipeId = recipeId;
    }

    /**
     * Set the ingredient's associated with this recipe.
     * @param recipeIngredients
     */
    public void setIngredients(ArrayList<String> recipeIngredients) {
        mIngredients = recipeIngredients;
    }

    /**
     * Set the image url.
     * @param imageUrl
     */
    public void setImage(String imageUrl) {
        mImage = imageUrl;
    }

    public void setIsFav(boolean tf) {
        isFavorite = tf;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public void setNumServings(int num) {
        mNumServings = num;
    }

    public int getNumServings() {
        return mNumServings;
    }

    public void setTotalTime(int time) {
        mTotalTime = time;
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    public void setCuisine(String cuisine) {
        mCuisine = cuisine;
    }

    public String getCuisine() {
        return mCuisine;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public float getRating() {
        return mRating;
    }

    @Override
    public String toString() {
        if(mIngredients != null) {
            return mRecipeName + " ID:" + mRecipeId + " \nIngredients:" + mIngredients.toString();
        } else {
            return mRecipeName + " ID:" + mRecipeId;
        }
    }

}

