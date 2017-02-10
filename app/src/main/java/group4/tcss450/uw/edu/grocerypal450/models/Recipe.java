package group4.tcss450.uw.edu.grocerypal450.models;

import java.util.ArrayList;

/**
 * This class will be used to model a recipe returned as JSON
 * from a Yummly API call to a Java object for handling within the app.
 */
public class Recipe {

    public String mRecipeName;
    public String mRecipeId;
    public ArrayList<String> mIngredients;
    public String mImage;

    public Recipe () {

    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public ArrayList<String> getIngredients() {
        return mIngredients;
    }

    public String getImgUrl() {
        return mImage;
    }

    public void setRecipeName(String recipeName) {
        mRecipeName = recipeName;
    }

    public void setRecipeId(String recipeId) {
        mRecipeId = recipeId;
    }

    public void setIngredients(ArrayList<String> recipeIngredients) {
        mIngredients = recipeIngredients;
    }

    public void setImage(String imageUrl) {
        mImage = imageUrl;
    }
}
