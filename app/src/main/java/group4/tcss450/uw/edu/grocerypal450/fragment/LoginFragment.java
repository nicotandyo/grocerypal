package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Patterns;
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
    private EditText mLoginEmail;
    /**
     * EditText for user to enter login password.
     */
    private EditText mLoginPassword;

    /**
     * Create new LoginFragment.
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
        mLoginEmail = (EditText) v.findViewById(R.id.loginEmail);
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
        String email = "", pass = "";
        email = mLoginEmail.getText().toString();
        pass = mLoginPassword.getText().toString();
        if (!validateEmail(email)) {
            error = true;
            mLoginEmail.setError("Please enter a valid email.");
        }
        if (TextUtils.isEmpty(pass) || pass.length() > 50) {
            error = true;
            mLoginPassword.setError("Please enter a password.");
        }
        if (!error) {
            loginProcess(email, pass);
        } else {
            Toast.makeText(getActivity(), "Invalid user details.", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Check to make sure the email entered is not empty and in valid form.
     * @param string
     * @return True if email is in valid form and not empty and not greater than 50 characters.
     */
    private boolean validateEmail(String string) {
        return (!TextUtils.isEmpty(string) || Patterns.EMAIL_ADDRESS.matcher(string).matches() || string.length() > 50);
    }

    private void loginProcess(String email, String password) {

        AsyncTask<String, Void, String> task = new LoginTask();
        task.execute(LOGIN_URL, email, password);
    }

    /**
     * Send the user to the register fragment.
     */
    private void goToRegister(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment fragment = new RegisterFragment();
        ft.replace(R.id.MainActivity_Frame, fragment, RegisterFragment.TAG);
        ft.commit();
    }

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
                throw new IllegalArgumentException("Base URL, email, and password required.");
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
                ArrayList<Pair> params = new ArrayList<Pair>();
                params.add(new Pair("email", strings[1]));
                params.add(new Pair("password", strings[2]));
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
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    //set user info to pass to the ProfileActivity
                    ArrayList<String> userInfo =  new ArrayList<String>();
                    userInfo.add((String) jsonUser.get("name"));
                    userInfo.add((String) jsonUser.get("email"));
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

