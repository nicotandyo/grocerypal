
package group4.tcss450.uw.edu.grocerypal450.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class will be used to model a recipe returned as JSON
 * from a Yummly API call to a Java object for handling within the app.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
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
    /**
     * True if isFavorite, false otherwise.
     */
    public boolean isFavorite;

    /**
     * The date.
     */
    public Calendar mDate;
    /**
     * The number of servings.
     */
    public int mNumServings;
    /**
     * The total time required.
     */
    public int mTotalTime;
    /**
     * The cuisine type.
     */
    public String mCuisine;
    /**
     * The rating.
     */
    public float mRating;
    /**
     * The url of the recipe.
     */
    public String mRecipeUrl;



    /**
     * Construct empty Recipe object.
     */
    public Recipe () {
        //set to not show in planner or favorites by default
        mDate = new GregorianCalendar(1900, 1, 1);
        isFavorite = false;
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

    /**
     * Get the favorites status.
     * @return true or false
     */
    public boolean getIsFav() { return isFavorite; }
    /**
     * Set the recipe name.
     * @param recipeName is the recipe name
     */
    public void setRecipeName(String recipeName) {
        mRecipeName = recipeName;
    }

    /**
     * Set the recipe id.
     * @param recipeId is the recipe id
     */
    public void setRecipeId(String recipeId) {
        mRecipeId = recipeId;
    }

    /**
     * Set the ingredient's associated with this recipe.
     * @param recipeIngredients is the recipe ingredients
     */
    public void setIngredients(ArrayList<String> recipeIngredients) {
        mIngredients = recipeIngredients;
    }

    /**
     * Set the image url.
     * @param imageUrl is the url of the image
     */
    public void setImage(String imageUrl) {
        mImage = imageUrl;
    }

    /**
     * Set the favorites status.
     * @param tf is the status
     */
    public void setIsFav(boolean tf) { isFavorite = tf; }

    /**
     * Set the date.
     * @param date is the date
     */
    public void setDate(Calendar date) {
        mDate = date;
    }

    /**
     * Get the date.
     * @return a calendar representing the date
     */
    public Calendar getDate() { return mDate; }

    /**
     * Set the number of servings of a recipe.
     * @param num is the number of servings
     */
    public void setNumServings(int num) {
        mNumServings = num;
    }

    /**
     * Get the number of servings.
     * @return the number of servings
     */
    public int getNumServings() {
        return mNumServings;
    }

    /**
     * Set the total time required to make a recipe.
     * @param time is the required time in seconds
     */
    public void setTotalTime(int time) {
        mTotalTime = time;
    }

    /**
     * Get the total time required to make a recipe.
     * @return the total time in seconds
     */
    public int getTotalTime() {
        return mTotalTime;
    }

    /**
     * Set the cuisine type.
     * @param cuisine is the cuisine
     */
    public void setCuisine(String cuisine) {
        mCuisine = cuisine;
    }

    /**
     * Get the cuisine type.
     * @return the cuisine type in String
     */
    public String getCuisine() {
        return mCuisine;
    }

    /**
     * Set the rating of a recipe.
     * @param rating is the rating
     */
    public void setRating(float rating) {
        mRating = rating;
    }

    /**
     * Get the rating of a recipe.
     * @return the rating
     */
    public float getRating() {
        return mRating;
    }

    /**
     * Set the URL of the recipe.
     * @param url is the url of the recipe
     */
    public void setRecipeUrl(String url) {
        mRecipeUrl = url;
    }

    /**
     * Get the recipe URL.
     * @return the recipe URL.
     */
    public String getRecipeUrl() {
        return mRecipeUrl;
    }

    /**
     * Get the String representation of the recipe.
     * @return the String representation
     */
    @Override
    public String toString() {
        return mRecipeName + " ID:" + mRecipeId + " \nIngredients:" + mIngredients.toString();

    }



}

