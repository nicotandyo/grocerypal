package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group4.tcss450.uw.edu.grocerypal450.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShoppingListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShoppingListFragment extends Fragment {
    public static final String TAG = "ShoppingListFragment";

    //private OnFragmentInteractionListener mListener;
    private List<String> mList = new ArrayList<String>();
    private TextView mTextViewList;

    public ShoppingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        final String[] ingredients = getResources().getStringArray(R.array.auto_complete_ingredients);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity().getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        ingredients);
        final AutoCompleteTextView text = (AutoCompleteTextView) v.findViewById(R.id.shopListEditTextSearch);
        text.setAdapter(adapter);
        mTextViewList = (TextView) v.findViewById(R.id.shopListTextView);
        mTextViewList.setMovementMethod(new ScrollingMovementMethod());
        Button a = (Button) v.findViewById(R.id.shopListBtnAdd);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = false;
                String ingredient = text.getText().toString().trim();
                b = addToList(ingredient);
                if(b == false) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "unable to add: " + ingredient,
                            Toast.LENGTH_SHORT).show();
                } else {
                    updateTheList();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "item added: " + ingredient,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button r = (Button) v.findViewById(R.id.shopListBtnRemove);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = false;
                String ingredient = text.getText().toString().trim();
                b = removeFromList(ingredient);
                if(b == false) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "unable to remove: " + ingredient,
                            Toast.LENGTH_SHORT).show();
                } else {
                    updateTheList();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "item removed: " + ingredient,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button s = (Button) v.findViewById(R.id.shopListBtnSave);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveTheList()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "file saved successfully",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button l = (Button) v.findViewById(R.id.shopListBtnLoad);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTheList(getActivity().getApplicationContext());
                updateTheList();
                Toast.makeText(getActivity().getApplicationContext(),
                        "file loaded successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Button c = (Button) v.findViewById(R.id.shopListBtnClear);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
                updateTheList();
            }
        });
        return v;
    }
    /**
     * Add ingredient to the shopping list.
     * @param ingredient
     * @return true if ingredient is added, false otherwise
     */
    private boolean addToList(String ingredient) {
        if(mList.add(ingredient)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove ingredient from the list.
     * @param ingredient
     * @return true if ingredient is removed, false otherwise
     */
    private boolean removeFromList(String ingredient) {
        if(mList.remove(ingredient)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Remove ingredient from the list.
     */
    private void clearAll() {
        mList.clear();
    }

    /**
     * Update the the value on the TextView.
     */
    private void updateTheList() {
        mTextViewList.setText("");
        Map<String, Integer> duplicates = new HashMap<String, Integer>();
        for (String word : mList) {
            duplicates.put(word, duplicates.containsKey(word)
                    ? duplicates.get(word) + 1 : 1);
        }
        for(Map.Entry<String, Integer> entry: duplicates.entrySet()) {
            mTextViewList.append(entry.getKey() + " (x" + entry.getValue() + ")");
            mTextViewList.append("\n");
        }
    }

    /**
     * Save the shopping list file.
     * @return true if successful, false otherwise.
     */
    private boolean saveTheList() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor mEdit1 = sp.edit();
    /* sKey is an array */
        mEdit1.putInt("Status_size", mList.size());

        for(int i=0;i<mList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, mList.get(i));
        }

        return mEdit1.commit();
    }

    /**
     * Load the previous shopping list file.
     * @param context
     */
    private void loadTheList(Context context){
        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(context);
        mList.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);
        for(int i=0;i<size;i++){
            mList.add(mSharedPreference1.getString("Status_" + i, null));
        }

    }
/*
    public void onButtonPressed(String text) {
        if (mListener != null) {
            mListener.onFragmentInteraction(TAG, text);
        }
    }*/

//    @Override
/*    public void onAttach(Context context) {
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String tag, String text);
    }
}
