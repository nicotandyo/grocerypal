package group4.tcss450.uw.edu.grocerypal450.Interface;

/**
 * Custom interface for the Favorites, and MealPlanner.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public interface MyCustomInterface {
    /**
     * Handles the click on the Favorites button.
     * @param position is the position
     */
    void onFavClicked(int position);

    /**
     * Handles the click on the Meal Planner button.
     * @param position is the position
     */
    void onPlannerClicked(int position, boolean tf);
}
