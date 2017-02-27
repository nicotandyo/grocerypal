package group4.tcss450.uw.edu.grocerypal450.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.fragment.InventoryFragment;
import group4.tcss450.uw.edu.grocerypal450.fragment.PlannerFragment;
import group4.tcss450.uw.edu.grocerypal450.fragment.ProfileFragment;
import group4.tcss450.uw.edu.grocerypal450.fragment.RecipeSearch;
import group4.tcss450.uw.edu.grocerypal450.fragment.SettingsFragment;
import group4.tcss450.uw.edu.grocerypal450.fragment.ShoppingListFragment;


public class ProfileActivity extends AppCompatActivity {
    /** mDrawerList populates the navigation drawer. */
    private ListView mDrawerList;
    /** Layout that holds the navigation drawer. */
    private DrawerLayout mDrawerLayout;
    /** Adapter that attaches to the navigation drawer. */
    private ArrayAdapter<String> mAdapter;
    /** Action bar button to toggle the navigation drawer. */
    private ActionBarDrawerToggle mDrawerToggle;
    /** Displays the current activity that you are in. */
    private String mActivityTitle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * This is the profile fragment.
     */
    private ProfileFragment mProfileFragment;

    /**
     * Creates the drawer on creation.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mProfileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        Intent sourceIntent = getIntent();
        ArrayList<String> userStuff = sourceIntent.getStringArrayListExtra("userInfo");
        args.putStringArrayList("userInfo", userStuff);
        mProfileFragment.setArguments(args);


        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, mProfileFragment, ProfileFragment.TAG)
                        .commit();
            }
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Populates the navigation drawer list view from string array resource.
     */
    private void addDrawerItems() {
        String[] drawerItems = getResources().getStringArray(R.array.nav_drawer_items);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String itemClicked = (String) parent.getItemAtPosition(position);
                Log.d("MAIN_ACTIVITY", itemClicked);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //Fragment tempFrag = null;
                switch (itemClicked) {
                    case "Recipe Search":
                        RecipeSearch searchFragment = new RecipeSearch();
                        ft.replace(R.id.fragmentContainer, searchFragment, RecipeSearch.TAG);
                        ft.addToBackStack(RecipeSearch.TAG).commit();
                        break;
                    case "Meal Planner":
                        Toast.makeText(ProfileActivity.this, itemClicked, Toast.LENGTH_SHORT).show();
                        break;
                    case "Ingredient Inventory":
                        Toast.makeText(ProfileActivity.this, itemClicked, Toast.LENGTH_SHORT).show();
                        break;
                    case "Shopping List":
                        ShoppingListFragment shoppingFragment = new ShoppingListFragment();
                        ft.replace(R.id.fragmentContainer, shoppingFragment, ShoppingListFragment.TAG);
                        ft.addToBackStack(ShoppingListFragment.TAG).commit();
                        break;
                    case "Settings":
                        Toast.makeText(ProfileActivity.this, itemClicked, Toast.LENGTH_SHORT).show();
                        break;
                }
/*                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, tempFrag, tempFrag.getClass().TAG)
                        .addToBackStack(null);
                // Commit the transaction
                transaction.commit();*/
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
    }

    /**
     * Sets up the navigation drawer.
     */
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * {@inheritDoc}
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /*
    @Override
    public void onFragmentInteraction(int color) {

    }
    */
}
