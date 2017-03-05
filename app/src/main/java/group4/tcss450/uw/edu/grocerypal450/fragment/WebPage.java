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
 */
public class WebPage extends Fragment {

    public static final String TAG = "WebPage";

    private String mUrl;

    public WebPage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString("RECIPE_URL");
        }
    }

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
