package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * This class is used to build the Login fragment which allows
 * the user to enter an email address and password to login.
 * User input has verification for formatting.
 * The user also has the option go to the register fragment
 * to create a new account.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class LoginFragment extends Fragment {

    /**
     * Tag for login fragment.
     */
    public static final String TAG = "LoginFragment";
    /**
     * Base url for web-service API to login to application.
     */
    private static final String LOGIN_URL = "https://limitless-chamber-51693.herokuapp.com/login.php";
    /**
     * EditText for user to enter login email.
     */
    private EditText mLoginName;
    /**
     * EditText for user to enter login password.
     */
    private EditText mLoginPassword;
    /** Shared preferences to check if user was previously logged in. */
    private SharedPreferences mPrefs;


    /**
    * Create new LoginFragment
    */
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * This method connects the LoginFragment class to the
     * UI elements of the fragment including the email and
     * password boxes, the login button, and the register button.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mPrefs = this.getActivity().getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        boolean isLogged = mPrefs.getBoolean(getString(R.string.IS_LOGGED_IN), false);
        String loggedUser = mPrefs.getString(getString(R.string.LOGGED_USER), "");
        //if logged in and name is set, log user in
        if(isLogged && loggedUser.length() > 0) {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            //set user info to pass to the ProfileActivity
            ArrayList<String> userInfo =  new ArrayList<String>();
            userInfo.add(loggedUser);
            intent.putExtra("userInfo", userInfo);
            startActivity(intent);
        }
        mLoginName = (EditText) v.findViewById(R.id.loginName);
        mLoginPassword = (EditText) v.findViewById(R.id.loginPassword);
        Button b = (Button) v.findViewById(R.id.loginBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
        b = (Button) v.findViewById(R.id.goRegisterBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToRegister();
            }
        });

        return v;
    }

    /**
     * Gather user input from the email and password fields,
     * parse for correct format and pass to LoginProcess().
     */
    private void login( ) {
        boolean error = false;
        String name = "", pass = "";
        name = mLoginName.getText().toString();
        pass = mLoginPassword.getText().toString();
        if (!validateName(name)) {
            error = true;
            mLoginName.setError("Username must have at least 3 characters.");
        }
        if (TextUtils.isEmpty(pass) || pass.length() > 50 || pass.length() < 8) {
            error = true;
            mLoginPassword.setError("Password must have at least 8 characters.");
        }
        if (!error) {
            loginProcess(name, pass);
        } else {
            Toast.makeText(getActivity(), "Invalid user details.", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Check to make sure the name entered is not empty and in valid form.
     * @param string is the name entered
     * @return True if name is not empty and between 3 and 50 characters.
     */
    private boolean validateName(String string) {
        return (!TextUtils.isEmpty(string) || string.length() < 3 || string.length() > 50);
    }

    /**
     * Handle the login proces.
     * @param name is the name
     * @param password is the password
     */
    private void loginProcess(String name, String password) {
        AsyncTask<String, Void, String> task = new LoginTask();
        task.execute(LOGIN_URL, name, password);

    }

    /**
     * Send the user to the register fragment.
     */
    private void goToRegister(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment fragment = new RegisterFragment();
        ft.add(R.id.MainActivity_Frame, fragment, RegisterFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * This private class creates a new Asynctask that is used
     * to make an http request to the web service API to verify the
     * user's login credentials and send the user to the profile screen.
     */
    private class LoginTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Logging in.");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Base URL, name, and password required.");
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
                //set key/value pairs to be used in POST request
                ArrayList<Pair> params = new ArrayList<>();
                params.add(new Pair("name", strings[1]));
                params.add(new Pair("password", strings[2]));
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
        @Override
        protected void onPostExecute(String result){
            //hide progress bar
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // Something wrong with the network or the URL.
            boolean error = false;
            JSONObject response = null;
            try {
                response = new JSONObject(result);
                error = response.getBoolean("error");
            } catch (JSONException e) {
                System.out.println(e.getMessage());
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
                try {
                    JSONObject jsonUser = response.getJSONObject("user");
                    mPrefs.edit().putString(getString(R.string.LOGGED_USER), (String) jsonUser.get("name")).apply();
                    mPrefs.edit().putBoolean(getString(R.string.IS_LOGGED_IN), true).apply();
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    //set user info to pass to the ProfileActivity
                    ArrayList<String> userInfo =  new ArrayList<String>();
                    userInfo.add((String) jsonUser.get("name"));
                    intent.putExtra("userInfo", userInfo);
                    startActivity(intent);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }

        }

        /**
         *
         * @param params key/value pairs to send in POST
         * @return URL encoded String
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

}

