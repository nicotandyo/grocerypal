package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import group4.tcss450.uw.edu.grocerypal450.models.User;

/**
 * This class will let the user to register to the system.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class RegisterFragment extends Fragment {
    /** TAG for the RegisterFragment. */
    public static final String TAG = "RegisterFragment";
    //private static final String REGISTER_URL = "http://10.0.2.2/grocerypal-php/register.php";
    private static final String REGISTER_URL = "https://limitless-chamber-51693.herokuapp.com/register.php";
    //private static final String REGISTER_URL = "http://cssgate.insttech.washington.edu/~lambm6/grocerypal/register.php";

    /** EditText for the name. */
    private EditText mRegisterName;
    /** EditText for the password. */
    private EditText mRegisterPassword1;
    /** EditText to verify the password. */
    private EditText mRegisterPassword2;
    /** Save logged-status to shared preferences */
    private SharedPreferences mPrefs;


    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View v = inflater.inflate(R.layout.fragment_register,container,false)
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register,container,false);
        mPrefs = this.getActivity().getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        mRegisterName = (EditText) v.findViewById(R.id.registerUsername);
        mRegisterPassword1 = (EditText) v.findViewById(R.id.registerPassword1);
        mRegisterPassword2 = (EditText) v.findViewById(R.id.registerPassword2);
        Button b = (Button) v.findViewById(R.id.registerBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });
        b = (Button) v.findViewById(R.id.goLoginBtn);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToLogin();
            }
        });
        return v;
    }

    /**
     * This method takes the input from the EditText fields for
     * name, email, password, and password confirmation, and
     * checks to make sure they have appropriate data to the
     * web service.
     */
    private void register() {

        String name = mRegisterName.getText().toString();
        String pass1 = mRegisterPassword1.getText().toString();
        String pass2 = mRegisterPassword2.getText().toString();

        boolean error = false;

        if (!validateName(name)) {
            error = true;
            mRegisterName.setError("Username must have at least 3 characters.");
        }
        if (!validatePass(pass1)) {
            error = true;
            mRegisterPassword1.setError("Password must have at least 8 characters.");
        }
        if (!validatePass(pass2)) {
            error = true;
            mRegisterPassword1.setError("Password must have at least 8 characters.");
        }
        if (!validatePasswords(pass1, pass2)) {
            error = true;
            mRegisterPassword2.setError("Passwords must match.");
        }
        if (!error) {
            User user = new User(name);
            user.setPassword(pass1);
            registerProcess(user);
        } else {
            Toast.makeText(getActivity(), "Invalid user details.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check to make sure the name entered is not empty and in valid form.
     * @param string is the username
     * @return True if name is not empty and between 3 and 50 characters.
     */
    private boolean validateName(String string) {
        return (!TextUtils.isEmpty(string) || string.length() < 3 || string.length() > 50);
    }

    /**
     * Validates the name field.
     * @param pass is the password
     * @return true if not empty and less than 50 characters
     */
    private boolean validatePass(String pass){
        return !(TextUtils.isEmpty(pass) || pass.length() > 50 || pass.length() < 8);
    }

    /**
     * Validates the password fields and ensures they match.
     * @param pass1 the first password
     * @param pass2 the second password
     * @return true if not empty and pass1 and pass2 match
     */
    private boolean validatePasswords(String pass1, String pass2) {
        return (pass1.equals(pass2));
    }


    /**
     * Adds a request to register the user through the web service.
     * @param user The user to add to the database.
     */
    private void registerProcess(User user) {

        AsyncTask<String, Void, String> task = new RegisterTask();
        task.execute(REGISTER_URL, user.getName(), user.getPassword());
    }

    /**
     * Send the user back to the login fragment.
     */
    private void goToLogin(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.MainActivity_Frame, fragment, LoginFragment.TAG);
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
     * A class for creating a properly formated url and then using it to make a successful API call.
     *
     */
    private class RegisterTask extends AsyncTask<String, Void, String> {
        /** This is the progress dialog. */
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Registering.");
            this.dialog.show();
        }

        /**
         * {@inheritDoc}
         * @param strings
         * @return
         */
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
                ArrayList<Pair> params = new ArrayList<Pair>();
                params.add(new Pair("name", strings[1]));
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

        /**
         * {@inheritDoc}
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
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
         * Get the query.
         * @param params is the parameter
         * @return the string value of the result
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
