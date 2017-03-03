
package group4.tcss450.uw.edu.grocerypal450.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Ingredient;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {
    /** The TAG for the ShoppingListFragment. */
    public static final String TAG = "InventoryFragment";


    /** The list of what is in the shopping list. */
    private List<Ingredient> mList = new ArrayList<Ingredient>();
    /** The TextView that holds the shopping list. */

    //private TextView mTextViewList;
    private ListView mListViewInven;

    private GroceryDB mInventoryDB;

    /**
     * The constructor for the ShoppingListFragment.
     */
    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mInventoryDB == null) {
            mInventoryDB = new GroceryDB(getActivity().getApplicationContext());
        }
        List<Ingredient> list = mInventoryDB.getIngredients();
        //System.out.println(list.toString());
        for(Ingredient i: list) {
            if(!i.isInventory()) {
                mList.add(i);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View v = inflater.inflate(R.layout.fragment_shopping_list, container, false)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);
        final String[] ingredients = getResources().getStringArray(R.array.auto_complete_ingredients);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity().getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        ingredients);
        final AutoCompleteTextView text = (AutoCompleteTextView) v.findViewById(R.id.inventoryEditText);
        text.setAdapter(adapter);

        //mTextViewList = (TextView) v.findViewById(R.id.inventoryTextView);
       // mTextViewList.setMovementMethod(new ScrollingMovementMethod());
        List<String> stringList = new ArrayList<String>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + "(x"+mList.get(i).getQuantity()+")");
        }
        mListViewInven = (ListView) v.findViewById(R.id.inventoryListView);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, stringList);
        mListViewInven.setAdapter(adapter1);
        mListViewInven.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = mListViewInven.getItemAtPosition(position).toString();

                text.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        text.showDropDown();
                    }
                },500);
                text.setText(item.substring(0, item.length()-5));
                text.setSelection(text.getText().length());
            }
        });

        updateTheList();
        //add button
        Button a = (Button) v.findViewById(R.id.inventoryAddBtn);
        a.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click on the add button.
             * @param v is the view
             */
            @Override
            public void onClick(View v) {
                boolean b = false;
                String ingredient = text.getText().toString().trim().toLowerCase();
                if(ingredient.length() < 1) {
                    return;
                }
                b = addToList(ingredient);
                if(!b) {
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
        //remove button
        Button r = (Button) v.findViewById(R.id.inventoryRemoveBtn);
        r.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click on the remove button.
             * @param v is the view
             */
            @Override
            public void onClick(View v) {
                boolean b = false;
                String ingredient = text.getText().toString().trim().toLowerCase();
                if(ingredient.length() < 1) {
                    return;
                }
                b = removeFromList(ingredient);
                if(!b) {
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
        //clear button
        Button c = (Button) v.findViewById(R.id.inventoryClearBtn);
        c.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click on the clear all button.
             * @param v is the view
             */
            @Override
            public void onClick(View v) {
                clearAll();
                updateTheList();
            }
        });

        //move button
        Button m = (Button) v.findViewById(R.id.invenMove);
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = text.getText().toString().trim().toLowerCase();
                sendToShoplist(ingredient);
                updateTheList();
            }
        });

        return v;
    }
    /**
     * Add ingredient to the shopping list.
     * @param ingredient is the ingredient
     * @return true if ingredient is added, false otherwise
     */
    private boolean addToList(String ingredient) {
        for(int i = 0; i < mList.size(); i++) {
            Ingredient ing = mList.get(i);
            if (ingredient.toLowerCase().equals(ing.getIngredient().trim())) {
                if (ing.getQuantity() >= 1) {
                    return mInventoryDB.incrementIngredient(ing);
                }
            }
        }
        return mInventoryDB.insertIngredient(ingredient, 1, 1);
    }

    /**
     * Remove ingredient from the list.
     * @param ingredient
     * @return true if ingredient is removed, false otherwise
     */
    private boolean removeFromList(String ingredient) {
        boolean isRemove = false;
        for(int i = 0; i < mList.size(); i++) {
            Ingredient ing = mList.get(i);
            if(ingredient.toLowerCase().equals(ing.getIngredient().trim())) {
                if(ing.getQuantity() > 1) {
                    isRemove = mInventoryDB.decrementIngredient(ing);
                } else if (ing.getQuantity() == 1){
                    isRemove = mInventoryDB.deleteItemInventory(ingredient);
                }
            }
        }
        return isRemove;
    }

    /**
     * Remove ingredient from the list.
     */
    private void clearAll() {
        mList.clear();
        mInventoryDB.deleteAllInventory();

        mListViewInven.setAdapter(null);
    }

    /**
     * Send the item to the shopping list
     * @param ingredient
     * @return
     */
    private boolean sendToShoplist(String ingredient) {
        boolean isSent = false;
        for(int i = 0; i < mList.size(); i++) {
            Ingredient ing = mList.get(i);
            if(ingredient.toLowerCase().equals(ing.getIngredient().trim())) {
                isSent = mInventoryDB.moveItemInvenToShoplist(ing);
            }
        }
        return isSent;

    }

    /**
     * Update the the value on the TextView.
     */
    private void updateTheList() {
        mList.clear();

        mListViewInven.setAdapter(null);

        List<Ingredient> list = mInventoryDB.getIngredients();
        //System.out.println(list.toString());
        for(Ingredient i: list) {
            if(i.isInventory()) {
                mList.add(i);
            }
        }

        List<String> stringList = new ArrayList<String>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + " (x"+mList.get(i).getQuantity()+")");
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, stringList);
        mListViewInven.setAdapter(adapter1);

    }
}