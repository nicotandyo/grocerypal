package group4.tcss450.uw.edu.grocerypal450.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class FavoritesFragment extends Fragment {

    private GroceryDB mDB;
    private List<Recipe> mFavorites = new ArrayList<Recipe>();
    private TextView mPlaceholder;


    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaceholder = (TextView) getActivity().findViewById(R.id.favorites_placeholder);
        mDB = ((ProfileActivity)getActivity()).getDB();
        //get all favorites and add to list
        List<Recipe> allRecipes = mDB.getRecipes();
        for(int i = 0; i < allRecipes.size(); i++) {
            Recipe r = allRecipes.get(i);
            if(r.isFavorite) {
                mFavorites.add(r);
                //debug print
                System.out.println(r.toString());
            }
        }
        mPlaceholder.setText(mFavorites.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

}
