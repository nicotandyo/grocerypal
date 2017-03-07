package group4.tcss450.uw.edu.grocerypal450.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group4.tcss450.uw.edu.grocerypal450.R;

/**
 * This class handles the settings.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class SettingsFragment extends Fragment {

    /**
     * Constructor for the SettingsFragment.
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
