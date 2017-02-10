package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;

public class RecipeSearch extends Fragment {

    public static final String TAG = "RecipeSearch";

    //private static final String API_ENDPOINT = "https://limitless-chamber-51693.herokuapp.com/yummly.php";
    private static final String API_ENDPOINT = "http://10.0.2.2/grocerypal-php/yummly.php";

    private EditText mSearch;

    private OnFragmentInteractionListener mListener;

    public RecipeSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_search, container, false);
        mSearch = (EditText) v.findViewById(R.id.recipeSearch);
        Button b = (Button) v.findViewById(R.id.searchBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void search() {
        String searchParam = mSearch.getText().toString();
        if(TextUtils.isEmpty(searchParam)) {
            Toast.makeText(getActivity(), "Search by recipe or ingredient.", Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncTask<String, Void, String> task = new RegisterTask();
        task.execute(API_ENDPOINT, searchParam);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String tag, String text);
    }

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
                //try {
                    //JSONObject numMatches = response.getJSONObject("totalMatchCount");
                    //JSONObject jsonRecipes = response.getJSONObject("matches");
                    //String recipeString = response.toString();
                    System.out.println(result);
                    //mListener.onFragmentInteraction(result);
/*                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }*/
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

