package group4.tcss450.uw.edu.grocerypal450.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerFragment extends Fragment {

    /** The TAG for the ShoppingListFragment. */
    public static final String TAG = "PlannerFragment";

    private GroceryDB mDB;

    private List<Recipe> mPlanner;

    private TextView mPlaceholder;
    /**
     * This is the constructor.
     */
    public PlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlanner = new ArrayList<Recipe>();
        mDB = ((ProfileActivity)getActivity()).getDB();
        //get all favorites and add to list
        List<Recipe> allRecipes = mDB.getRecipes();
        for(int i = 0; i < allRecipes.size(); i++) {
            Recipe r = allRecipes.get(i);
            /*
            if(r.getDate() != null) {
                mPlanner.add(r);
                //debug print
                System.out.println(r.toString());
            }
            */
        }
    }

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflater.inflate(R.layout.fragment_inventory, container, false)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_planner, container, false);
        mPlaceholder = (TextView) getActivity().findViewById(R.id.planner_placeholder);
        mPlaceholder.setText("Planner goes here.");
        return v;
    }

}
