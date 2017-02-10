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


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public static final String KEY = "userInfo";

    private ArrayList<String> mNameEmail;

    private TextView mShowName;
    private TextView mShowEmail;

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

    public void updateContent(String name, String email) {
        mShowName.setText(name);
        mShowEmail.setText(email);
    }

    private void goToSearch(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RecipeSearch fragment = new RecipeSearch();
        ft.replace(R.id.fragmentContainer, fragment, RecipeSearch.TAG);
        ft.addToBackStack(RecipeSearch.TAG).commit();
    }
    private void goToShoppingList(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ShoppingListFragment fragment = new ShoppingListFragment();
        ft.replace(R.id.fragmentContainer, fragment, ShoppingListFragment.TAG);
        ft.addToBackStack(ShoppingListFragment.TAG).commit();
    }
/*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    }*/

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
/*    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }*/
}
