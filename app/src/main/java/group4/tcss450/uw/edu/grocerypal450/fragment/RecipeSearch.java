
package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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
import java.util.Date;
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

    private static final String DIALOG_DATE = "this.DateDialog";
    /**
     * Base url of the web service which calls the Yummly API to get recipe results.
     */
    private static final String API_ENDPOINT = "https://limitless-chamber-51693.herokuapp.com/yummly.php";
    /**
     * Editext containing the user's search input.
     */
    private EditText mSearch;

    private EditText mEditText;


    /**
     * LinearLayout that contains a set of buttons used to display the returned recipes from the search.
     */
    private RecyclerView mRecipeList;
    /**
     * String representing a JSON response for the user's API call.
     */
    private String mJsonString;

    private View v;

    private RecyclerViewAdapter mAdapter;

    private ViewPagerAdapter mPagerAdapter;

    private ArrayAdapter<String> mUserInventoryAdapter;

    private ArrayAdapter<String> mSuggestedAdapter;

    private ArrayAdapter<String> mSearchAdapter;

    // The List holding the content currently being fed to the recycler view.
    private List<Recipe> mDisplayList;

    // The list
    private List<Recipe> mSearchResults;

    private List<Recipe> mUserRecipes;

    private ListView mUserIngredientsListView;

    private ListView mSuggestedList;

    private ListView mSearchList;

    private List<String> tempStorage;

    private List<String> tempStorage2;

    private List<Ingredient> mUserIngredientsFromDB;

    private List<String> mUserInventory;

    private List<String> mIngredientsToSearch;

    private List<String> mSuggestedIngredients;

    private String[] mIngredientArrayResource;

    private ListView mListView;

    private ViewPager vp;

    private String searchParam;

    private GroceryDB mRecipeDB;

    private Vector<View> mPages;

    private RadioButton srch;

    /**
     * Construct a new RecipeSearch fragment.
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
        mUserIngredientsFromDB = mRecipeDB.getIngredients();
        Log.d("# ingredients from db: ", String.valueOf(mUserIngredientsFromDB.size()));
        mUserInventory = new ArrayList<String>();
        Log.d("user ing from db = ", String.valueOf(mUserIngredientsFromDB.size()));

    }

    /**
     * {@inheritDoc}
     * Links the search box and button to the RecipeSearch class to provide functionality.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_recipe_search, container, false);


        mDisplayList = new ArrayList<Recipe>();
        initViews();

        if (mUserIngredientsFromDB.size() > 0) {
            userInventoryList(mUserIngredientsFromDB);
        }
        return v;
    }

    /**
     * This method creates views and sets adapters where required.
     */
    private void initViews() {
        tempStorage = new ArrayList();
        tempStorage2 = new ArrayList();
        mIngredientArrayResource = getResources().getStringArray(R.array.auto_complete_ingredients);
        mSuggestedIngredients = new ArrayList<String>(Arrays.asList(mIngredientArrayResource));
        mIngredientsToSearch = new ArrayList();

        mEditText = (EditText) v.findViewById(R.id.recipeSearch);
        //show listviews when editText clicked.
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setVisibility(v.VISIBLE);
            }
        });
        //jump to list position on text typed.
        mEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                jumpToPosition(search);
            }
        });

        final RadioGroup rg = (RadioGroup) v.findViewById(R.id.radioGroup);
        srch = (RadioButton) v.findViewById(R.id.radioSearch);
        srch.setVisibility(View.INVISIBLE);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = rg.getCheckedRadioButtonId();
                switch (id) {
                    case (R.id.radioPlanner):
                        mDisplayList.clear();
                        srch.setVisibility(View.VISIBLE);
                        for (int k = 0; k < mUserRecipes.size(); k++) {
                            Recipe tempRecipe = mUserRecipes.get(k);
                            int year = mUserRecipes.get(k).mDate.get(Calendar.YEAR);
                            if (year != 1900) {
                                mDisplayList.add(mUserRecipes.get(k));
                            }
                        }
                        populateList(mDisplayList);
                        break;

                    case (R.id.radioSearch):
                        mDisplayList.clear();
                        mDisplayList.addAll(mSearchResults);
                        populateList(mDisplayList);
                        break;
                    case (R.id.radioFav):
                        Log.d("radioFav clicked", "");
                        mDisplayList.clear();
                        srch.setVisibility(View.VISIBLE);
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


        mSuggestedList = new ListView(v.getContext());
        mSearchList = new ListView(v.getContext());


        // set the suggested ingredient list adapter
        mSuggestedAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                R.layout.suggested_ingredient_list_item, mSuggestedIngredients);
        mSuggestedList.setAdapter(mSuggestedAdapter);
        // add ingredient to search list on item clicked.
        mSuggestedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                addIngredientFromList(position);
            }
        });
        // Close soft keyboard on list touched.
        mSuggestedList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Close soft keyboard on list touched.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                return false;
            }
        });

        mSearchAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                R.layout.search_ingredient_list_item, mIngredientsToSearch);
        mSearchList.setAdapter(mSearchAdapter);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                removeIngredient(position);
            }
        });

        mPages = new Vector<View>();

        mPages.add(mSuggestedList);
        mPages.add(mSearchList);
        vp = (ViewPager) v.findViewById(R.id.view_pager);
        mPagerAdapter = new ViewPagerAdapter(v.getContext(), mPages);
        vp.setAdapter(mPagerAdapter);

        ImageView i = (ImageView) v.findViewById(R.id.addIngredient);
        i.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addIngredientFromText();
            }
        });


        Button b = (Button) v.findViewById(R.id.searchBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        mRecipeList = (RecyclerView) v.findViewById(R.id.dynamic_recipeList);
        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        mRecipeList.setLayoutManager(llm);
        mRecipeList.setHasFixedSize(true);
    }

    /**
     * This method removes an ingredient from the suggested list of ingredients
     * and adds it to the list of ingredients to search.
     */
    private void addIngredientFromList(int position) {
        mEditText.getText().clear();
        searchParam = mSuggestedList.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.add(searchParam);
        tempStorage.add(searchParam);
        Collections.sort(mIngredientsToSearch);
        mSuggestedIngredients.remove(searchParam);
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
        searchParam = mUserIngredientsListView.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.add(searchParam);
        mUserInventory.remove(searchParam);
        tempStorage2.add(searchParam);
        Collections.sort(mIngredientsToSearch);
        mUserInventoryAdapter.notifyDataSetChanged();
        mSearchAdapter.notifyDataSetChanged();
    }

    private void addIngredientFromText() {
        if (mEditText.getText().toString().length() != 0) {
            searchParam = mEditText.getText().toString().toLowerCase();
            mIngredientsToSearch.add(searchParam);
            mEditText.getText().clear();
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This method removes an ingredient from the list of ingredients to search
     * and adds it back to the suggested ingredient list.
     *
     * @param position
     */
    private void removeIngredient(int position) {
        searchParam = mSearchList.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.remove(searchParam);
        if (tempStorage.contains(searchParam)) {
            mSuggestedIngredients.add(searchParam);
        }
        if (tempStorage2.contains(searchParam)) {
            mUserInventory.add(searchParam);
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
        mSuggestedIngredients.addAll(tempStorage);
        Collections.sort(mSuggestedIngredients);
        mDisplayList.clear();
        addIngredientFromText();
        vp.setVisibility(v.GONE);
        String encodedIngredient = "";
        for (int i = 0; i < mIngredientsToSearch.size(); i++) {
            encodedIngredient += "&allowedIngredient[]=" + mIngredientsToSearch.get(i).replace(" ", "+");
        }
        // Close soft keyboard on search button pressed.
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
     * @param theString
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

    @Override
    public void onFavClicked(int position) {
        Recipe tempRecipe = mDisplayList.get(position);
        if(!mRecipeDB.isRecipeExist(mDisplayList.get(position))) {
            mRecipeDB.insertRecipe(mDisplayList.get(position));
        }

        // Setting isFav from false to true
        if (!tempRecipe.getIsFav()) {
            mDisplayList.get(position).setIsFav(true);

            // mUserRecipe list is empty then add favorite.
            if (mUserRecipes.size() == 0) {
                mUserRecipes.add(tempRecipe);
            }
            // if mUserRecipe list contains recipes compare so as not to add duplicate.
            if (mUserRecipes.size() > 0) {
                boolean add = true;
                for (int k = 0; k < mUserRecipes.size(); k++) {
                    // Same ID and already favorite
                    if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId()) &&
                            mUserRecipes.get(k).getIsFav() == tempRecipe.getIsFav()) {
                        add = false;
                        Toast.makeText(getActivity(), "This recipe is already one of your favorites.",
                                Toast.LENGTH_LONG).show();
                    }
                    // Same ID, but not yet favorite then set existing recipes boolean.
                    if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId()) &&
                            mUserRecipes.get(k).getIsFav() != tempRecipe.getIsFav()) {
                        add = false;
                        mUserRecipes.get(k).setIsFav(true);
                    }
                }
                if (add == true) {
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


    @Override
    public void onPlannerClicked(final int position) {


        Recipe tempRecipe = mDisplayList.get(position);
        if(!mRecipeDB.isRecipeExist(tempRecipe)) {
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
                        mUserRecipes.get(k).mDate.set(Calendar.YEAR, tempRecipe.mDate.get(Calendar.YEAR));
                        mUserRecipes.get(k).mDate.set(Calendar.MONTH, tempRecipe.mDate.get(Calendar.MONTH));
                        mUserRecipes.get(k).mDate.set(Calendar.DAY_OF_MONTH, tempRecipe.mDate.get(Calendar.DAY_OF_MONTH));
                        add = false;
                    }
                    // recipe already in user list with date set, make sure its not same date.
                    if (mUserRecipes.get(k).getRecipeId().equals(tempRecipe.getRecipeId()) &&
                            Integer.valueOf(mUserRecipes.get(k).mDate.get(Calendar.YEAR)) != 1900) {

                        // if same recipe trying to set for same day
                        if (mUserRecipes.get(k).mDate.get(Calendar.MONTH) == tempRecipe.mDate.get(Calendar.MONTH) &&
                                mUserRecipes.get(k).mDate.get(Calendar.DAY_OF_MONTH) == tempRecipe.mDate.get(Calendar.DAY_OF_MONTH) &&
                                mUserRecipes.get(k).mDate.get(Calendar.YEAR) == tempRecipe.mDate.get(Calendar.YEAR)) {
                            add = false;
                        }
                        // if some recipe already set on a specific date
                        if (mUserRecipes.get(k).mDate.get(Calendar.MONTH) == tempRecipe.mDate.get(Calendar.MONTH) &&
                                mUserRecipes.get(k).mDate.get(Calendar.DAY_OF_MONTH) == tempRecipe.mDate.get(Calendar.DAY_OF_MONTH) &&
                                mUserRecipes.get(k).mDate.get(Calendar.YEAR) == tempRecipe.mDate.get(Calendar.YEAR)) {
                            add = false;
                            Toast.makeText(getActivity(), "You already have a recipe set on this date.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            if (add = true) {
                mUserRecipes.add(tempRecipe);
                mAdapter.notifyDataSetChanged();
            }
            //mRecipeDB.updateDate(tempRecipe);
            DatePickerDialog hi = new DatePickerDialog(v.getContext(), dateListener, year, month, day);
            hi.show();
        }
    }


    private void userInventoryList(List<Ingredient> theList) {
        List<Ingredient> tempList = theList;
        for (int z = 0; z < tempList.size(); z++) {
            mUserInventory.add(tempList.get(z).getIngredient());
        }
        Log.d("mUserInventory size ", String.valueOf(mUserInventory.size()));
        mUserIngredientsListView = new ListView(v.getContext());
        mUserInventoryAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                R.layout.suggested_ingredient_list_item, mUserInventory);
        mUserIngredientsListView.setAdapter(mUserInventoryAdapter);
        mPages.add(mUserIngredientsListView);
        mUserIngredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                addIngredientFromInventory(position);
            }
        });
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Generate an ArrayList of Recipe objects based on the returned information in the JSON response from the Yummly API.
     *
     * @param stringResult
     * @return
     */
    private List<Recipe> parseResults(String stringResult) {
        List<Recipe> recipeList = new ArrayList<Recipe>();
        try {
            //take the string response from the server and construct a JSONObject
            JSONObject jsonResult = new JSONObject(stringResult);
            //get only the recipes from the JSONObject
            JSONArray jsonRecipes = jsonResult.getJSONArray("matches");
            Recipe newRecipe;


            //for the number of recipes in the response, create new Recipe Java Objects
            for (int i = 0; i < jsonRecipes.length(); i++) {
                newRecipe = new Recipe();
                ArrayList<String> recipeIngredients = new ArrayList<String>();
                JSONObject jsonRecipe = jsonRecipes.getJSONObject(i);
                newRecipe.setRecipeId(jsonRecipe.getString("id"));
                newRecipe.setRecipeName(jsonRecipe.getString("recipeName"));
                newRecipe.mDate.set(1900, 01, 01);
                //newRecipe.setImage(jsonRecipe.getString("smallImageUrls"));
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
     *
     * @param recipes
     */
    private void populateList(List<Recipe> recipes) {
        Log.d("populateList() called", "");
        System.out.println("Number of recipes found:" + recipes.size());

        //Remove previous search results from view if present.
        mRecipeList.removeAllViews();

        mAdapter = new RecyclerViewAdapter(v.getContext(), recipes, this);
        mRecipeList.setAdapter(mAdapter);
    }

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
                ArrayList<Pair> params = new ArrayList<Pair>();
                params.add(new Pair("search", strings[1]));
                wr.write(getQuery(params));
                wr.flush();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
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
            mSearchResults = new ArrayList<Recipe>();
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
                return;
            } else if (error) {
                try {
                    String errorResponse = response.getString("error_msg");
                    Toast.makeText(getActivity(), errorResponse, Toast.LENGTH_LONG)
                            .show();
                    return;
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println(mJsonString);
                //mResults.setText(mJsonString);
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

