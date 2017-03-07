
package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import group4.tcss450.uw.edu.grocerypal450.Interface.MyCustomInterface;
import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;
import group4.tcss450.uw.edu.grocerypal450.adapters.RecyclerViewAdapter;
import group4.tcss450.uw.edu.grocerypal450.adapters.ViewPagerAdapter;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;
import group4.tcss450.uw.edu.grocerypal450.models.Ingredient;


/**
 * This class allows the user to enter a search query
 * that is used by the web service to call the Yummly API
 * for a set of recipes.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class RecipeSearch extends Fragment implements MyCustomInterface {
    /**
     * Tag for RecipeSearch fragment.
     */
    public static final String TAG = "RecipeSearch";
    /**
     * Base url of the web service which calls the Yummly API to get recipe results.
     */
    private static final String API_ENDPOINT = "https://limitless-chamber-51693.herokuapp.com/yummly.php";
    /**
     * EditText containing the user's search input.
     */
    private EditText mEditText;
    /**
     * LinearLayout that contains a set of buttons used to display the returned recipes from the search.
     */
    private RecyclerView mRecipeList;
    /**
     * String representing a JSON response for the user's API call.
     */
    private String mJsonString;
    /**
     * View for this fragment.
     */
    private View mView;
    /**
     * RecyclerViewAdapter for the recipe search.
     */
    private RecyclerViewAdapter mAdapter;
    /**
     * ViewPagerAdapter for the recipe search.
     */
    private ViewPagerAdapter mPagerAdapter;
    /**
     * ArrayAdapter for the user inventory.
     */
    private ArrayAdapter<String> mUserInventoryAdapter;
    /**
     * ArrayAdapter for the suggested recipe.
     */
    private ArrayAdapter<String> mSuggestedAdapter;
    /**
     * ArrayAdapter for the search recipe.
     */
    private ArrayAdapter<String> mSearchAdapter;

    /**
     * The List holding the content currently being fed to the recycler view.
     */
    private List<Recipe> mDisplayList;
    /**
     * List to hold the results from the search recipe.
     */
    private List<Recipe> mSearchResults;
    /**
     * List to hold the user's recipes.
     */
    private List<Recipe> mUserRecipes;
    /**
     * ListView for the ingredients.
     */
    private ListView mUserIngredientsListView;
    /**
     * ListView for the suggested recipe.
     */
    private ListView mSuggestedList;
    /**
     * ListView for the search recipe.
     */
    private ListView mSearchList;
    /**
     * List of String for temporary storage.
     */
    private List<String> mTempStorage;
    /**
     * List of String for another temporary storage.
     */
    private List<String> mTempStorage2;
    /**
     * List of ingredients from the database.
     */
    private List<Ingredient> mUserIngredientsFromDB;
    /**
     * List of user inventory.
     */
    private List<String> mUserInventory;
    /**
     * List of ingredients to search.
     */
    private List<String> mIngredientsToSearch;
    /**
     * List of suggested ingredients.
     */
    private List<String> mSuggestedIngredients;
    /**
     * String array of ingredients.
     */
    private String[] mIngredientArrayResource;
    /**
     * ViewPager for the recipe research.
     */
    private ViewPager mViewPager;
    /**
     * String for the search parameter.
     */
    private String mSearchParameter;
    /**
     * Recipe database.
     */
    private GroceryDB mRecipeDB;
    /**
     * Vector view for the page.
     */
    private Vector<View> mPages;
    /**
     * RadioButton for the meal planner, favorites, and search.
     */
    private RadioButton mRadioButton;

    /**
     * Constructor for the RecipeSearch fragment.
     */
    public RecipeSearch() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get db info and build appropriate views.
        if (mRecipeDB == null) {
            mRecipeDB = ((ProfileActivity) getActivity()).getDB();
        }
        mUserRecipes = mRecipeDB.getRecipes();
        mSearchResults = new ArrayList<>();
        mUserIngredientsFromDB = mRecipeDB.getIngredients();
        Log.d("# ingredients from db: ", String.valueOf(mUserIngredientsFromDB.size()));
        mUserInventory = new ArrayList<>();
        Log.d("user ing from db = ", String.valueOf(mUserIngredientsFromDB.size()));

    }

    /**
     * {@inheritDoc}
     * Links the search box and button to the RecipeSearch class to provide functionality.
     *x
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recipe_search, container, false);


        mDisplayList = new ArrayList<>();
        initViews();

        if (mUserIngredientsFromDB.size() > 0) {
            userInventoryList(mUserIngredientsFromDB);
        }
        return mView;
    }

    /**
     * This method creates views and sets adapters where required.
     */
    private void initViews() {
        mTempStorage = new ArrayList<>();
        mTempStorage2 = new ArrayList<>();
        mIngredientArrayResource = getResources().getStringArray(R.array.auto_complete_ingredients);
        mSuggestedIngredients = new ArrayList<>(Arrays.asList(mIngredientArrayResource));
        mIngredientsToSearch = new ArrayList<>();

        mEditText = (EditText) mView.findViewById(R.id.recipeSearch);
        //show the ListView when the EditText is clicked.
        mEditText.setOnClickListener(new View.OnClickListener() {
            /**
             * Set the visibility of the ViewPager.
             * @param v is the view.
             */
            @Override
            public void onClick(View v) {
                mViewPager.setVisibility(View.VISIBLE);
            }
        });

        //jump to list position on text typed.
        mEditText.addTextChangedListener(new TextWatcher() {
            /**
             * {@inheritDoc}
             * @param s
             * @param start
             * @param count
             * @param after
             */
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * {@inheritDoc}
             * @param s
             * @param start
             * @param before
             * @param count
             */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            /**
             * Jump to the list position after text changed.
             * @param s is the text
             */
            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                jumpToPosition(search);
            }
        });

        final RadioGroup rg = (RadioGroup) mView.findViewById(R.id.radioGroup);
        mRadioButton = (RadioButton) mView.findViewById(R.id.radioSearch);
        mRadioButton.setVisibility(View.INVISIBLE);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /**
             * Respond to each RadioButton click.
             * @param radioGroup is the RadioButton group
             * @param i is the position
             */
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = rg.getCheckedRadioButtonId();
                switch (id) {
                    case (R.id.radioPlanner): // meal planner
                        mDisplayList.clear();
                        mRadioButton.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.GONE);
                        for (int k = 0; k < mUserRecipes.size(); k++) {
                            Recipe tempRecipe = mUserRecipes.get(k);
                            int year = tempRecipe.mDate.get(Calendar.YEAR);
                            if (year != 1900) {
                                mDisplayList.add(mUserRecipes.get(k));
                            }
                        }
                        populateList(mDisplayList);
                        break;

                    case (R.id.radioSearch): // search
                        mDisplayList.clear();
                        //mViewPager.setVisibility(View.VISIBLE);
                        if(mSearchResults != null) {
                            mDisplayList.addAll(mSearchResults);
                        }
                        if(mSearchResults.size() == 0) {
                            mViewPager.setVisibility(mView.VISIBLE);
                        }
                        populateList(mDisplayList);
                        break;
                    case (R.id.radioFav): // favorites
                        Log.d("radioFav clicked", "");
                        mDisplayList.clear();
                        mRadioButton.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.GONE);
                        for (int k = 0; k < mUserRecipes.size(); k++) {
                            if (mUserRecipes.get(k).getIsFav()) {
                                mDisplayList.add(mUserRecipes.get(k));
                            }
                        }
                        populateList(mDisplayList);
                        break;
                }
            }
        });


        //make sure all suggested ingredients are lowercase
        for (int i = 0; i < mSuggestedIngredients.size(); i++) {
            mSuggestedIngredients.set(i, mSuggestedIngredients.get(i).toLowerCase());
        }
        //sort suggested ingredients alphabetically.
        Collections.sort(mSuggestedIngredients);


        mSuggestedList = new ListView(mView.getContext());
        mSearchList = new ListView(mView.getContext());


        // set the suggested ingredient list adapter
        mSuggestedAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.suggested_ingredient_list_item, mSuggestedIngredients);
        mSuggestedList.setAdapter(mSuggestedAdapter);
        // add ingredient to search list on item clicked.
        mSuggestedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * {@inheritDoc}
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                addIngredientFromList(position);
            }
        });
        // Close soft keyboard on list touched.
        mSuggestedList.setOnTouchListener(new View.OnTouchListener() {
            /**
             * {@inheritDoc}
             * @param v
             * @param event
             * @return
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Close soft keyboard on list touched.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                return false;
            }
        });

        mSearchAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.search_ingredient_list_item, mIngredientsToSearch);
        mSearchList.setAdapter(mSearchAdapter);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * {@inheritDoc}
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                removeIngredient(position);
            }
        });

        final ImageView chevleft = (ImageView) mView.findViewById(R.id.left_nav);
        final ImageView chevright = (ImageView) mView.findViewById(R.id.right_nav);

        mPages = new Vector<>();
        mPages.add(mSuggestedList);
        mPages.add(mSearchList);
        mViewPager = (ViewPager) mView.findViewById(R.id.view_pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int page = mViewPager.getCurrentItem();
                switch (page) {
                    case (0): // meal planner

                        chevleft.setVisibility(mView.INVISIBLE);
                        getActivity().setTitle(getResources().getString(R.string.page_suggested_list));
                        break;

                    case (1): // search

                        getActivity().setTitle(getResources().getString(R.string.page_search_list));
                        chevleft.setVisibility(mView.INVISIBLE);

                        break;
                    case (2): // favorites
                        chevleft.setVisibility(mView.VISIBLE);
                        chevright.setVisibility(mView.VISIBLE);
                        getActivity().setTitle(getResources().getString(R.string.page_search_list));
                        break;
                    case (2): // favorites
                        chevright.setVisibility(mView.INVISIBLE);
                        getActivity().setTitle(getResources().getString(R.string.page_inventory_list));
                        break;
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerAdapter = new ViewPagerAdapter(mView.getContext(), mPages);
        mViewPager.setAdapter(mPagerAdapter);

        ImageView i = (ImageView) mView.findViewById(R.id.addIngredient);
        i.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             * @param v
             */
            @Override
            public void onClick(View v) {
                addIngredientFromText();
            }
        });


        Button b = (Button) mView.findViewById(R.id.searchBtn);
        b.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             * @param v
             */
            @Override
            public void onClick(View v) {
                search();
            }
        });

        mRecipeList = (RecyclerView) mView.findViewById(R.id.dynamic_recipeList);
        LinearLayoutManager llm = new LinearLayoutManager(mView.getContext());
        mRecipeList.setLayoutManager(llm);
        mRecipeList.setHasFixedSize(true);
    }

    /**
     * This method removes an ingredient from the suggested list of ingredients
     * and adds it to the list of ingredients to search.
     */
    private void addIngredientFromList(int position) {
        mEditText.getText().clear();
        mSearchParameter = mSuggestedList.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.add(mSearchParameter);
        mTempStorage.add(mSearchParameter);
        Collections.sort(mIngredientsToSearch);
        mSuggestedIngredients.remove(mSearchParameter);
        //mSuggestedAdapter.remove(mSuggestedAdapter.getItem(position));
        mSuggestedAdapter.notifyDataSetChanged();
        mSearchAdapter.notifyDataSetChanged();
    }

    /**
     * This method removes an ingredient from the suggested list of ingredients
     * and adds it to the list of ingredients to search.
     */
    private void addIngredientFromInventory(int position) {
        mEditText.getText().clear();
        mSearchParameter = mUserIngredientsListView.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.add(mSearchParameter);
        mUserInventory.remove(mSearchParameter);
        mTempStorage2.add(mSearchParameter);
        Collections.sort(mIngredientsToSearch);
        mUserInventoryAdapter.notifyDataSetChanged();
        mSearchAdapter.notifyDataSetChanged();
    }

    /**
     * Add ingredient from the text.
     */
    private void addIngredientFromText() {
        if (mEditText.getText().toString().length() != 0) {
            mSearchParameter = mEditText.getText().toString().toLowerCase();
            mIngredientsToSearch.add(mSearchParameter);
            mEditText.getText().clear();
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This method removes an ingredient from the list of ingredients to search
     * and adds it back to the suggested ingredient list.
     *
     * @param position is the ingredient position
     */
    private void removeIngredient(int position) {
        mSearchParameter = mSearchList.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.remove(mSearchParameter);
        if (mTempStorage.contains(mSearchParameter)) {
            mSuggestedIngredients.add(mSearchParameter);
        }
        if (mTempStorage2.contains(mSearchParameter)) {
            mUserInventory.add(mSearchParameter);
        }
        Collections.sort(mSuggestedIngredients);
        Collections.sort(mUserInventory);
        mUserInventoryAdapter.notifyDataSetChanged();
        mSuggestedAdapter.notifyDataSetChanged();
        mSearchAdapter.notifyDataSetChanged();
    }

    /**
     * Take the user input string from the edittext and send to the web service.
     */
    private void search() {
        mSuggestedIngredients.addAll(mTempStorage);
        Collections.sort(mSuggestedIngredients);
        mDisplayList.clear();
        addIngredientFromText();
        mViewPager.setVisibility(mView.GONE);
        String encodedIngredient = "";
        for (int i = 0; i < mIngredientsToSearch.size(); i++) {
            encodedIngredient += "&allowedIngredient[]=" + mIngredientsToSearch.get(i).replace(" ", "+");
        }
        // Close soft keyboard on search button pressed.
        InputMethodManager imm = (InputMethodManager) getActivity().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

        Log.d("Search String = ", encodedIngredient);
        AsyncTask<String, Void, String> task = new RegisterTask();
        task.execute(API_ENDPOINT, encodedIngredient);
        Log.d("ATTN: ", API_ENDPOINT);
        mIngredientsToSearch.clear();
    }

    /**
     * This meathod jumps to the position in a listview of the
     * first letter in the passed string.
     *
     * @param theString is the name of the ingredients
     */
    private void jumpToPosition(String theString) {
        if (theString.length() != 0) {
            for (int i = 0; i < mSuggestedIngredients.size(); i++) {
                if (mSuggestedIngredients.get(i).startsWith(theString)) {
                    mSuggestedList.setSelection(i);
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param position
     */
    @Override
    public void onFavClicked(int position) {
        Recipe tempRecipe = mDisplayList.get(position);
        if(!mRecipeDB.isRecipeExist(mDisplayList.get(position))) {
            mRecipeDB.insertRecipe(mDisplayList.get(position));
        }

        // Setting isFav from false to true

        if (!tempRecipe.getIsFav()) {
            tempRecipe = mDisplayList.get(position);
            mDisplayList.get(position).setIsFav(true);

            // mUserRecipe list is empty then add favorite.
            if (mUserRecipes.size() == 0) {
                mUserRecipes.add(tempRecipe);
            }
            // if mUserRecipe list contains recipes compare so as not to add duplicate.
            if (mUserRecipes.size() > 0) {
                boolean add = true;
                for (int k = 0; k < mUserRecipes.size(); k++) {
                    // Same ID, but not yet favorite then set existing recipes boolean.
                    if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId()) &&
                            mUserRecipes.get(k).getIsFav() != tempRecipe.getIsFav()) {
                        add = false;
                        mUserRecipes.get(k).setIsFav(true);
                    }
                }
                if (add) {
                    mUserRecipes.add(tempRecipe);
                }
            }

            Log.d("1 mUserRecipes size = ", String.valueOf(mUserRecipes.size()));

        } else if (tempRecipe.getIsFav()) {

            mDisplayList.get(position).setIsFav(false);
            tempRecipe = mDisplayList.get(position);
            Log.d("true to false set", "");

            for (int k = 0; k < mUserRecipes.size(); k++) {
                if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId())) {
                    mUserRecipes.get(k).setIsFav(false);
                    Log.d("3 mUserRecipes size = ", String.valueOf(mUserRecipes.size()));
                }
            }
        }
        mRecipeDB.updateFavorite(tempRecipe);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     * @param position
     */
    @Override
    public void onPlannerClicked(final int position, final boolean tf) {
        if(!tf) {
            Recipe tempRecipe = mDisplayList.get(position);
            if (!mRecipeDB.isRecipeExist(tempRecipe)) {
                mRecipeDB.insertRecipe(tempRecipe);
            }
            // Calendar object can't be null so 1900 is what all recipes are set to when parsed from JSON.
            // Calendar YEAR = 1900 indicates recipe not added to planner.
            if (Integer.valueOf(tempRecipe.mDate.get(Calendar.YEAR)) == 1900) {
                Calendar curCal = new GregorianCalendar();
                int year = curCal.get(Calendar.YEAR);
                int month = curCal.get(Calendar.MONTH);
                int day = curCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                    /**
                     * {@inheritDoc}
                     *
                     * @param view
                     * @param year
                     * @param month
                     * @param day
                     */
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Recipe tempRecipe = mDisplayList.get(position);
                        tempRecipe.mDate.set(year, month + 1, day);
                    }
                };
                boolean add = true;
                // if user recipe list empty then add.
                if (mUserRecipes.size() == 0) {
                    add = true;
                }
                //if user list contains recipes
                if (mUserRecipes.size() > 0) {
                    //cycle thru user recipe list
                    for (int k = 0; k < mUserRecipes.size(); k++) {
                        // Recipe already in user list but date not set
                        if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId()) &&
                                Integer.valueOf(mUserRecipes.get(k).mDate.get(Calendar.YEAR)) == 1900) {
                            mUserRecipes.get(k).mDate.set(Calendar.YEAR, tempRecipe.mDate.
                                    get(Calendar.YEAR));
                            mUserRecipes.get(k).mDate.set(Calendar.MONTH, tempRecipe.mDate.
                                    get(Calendar.MONTH));
                            mUserRecipes.get(k).mDate.set(Calendar.DAY_OF_MONTH, tempRecipe.mDate.
                                    get(Calendar.DAY_OF_MONTH));
                            add = false;
                        }
                        // recipe already in user list with date set, make sure its not same date.
                        if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId()) &&
                                Integer.valueOf(mUserRecipes.get(k).mDate.get(Calendar.YEAR)) != 1900) {
                            // if same recipe trying to set for same day
                            if (mUserRecipes.get(k).mDate.
                                    get(Calendar.MONTH) == tempRecipe.mDate.get(Calendar.MONTH) &&
                                    mUserRecipes.get(k).mDate.
                                            get(Calendar.DAY_OF_MONTH) == tempRecipe.mDate.
                                            get(Calendar.DAY_OF_MONTH) &&
                                    mUserRecipes.get(k).mDate.
                                            get(Calendar.YEAR) == tempRecipe.mDate.get(Calendar.YEAR)) {
                                add = false;
                            }
                            // if some recipe already set on a specific date
                            if (mUserRecipes.get(k).mDate.get(Calendar.MONTH) == tempRecipe.mDate.
                                    get(Calendar.MONTH) &&
                                    mUserRecipes.get(k).mDate.get(Calendar.DAY_OF_MONTH) ==
                                            tempRecipe.mDate.get(Calendar.DAY_OF_MONTH) &&
                                    mUserRecipes.get(k).mDate.get(Calendar.YEAR) ==
                                            tempRecipe.mDate.get(Calendar.YEAR)) {
                                add = false;
                                Toast.makeText(getActivity(),
                                        "You already have a recipe set on this date.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                if (add) {
                    mUserRecipes.add(tempRecipe);
                    mAdapter.notifyDataSetChanged();
                }

                DatePickerDialog hi = new DatePickerDialog(mView.getContext(), dateListener, year, month, day);

                hi.show();
            }
        } else {
            Recipe tempRecipe = mDisplayList.get(position);
            if (mRecipeDB.isRecipeExist(tempRecipe)) {
                mRecipeDB.removeRecipe(tempRecipe);
            }
        }
    }

    /**
     * Setup the user inventory list.
     * @param theList is the ingredient list
     */
    private void userInventoryList(List<Ingredient> theList) {
        for (int z = 0; z < theList.size(); z++) {
            mUserInventory.add(theList.get(z).getIngredient());
        }
        Log.d("mUserInventory size ", String.valueOf(mUserInventory.size()));
        mUserIngredientsListView = new ListView(mView.getContext());
        mUserInventoryAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.suggested_ingredient_list_item, mUserInventory);
        mUserIngredientsListView.setAdapter(mUserInventoryAdapter);
        mPages.add(mUserIngredientsListView);
        mUserIngredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * {@inheritDoc}
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                addIngredientFromInventory(position);
            }
        });
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Generate an ArrayList of Recipe objects based on the returned information in the JSON
     * response from the Yummly API.
     * @param stringResult is the result
     * @return a list of recipe
     */
    private List<Recipe> parseResults(String stringResult) {
        List<Recipe> recipeList = new ArrayList<>();
        try {
            //take the string response from the server and construct a JSONObject
            JSONObject jsonResult = new JSONObject(stringResult);
            //get only the recipes from the JSONObject
            JSONArray jsonRecipes = jsonResult.getJSONArray("matches");
            Recipe newRecipe;


            //for the number of recipes in the response, create new Recipe Java Objects
            for (int i = 0; i < jsonRecipes.length(); i++) {
                newRecipe = new Recipe();
                ArrayList<String> recipeIngredients = new ArrayList<>();
                JSONObject jsonRecipe = jsonRecipes.getJSONObject(i);
                newRecipe.setRecipeId(jsonRecipe.getString("id"));
                newRecipe.setRecipeName(jsonRecipe.getString("recipeName"));
                newRecipe.mDate.set(1900, 1, 1);
                JSONArray jsonIngredients = jsonRecipe.getJSONArray("ingredients");

                String small_image = jsonRecipe.getJSONObject("imageUrlsBySize").getString("90");
                String choppedImage = small_image.substring(0, small_image.length() - 4);
                newRecipe.setImage(choppedImage + "1000");

                //add all ingredients found to the Recipe's ingredients list
                for (int j = 0; j < jsonIngredients.length(); j++) {
                    String ingredient = jsonIngredients.getString(j);
                    recipeIngredients.add(ingredient);
                }
                newRecipe.setIngredients(recipeIngredients);
                recipeList.add(newRecipe);
                System.out.println(newRecipe);
            }
        } catch (JSONException e) {
            System.err.println("Exception parsing results: " + e.getMessage());
        }
        return recipeList;
    }

    /**
     * Populate the LinearLayout in the RecipeSearch fragment to show the user the recipes that
     * were returned from their search.
     * @param recipes is the list of recipes
     */
    private void populateList(List<Recipe> recipes) {
        Log.d("populateList() called", "");
        System.out.println("Number of recipes found:" + recipes.size());

        //Remove previous search results from view if present.
        mRecipeList.removeAllViews();

        mAdapter = new RecyclerViewAdapter(mView.getContext(), recipes, this);
        mRecipeList.setAdapter(mAdapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        Recipe tempRecipe;
        for (int r = 0; r < mUserRecipes.size(); r++) {
            tempRecipe = mUserRecipes.get(r);
            mRecipeDB.updateDate(tempRecipe);
            mRecipeDB.updateFavorite(tempRecipe);
        }

    }

    /**
     * Private class that creates an Asynctask which calls the web service, which in turn
     * calls the Yummly API to get a response based off of the user's search parameters.
     */
    private class RegisterTask extends AsyncTask<String, Void, String> {
        /**
         * The progress dialog.
         */
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Searching.");
            this.dialog.show();
        }

        /**
         * {@inheritDoc}
         * @param strings
         * @return
         */
        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length < 2) {
                throw new IllegalArgumentException("Recipe search requires at least one parameter.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {
                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                ArrayList<Pair> params = new ArrayList<>();

                params.add(new Pair("search", strings[1]));
                wr.write(getQuery(params));
                wr.flush();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }

        /**
         * {@inheritDoc}
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            mJsonString = result;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            boolean error = false;
            JSONObject response = null;
            try {
                response = new JSONObject(result);
            } catch (JSONException e) {
                System.out.println("JSONObject response error: " + e.getMessage());
            }
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG)
                        .show();
            } else if(error) {
                try {
                    String errorResponse = response.getString("error_msg");
                    Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_LONG)
                            .show();
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println(mJsonString);
                mSearchResults = parseResults(mJsonString);
                mDisplayList.addAll(mSearchResults);
                populateList(mDisplayList);
            }
        }

    }

    /**
     * {@inheritDoc}
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    @NonNull
    private String getQuery(ArrayList<Pair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode((String) pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.second, "UTF-8"));
        }
        return result.toString();
    }
}
