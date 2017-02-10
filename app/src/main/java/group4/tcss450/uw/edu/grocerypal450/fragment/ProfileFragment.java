package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.fragment.ShoppingListFragment;

/**
 * This fragment display's the user's name and email and offers
 * navigation options to the other fragments in the application.
 */
public class ProfileFragment extends Fragment {
    /**
     * TAG for ProfileFragment
     */
    public static final String TAG = "ProfileFragment";
    /**
     * Key to retrieve arguments sent from MainActivity.
     */
    public static final String KEY = "userInfo";
    /**
     * ArrayList containing the arguments passed from MainActivity.
     */
    private ArrayList<String> mNameEmail;

    private TextView mShowName;
    private TextView mShowEmail;

    /**
     * Construct for ProfileFragment.
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNameEmail = getArguments().getStringArrayList(KEY);
            System.out.println(mNameEmail);
        }
    }

    /**
     * {@inheritDoc}
     * Displays the user's name and email and set's listener to
     * buttons to take the user to other features of the app.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mShowName = (TextView) v.findViewById(R.id.showName);
        mShowEmail = (TextView) v.findViewById(R.id.showEmail);
        updateContent(mNameEmail.get(0), mNameEmail.get(1));
        Button b = (Button) v.findViewById(R.id.goToSearch);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToSearch();
            }
        });
        Button buttonShop = (Button) v.findViewById(R.id.goToShoppingList);
        buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShoppingList();
            }
        });
        return v;
    }

    /**
     * Set the text views to display the user's name and email.
     * @param name Name to be shown
     * @param email Email to be shown
     */
    public void updateContent(String name, String email) {
        mShowName.setText(name);
        mShowEmail.setText(email);
    }

    /**
     * Replace this fragment with the RecipeSearch fragment.
     */
    private void goToSearch(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RecipeSearch fragment = new RecipeSearch();
        ft.replace(R.id.fragmentContainer, fragment, RecipeSearch.TAG);
        ft.addToBackStack(RecipeSearch.TAG).commit();
    }

    /**
     * Replace this fragment with the ShoppingList fragment.
     */
    private void goToShoppingList(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ShoppingListFragment fragment = new ShoppingListFragment();
        ft.replace(R.id.fragmentContainer, fragment, ShoppingListFragment.TAG);
        ft.addToBackStack(ShoppingListFragment.TAG).commit();
    }
}
