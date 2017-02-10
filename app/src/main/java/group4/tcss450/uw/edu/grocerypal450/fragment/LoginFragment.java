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

import com.google.gson.Gson;

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
import group4.tcss450.uw.edu.grocerypal450.models.Response;
import group4.tcss450.uw.edu.grocerypal450.models.User;


public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    //private static final String LOGIN_URL = "http://10.0.2.2/grocerypal-php/login.php";
    private static final String LOGIN_URL = "https://limitless-chamber-51693.herokuapp.com/login.php";
    //private static final String LOGIN_URL = "http://cssgate.insttech.washington.edu/~lambm6/grocerypal/login.php";
    private EditText mLoginEmail, mLoginPassword;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

    private void login( ) {
        boolean error = false;
        String email = "", pass = "";
        email = mLoginEmail.getText().toString();
        pass = mLoginPassword.getText().toString();
        if (!validateEmail(email)) {
            error = true;
            mLoginEmail.setError("Please enter a valid email.");
        }
        if (TextUtils.isEmpty(pass)) {
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
     *
     * @param string
     * @return True if email is in valid form and not empty.
     */
    private boolean validateEmail(String string) {
        return (!TextUtils.isEmpty(string) || Patterns.EMAIL_ADDRESS.matcher(string).matches());
    }

    private void loginProcess(String email, String password) {

        /*mSubscriptions.add(Retrofit.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));*/
        AsyncTask<String, Void, String> task = new LoginTask();
        task.execute(LOGIN_URL, email, password);
    }

    /**
     * Send the user back to the login fragment.
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

