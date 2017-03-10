package group4.tcss450.uw.edu.grocerypal450.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.fragment.LoginFragment;

/**
 * This class serves as the entry point for the application,
 * handling the login and registration fragments.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class MainActivity extends AppCompatActivity {
    /**
     * mLoginFragment is the login fragment.
     */
    private LoginFragment mLoginFragment;

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginFragment = new LoginFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.MainActivity_Frame,mLoginFragment,LoginFragment.TAG)
                .addToBackStack(null).commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }


}


