package group4.tcss450.uw.edu.grocerypal450.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.fragment.LoginFragment;

/**
 * This class serves as the entry point for the application,
 * handling the login and registration fragments.
 */
public class MainActivity extends AppCompatActivity {
    /** mLoginFragment is the login fragment. */
    private LoginFragment mLoginFragment;

    /**
     * Displays the initial fragment to user on creation.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginFragment = new LoginFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.MainActivity_Frame,mLoginFragment,LoginFragment.TAG).commit();
    }


}


