package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.adapters.RecyclerViewAdapter;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;

import static group4.tcss450.uw.edu.grocerypal450.R.id.ingredient_list_view;

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

    ArrayAdapter<String> itemsAdapter;

    private ListView mIngredientList;

    private List<String> masterList;

    private List<String> masterCopy;

    private List<String> mIngredientsToSearch;

    String searchParam;

    /**
     * Construct a new RecipeSearch fragment.
     */
    public RecipeSearch() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     * Links the search box and button to the RecipeSearch class to provide functionality.
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
        mSearch = (EditText) v.findViewById(R.id.recipeSearch);
        mSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayIngredientList();
            }
        });
        ImageView i = (ImageView) v.findViewById(R.id.addIngredient);
        i.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addIngredient();
            }
        });
        Button b = (Button) v.findViewById(R.id.searchBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        mIngredientList = (ListView) v.findViewById(R.id.ingredient_list_view);

        mRecipeList = (RecyclerView) v.findViewById(R.id.dynamic_recipeList);
        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        mRecipeList.setLayoutManager(llm);
        mRecipeList.setHasFixedSize(true);

        return v;
    }

    /**
     *
     */
    private void addIngredient() {
        searchParam = mSearch.getText().toString();
        mSearch.getText().clear();
        mIngredientsToSearch.add(searchParam);
    }

    /**
     * Displays list view with searchable ingredient list.
     */
    private void displayIngredientList() {
        masterList = Arrays.asList(v.getResources().getStringArray(R.array.ingredient_list_array));
        final ArrayList<String> masterCopy = new ArrayList<String>(masterList);
        mIngredientsToSearch = new ArrayList();
        itemsAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, masterCopy);
        mIngredientList.setAdapter(itemsAdapter);

        //define what happens when an element in the ListView is clicked
        mIngredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String itemClicked = (String) parent.getItemAtPosition(position);
                mIngredientsToSearch.add(itemClicked);
                masterCopy.remove(itemClicked);
                itemsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Take the user input string from the edittext and send to the web service.
     */
    private void search() {
        searchParam = mSearch.getText().toString();
        // Clear editText view on search button pressed.
        mSearch.getText().clear();
        mIngredientList.setAdapter(null);
        // Close soft keyboard on search button pressed.
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);

        if(TextUtils.isEmpty(searchParam)) {
            Toast.makeText(getActivity(), "Search by recipe or ingredient.", Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncTask<String, Void, String> task = new RegisterTask();
        task.execute(API_ENDPOINT, searchParam);
        Log.d("ATTN: ", API_ENDPOINT);

    }

    /**
     * Generate an ArrayList of Recipe objects based on the returned information in the JSON response from the Yummly API.
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
            for(int i = 0; i < jsonRecipes.length(); i++) {
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
                for(int j = 0; j < jsonIngredients.length(); j++) {
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
            } else if(error) {
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

