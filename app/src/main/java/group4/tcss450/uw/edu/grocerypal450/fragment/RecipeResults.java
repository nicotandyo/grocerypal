package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
    //private static final String API_ENDPOINT = "https://limitless-chamber-51693.herokuapp.com/yummly.php";
    private static final String API_ENDPOINT = "http://10.0.2.2/grocerypal-php/yummly.php";

    /**
     * TextView to show the results.
     */
    private Recipe mRecipe;
    private TextView mName;
    private TextView mInfo;
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
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = new Recipe();
        if(getArguments() != null) {
            mRecipe = (Recipe) getArguments().getSerializable("RECIPE");
        }
        System.out.println("Recipe result:" + mRecipe.toString());
        mDB = ((ProfileActivity)getActivity()).getDB();
        AsyncTask<String, Void, String> task = new GetRecipeTask();
        task.execute(API_ENDPOINT, mRecipe.getRecipeId());
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
        mName = (TextView) v.findViewById(R.id.resultName);
        mImage = (ImageView) v.findViewById(R.id.resultImage);
        mListView = (ListView) v.findViewById(R.id.resultIngredients);
        mInfo = (TextView) v.findViewById(R.id.resultInfo);
        mInfo.setText("Total time: " + mRecipe.getTotalTime()/60 + "\n"
                    +"Number of Servings: " + mRecipe.getNumServings() + "\n"
                    +"Rating: " + mRecipe.getRating());
        updateTheList();

        mName.setText(mRecipe.getRecipeName());
        //Render image using Picasso library
        Picasso.with(getActivity()).load(mRecipe.getImgUrl())
                .into(mImage);
        Button directions = (Button) v.findViewById(R.id.resultDirectionsBtn);
        directions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDirections();
            }
        });

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

    private void showDirections() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        WebPage fragment = new WebPage();
        Bundle args = new Bundle();
        args.putString("RECIPE_URL", mRecipe.getRecipeUrl());
        fragment.setArguments(args);
        ft.replace(R.id.fragmentContainer, fragment, WebPage.TAG);
        ft.commit();
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

    private class GetRecipeTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 2) {
                throw new IllegalArgumentException("Get recipe requires endpoint url and recipe ID.");
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
                params.add(new Pair("getRecipe", strings[1]));
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
            String mJsonString = result;
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
                parseJsonResult(result);


            }
        }
    }

    private void parseJsonResult(String result) {
        try {
            JSONObject recipe = new JSONObject(result);
            JSONObject source = recipe.getJSONObject("source");
            mRecipe.setRecipeUrl(source.getString("sourceRecipeUrl"));
            mRecipe.setTotalTime(recipe.getInt("totalTimeInSeconds"));
            mRecipe.setRating(((float)recipe.getDouble("rating")));
            mRecipe.setNumServings(recipe.getInt("numberOfServings"));
            mInfo.setText("Total time: " + mRecipe.getTotalTime()/60 + "\n"
                    +"Number of Servings: " + mRecipe.getNumServings() + "\n"
                    +"Rating: " + mRecipe.getRating());
        } catch (JSONException e) {
            System.err.println("Error parsing get recipe: " + e.getMessage());
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
