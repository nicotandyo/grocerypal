package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.List;
import java.util.Vector;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.adapters.ListViewTextImageAdapter;
import group4.tcss450.uw.edu.grocerypal450.adapters.RecyclerViewAdapter;
import group4.tcss450.uw.edu.grocerypal450.adapters.ViewPagerAdapter;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;


/**
 * This class allows the user to enter a search query
 * that is used by the web service to call the Yummly API
 * for a set of recipes.
 */
public class RecipeSearch extends Fragment {
    /**
     * Tag for RecipeSearch fragment.
     */
    public static final String TAG = "RecipeSearch";
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

    private ArrayAdapter<String> mSuggestedAdapter;

    private ArrayAdapter<String> mSearchAdapter;

    private ArrayAdapter<String> tempAdapter;

    ArrayAdapter<String> itemsAdapter;

    private ListView mSuggestedList;

    private ListView mSearchList;

    private List<String> masterList;

    private List<String> masterCopy;

    private List<String> mIngredientsToSearch;

    private String[] mSuggestedIngredients;

    private ListView mListView;

    private ViewPager vp;

    String searchParam;

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

        mEditText = (EditText) v.findViewById(R.id.recipeSearch);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setVisibility(v.VISIBLE);
            }
        });

        mSuggestedIngredients = getResources().getStringArray(R.array.auto_complete_ingredients);
        mIngredientsToSearch = new ArrayList();

        Vector<View> mPages = new Vector<View>();

        mSuggestedList = new ListView(v.getContext());
        mSearchList = new ListView(v.getContext());

        mPages.add(mSuggestedList);
        mPages.add(mSearchList);

        vp = (ViewPager) v.findViewById(R.id.view_pager);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(v.getContext(), mPages);
        vp.setAdapter(mPagerAdapter);


        /*
        mSuggestedAdapter = new ListViewTextImageAdapter(getActivity().getBaseContext(),
                R.layout.suggested_ingredient_list_item, mSuggestedIngredients);
        mSuggestedList.setAdapter(mSuggestedAdapter);
        mSuggestedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("U clicked mSuggestdList", " ");
                addIngredient(position);
            }
        });
        */


        mSuggestedAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, mSuggestedIngredients);
        mSuggestedList.setAdapter(mSuggestedAdapter);
        mSuggestedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("U clicked mSuggestdList", " ");
                addIngredient(position);
            }
        });




        mSearchAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, mIngredientsToSearch);
        mSearchList.setAdapter(mSearchAdapter);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                removeIngredient(position);
            }
        });



        ImageView i = (ImageView) v.findViewById(R.id.addIngredient);

        i.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchParam = mEditText.getText().toString().toLowerCase();
                mIngredientsToSearch.add(searchParam);
                mEditText.getText().clear();
                mSearchAdapter.notifyDataSetChanged();
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

        return v;
    }

    /**
     * This method adds the text in the AutoCOmplete view to the lit of ingredients to search.
     */
    private void addIngredient(int position) {
        Log.d("addIngredient(): ", "begin");
        searchParam = mSuggestedList.getItemAtPosition(position).toString().toLowerCase();
        mIngredientsToSearch.add(searchParam);
        Log.d("EditText getText = ", searchParam);
        Log.d("You added: ", mIngredientsToSearch.get(mIngredientsToSearch.size() - 1));
        mSearchAdapter.notifyDataSetChanged();
    }


    private void removeIngredient(int position) {
        System.out.println("mIngredientsToSearch Size before removal = " + mIngredientsToSearch.size());
        System.out.println("List position clicked = " + position);
        Log.d("You removed: ", mIngredientsToSearch.get(position));
        mIngredientsToSearch.remove(position);
        System.out.println("mIngredientsToSearch Size after removal = " + mIngredientsToSearch.size());

        if (mIngredientsToSearch.size() != 0) {
            Log.d("The last item is now: ", mIngredientsToSearch.get(mIngredientsToSearch.size() - 1));
        }
        mSearchAdapter.notifyDataSetChanged();
    }

    /**
     * Take the user input string from the edittext and send to the web service.
     */
    private void search() {
        vp.setVisibility(v.GONE);


        String encodedIngredient = "";
        for (int i = 0; i < mIngredientsToSearch.size(); i++) {
            encodedIngredient += "&allowedIngredient[]=" + mIngredientsToSearch.get(i).replace(" ", "+");
        }


        //mIngredientList.setAdapter(null);
        // Close soft keyboard on search button pressed.
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

        //mIngredientsToSearch.clear();

        Log.d("Search String = ", encodedIngredient);
        AsyncTask<String, Void, String> task = new RegisterTask();
        task.execute(API_ENDPOINT, encodedIngredient);
        Log.d("ATTN: ", API_ENDPOINT);
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
        System.out.println("Number of recipes found:" + recipes.size());

        //Remove previous search results from view if present.
        mRecipeList.removeAllViews();

        mAdapter = new RecyclerViewAdapter(v.getContext(), recipes);
        mRecipeList.setAdapter(mAdapter);
    }

    /**
     * Private class that creates an Asynctask which calls the web service, which in turn
     * calls the Yummly API to get a response based off of the user's search parameters.
     */
    private class RegisterTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Searching.");
            this.dialog.show();
        }

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
                List<Recipe> recipes = parseResults(mJsonString);
                populateList(recipes);
            }
        }
    }

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

