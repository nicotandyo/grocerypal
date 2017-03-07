package group4.tcss450.uw.edu.grocerypal450.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import group4.tcss450.uw.edu.grocerypal450.R;

/**
 * This class is used to display web pages
 * for the recipe directions.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class WebPage extends Fragment {
    /**
     * The TAG for the WebPage fragment.
     */
    public static final String TAG = "WebPage";
    /**
     * The URL for the web page.
     */
    private String mUrl;

    /**
     * Constructor for the WebPage.
     */
    public WebPage() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString("RECIPE_URL");
        }
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
        View v = inflater.inflate(R.layout.fragment_web_page, container, false);
        WebView w = (WebView) v.findViewById(R.id.webview);
        w.loadUrl(mUrl);
        return v;
    }

}
