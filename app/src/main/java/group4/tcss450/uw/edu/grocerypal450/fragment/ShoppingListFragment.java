

package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Ingredient;


/**
 * This class lets the user add or remove ingredients from the shopping list
 * and let the user export the results via email based applications.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgeral.
 */
public class ShoppingListFragment extends Fragment{
    /** The TAG for the ShoppingListFragment. */
    public static final String TAG = "ShoppingListFragment";


    /** The list of what is in the shopping list. */
    private List<Ingredient> mList = new ArrayList<>();
    /** The ListView. */
    private ListView mListView;
    /** ShoppingListAdapter to handle the ListView. */
    private ShoppingListAdapter mAdapter;
    /** AutoCompleteTextView for the ingredients. */
    private AutoCompleteTextView mAutoTextView;
    /** The database. */
    private GroceryDB mShoplistDB;




    /**
     * The constructor for the ShoppingListFragment.
     */
    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mShoplistDB == null) {
            mShoplistDB = ((ProfileActivity) getActivity()).getDB();
        }
        List<Ingredient> list = mShoplistDB.getIngredients();
        //System.out.println(list.toString());
        for(Ingredient i: list) {
            if(!i.isInventory()) {
                mList.add(i);
            }
        }
    }



    /**
     * {@inheritDoc}
     * @param inflater is the inflater.
     * @param container is the container.
     * @param savedInstanceState is the saved instance state.
     * @return View v = inflater.inflate(R.layout.fragment_shopping_list, container, false)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        final String[] ingredients = getResources().getStringArray(R.array.auto_complete_ingredients);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity().getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        ingredients);
        mAutoTextView = (AutoCompleteTextView) v.findViewById(R.id.shopListEditTextSearch);
        mAutoTextView.setAdapter(adapter);
        List<String> stringList = new ArrayList<>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + "(x"+mList.get(i).getQuantity()+")");
        }

        mListView = (ListView) v.findViewById(R.id.shopListListView);
        mAdapter = new ShoppingListAdapter(stringList, getActivity().getApplicationContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = mListView.getItemAtPosition(position).toString();
                Log.d("RUNNING", "V");
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
        Button a = (Button) v.findViewById(R.id.shopListBtnAdd);
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
        Button r = (Button) v.findViewById(R.id.shopListBtnRemove);
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
        Button c = (Button) v.findViewById(R.id.shopListBtnClear);
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
        //export button
        Button e = (Button) v.findViewById(R.id.shopListBtnExport);
        e.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click on the clear all button.
             * @param v is the view
             */
            @Override
            public void onClick(View v) {
                String subject = "Shopping List";
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < mList.size(); i++) {
                    stringBuilder.append(mList.get(i).getIngredient() + " (x" + mList.get(i).getQuantity() + ")\n");
                }
                String message = stringBuilder.toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));

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
                    return mShoplistDB.incrementIngredient(ing);
                }
            }
        }
        return mShoplistDB.insertIngredient(ingredient, 1, 0);
    }

    /**
     * Remove ingredient from the list.

     * @param ingredient is the ingredient

     * @return true if ingredient is removed, false otherwise
     */
    private boolean removeFromList(String ingredient) {
        boolean isRemove = false;
        for(int i = 0; i < mList.size(); i++) {
            Ingredient ing = mList.get(i);
            if(ingredient.toLowerCase().equals(ing.getIngredient().trim())) {
                if(ing.getQuantity() > 1) {
                    isRemove = mShoplistDB.decrementIngredient(ing);
                } else if (ing.getQuantity() == 1){
                    isRemove = mShoplistDB.deleteItemShoplist(ingredient);
                }
            }
        }
        return isRemove;
    }

    /**

     * Send the item to the inventory.
     * @param ingredient is the ingredient to send
     * @return true or false
     */
    private boolean sendToInven(String ingredient) {
        boolean isSent = false;
        for(int i = 0; i < mList.size(); i++) {
            Ingredient ing = mList.get(i);
            if(ingredient.toLowerCase().equals(ing.getIngredient().trim())) {
                isSent = mShoplistDB.moveItemShoplistToInven(ing);
            }
        }
        return isSent;
    }

    /**

     * Remove ingredient from the list.
     */
    private void clearAll() {
        mList.clear();
        mShoplistDB.deleteAllShoplist();

        mListView.setAdapter(null);
        //mTextViewList.setText("");

    }

    /**
     * Update the the value on the TextView.
     */
    private void updateTheList() {
        mList.clear();
        mListView.setAdapter(null);

        List<Ingredient> list = mShoplistDB.getIngredients();
        for(Ingredient i: list) {
            if(!i.isInventory()) {
                mList.add(i);
            }
        }
        List<String> stringList = new ArrayList<>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + " (x"+mList.get(i).getQuantity()+")");
        }
        mAdapter = new ShoppingListAdapter(stringList, getActivity().getApplicationContext());
        mListView.setAdapter(mAdapter);
    }


    /**
     * The adapter to generate and handle the listview for the ShoppingListFragment class.
     */
    public class ShoppingListAdapter extends BaseAdapter implements ListAdapter {
        private List<String> list = new ArrayList<>();
        private Context context;
        ShoppingListAdapter(List<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        /**
         * Get the size of the list.
         * @return size of the list
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Get the item on certain position.
         * @param pos is the position
         * @return the item
         */
        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        /**
         * Return 0.
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
         * @param parent is the ViewGroup
         * @return the View
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_shoplist_item, parent, false);
            }



            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            Button decrement = (Button)view.findViewById(R.id.decrement_btn);
            Button increment = (Button)view.findViewById(R.id.increment_btn);
            Button moveInven = (Button)view.findViewById(R.id.saveToInven_btn);

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
                    sendToInven(ingredient);
                    updateTheList();
                }
            });

            return view;
        }
    }
}