package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Ingredient;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;

/**
 * This class will handle the result from the RecipeSearch class.
 */
public class RecipeResults extends Fragment {

    public static final String TAG = "RecipeResults";
    /**
     * Base url of the web service which calls the Yummly API to get recipe results.
     */
    private static final String API_ENDPOINT = "https://limitless-chamber-51693.herokuapp.com/yummly.php";

    /**
     * TextView to show the results.
     */
    private Recipe mRecipe;
    private TextView mName;
    private ImageView mImage;
    private ListView mListView;
    private List<Ingredient> mIngredientList;
    private List<String> mNewIngredients;
    private GroceryDB mDB;
    private IngredientAdapter mAdapter;

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
        mRecipe = new Recipe();
        if(getArguments() != null) {
            mRecipe = (Recipe) getArguments().getSerializable("RECIPE");
        }
        System.out.println("Recipe result:" + mRecipe.toString());
        //Render image using Picasso library
        mName = (TextView) v.findViewById(R.id.resultName);
        mImage = (ImageView) v.findViewById(R.id.resultImage);
        mListView = (ListView) v.findViewById(R.id.resultIngredients);
        mDB = ((ProfileActivity)getActivity()).getDB();
        updateTheList();

        mName.setText(mRecipe.getRecipeName());
        //Render image using Picasso library
        Picasso.with(getActivity()).load(mRecipe.getImgUrl())
                .into(mImage);

        return v;
    }

    /**
     * Add ingredient to the shopping list.
     * @param ingredient is the ingredient
     * @return true if ingredient is added, false otherwise
     */
    private boolean addToInventory(String ingredient) {
        for(Ingredient i: mIngredientList) {
            if (i.getIngredient().toLowerCase().equals(ingredient.toLowerCase().trim())) {
                if (i.getQuantity() >= 1) {
                    return mDB.incrementIngredient(i);
                }
            }
        }
        return mDB.insertIngredient(ingredient.toLowerCase().trim(), 1, 1);
    }


    /**
     * Add ingredient to the shopping list.
     * @param ingredient is the ingredient
     * @return true if ingredient is added, false otherwise
     */
    private boolean addToList(String ingredient) {
        for(Ingredient i: mIngredientList) {
            if (i.getIngredient().toLowerCase().equals(ingredient.toLowerCase().trim())) {
                if (i.getQuantity() >= 1) {
                    return mDB.incrementIngredient(i);
                }
            }
        }
        return mDB.insertIngredient(ingredient.toLowerCase().trim(), 1, 0);
    }

    /**
     * Update the the value on the TextView.
     */
    private void updateTheList() {
        mListView.setAdapter(null);

        mIngredientList = mDB.getIngredients();
        mNewIngredients = mRecipe.getIngredients();

        mAdapter = new IngredientAdapter(mNewIngredients, getActivity().getApplicationContext());
        mListView.setAdapter(mAdapter);
    }


    public class IngredientAdapter extends BaseAdapter implements ListAdapter {
        private List<String> list = new ArrayList<String>();
        private Context context;



        public IngredientAdapter(List<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_result_item, null);
            }

            //Handle buttons and add onClickListeners
            Button addInventory = (Button)view.findViewById(R.id.resultAddToInven);
            Button addShopping = (Button)view.findViewById(R.id.resultAddToList);

            for(Ingredient i: mIngredientList) {
                if(i.getIngredient().toLowerCase().equals(list.get(position))) {
                    //addInventory.setBackgroundResource(0);
                    //addShopping.setBackgroundResource(0);
                    if(i.isInventory()) {
                        addShopping.setBackgroundResource(0);
                        view.setBackgroundColor(Color.parseColor("#C6FE5C"));
                    } else {
                        addInventory.setBackgroundResource(0);
                        view.setBackgroundColor(Color.parseColor("#929cdd"));
                    }
                } else {
                    addInventory.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            boolean b;
                            String ingredient = mNewIngredients.get(position);
                            b = addToInventory(ingredient);
                            if(!b) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "unable to add: " + ingredient,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                updateTheList();
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "added to inventory: " + ingredient,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    addShopping.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            boolean b;
                            String ingredient = mNewIngredients.get(position);
                            b = addToList(ingredient);
                            if(!b) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "unable to add: " + ingredient,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                updateTheList();
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "added to shopping: " + ingredient,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.result_item_string);
            listItemText.setText(list.get(position));

            return view;
        }
    }
}
