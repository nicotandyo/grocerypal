package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import group4.tcss450.uw.edu.grocerypal450.R;

/**
 * This class will handle the result from the RecipeSearch class.
 */
public class RecipeResults extends Fragment {

    /**
     * TextView to show the results.
     */
    private TextView mResults;

    /**
     * This is the constructor.
     */
    public RecipeResults() {
        // Required empty public constructor
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
        View v = inflater.inflate(R.layout.fragment_recipe_results, container, false);
        mResults = (TextView) v.findViewById(R.id.resultsPlaceholder);
        String recipeString=getArguments().getString("jsonRecipes");
        //JSONObject recipeJson=new JSONObject(recipeString);
        mResults.setText(recipeString);
        return v;
    }
}
