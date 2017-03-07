
package group4.tcss450.uw.edu.grocerypal450.fragment;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Ingredient;

/**
 * This class let the user add or remove ingredients from the inventory.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class InventoryFragment extends Fragment {
    /** The TAG for the ShoppingListFragment. */
    public static final String TAG = "InventoryFragment";
    /** The list of what is in the shopping list. */
    private List<Ingredient> mList = new ArrayList<>();
    /** The ListView. */
    private ListView mListViewInven;
    /** The adapter than handles the listView. */
    private InventoryListAdapter mAdapter;
    /** The AutoCompleteTextView to enter the ingredients. */
    private AutoCompleteTextView mAutoTextView;
    /** The database. */
    private GroceryDB mInventoryDB;

    /**
     * The constructor for the ShoppingListFragment.
     */
    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mInventoryDB == null) {
            mInventoryDB = ((ProfileActivity) getActivity()).getDB();
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
        getActivity().setTitle(getResources().getString(R.string.inventory_title));
        final String[] ingredients = getResources().getStringArray(R.array.auto_complete_ingredients);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity().getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        ingredients);
        mAutoTextView = (AutoCompleteTextView) v.findViewById(R.id.inventoryEditText);
        mAutoTextView.setAdapter(adapter);

        List<String> stringList = new ArrayList<>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + "(x"+mList.get(i).getQuantity()+")");
        }
        mListViewInven = (ListView) v.findViewById(R.id.inventoryListView);
        mAdapter = new InventoryListAdapter(stringList, getActivity().getApplicationContext());
        mListViewInven.setAdapter(mAdapter);
        mListViewInven.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = mListViewInven.getItemAtPosition(position).toString();

                mAutoTextView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAutoTextView.showDropDown();
                    }
                },500);
                mAutoTextView.setText(item.substring(0, item.length()-5));
                mAutoTextView.setSelection(mAutoTextView.getText().length());
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
                boolean b;
                String ingredient = mAutoTextView.getText().toString().trim().toLowerCase();
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
                boolean b;
                String ingredient = mAutoTextView.getText().toString().trim().toLowerCase();
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
     * @param ingredient is the ingredient to remove
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
     * @param ingredient is the send to shopping list
     * @return true or false
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

        List<String> stringList = new ArrayList<>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + " (x"+mList.get(i).getQuantity()+")");
        }
        mAdapter = new InventoryListAdapter(stringList, getActivity().getApplicationContext());
        mListViewInven.setAdapter(mAdapter);

    }

    /**
     * The adapter to generate and handle the listview for the InventoryFragment class.
     */
    public class InventoryListAdapter extends BaseAdapter implements ListAdapter {
        private List<String> list = new ArrayList<>();
        private Context context;


        /**
         * Constructor for the InventoryListAdapter.
         * @param list is the list
         * @param context is the application context
         */
        InventoryListAdapter(List<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        /**
         * Get the size of the list.
         * @return the size of the list
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Get the item in certain position.
         * @param pos is the position
         * @return the item in that position
         */
        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        /**
         * Return 0
         * @param pos is the position
         * @return 0
         */
        @Override
        public long getItemId(int pos) {
            return 0;
        }

        /**
         * Get the view.
         * @param position is the position
         * @param convertView is the View
         * @param parent is the ViewGroup parent
         * @return the modified view
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_inventory_item, parent, false);
            }



            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_inven_string);
            listItemText.setText(list.get(position));

            //Handle buttons and the onClickListeners
            Button decrement = (Button)view.findViewById(R.id.decrementInven_btn);
            Button increment = (Button)view.findViewById(R.id.incrementInven_btn);
            Button moveInven = (Button)view.findViewById(R.id.saveToShop_btn);

            decrement.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("DECREMENT", "ITEM");
                    boolean b;
                    String ingredient = mList.get(position).getIngredient();
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
            increment.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("INCREMENT", "ITEM");
                    boolean b;
                    String ingredient = mList.get(position).getIngredient();
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
            moveInven.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("MOVE TO INVEN", "ITEM");
                    String ingredient = mList.get(position).getIngredient();
                    sendToShoplist(ingredient);
                    updateTheList();
                }
            });

            return view;
        }
    }
}