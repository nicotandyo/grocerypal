

package group4.tcss450.uw.edu.grocerypal450.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.RunnableFuture;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Ingredient;


/**
 * This class handles the shopping list fragment.
 */

public class ShoppingListFragment extends Fragment {
    /** The TAG for the ShoppingListFragment. */
    public static final String TAG = "ShoppingListFragment";


    /** The list of what is in the shopping list. */
    private List<Ingredient> mList = new ArrayList<Ingredient>();
    /** The TextView that holds the shopping list. */

    //private TextView mTextViewList;
    private ListView mListView;


    private GroceryDB mShoplistDB;

    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}
    float historicX = Float.NaN, historicY = Float.NaN;


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
            mShoplistDB = new GroceryDB(getActivity().getApplicationContext());
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
                new ArrayAdapter<String>(getActivity().getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        ingredients);
        final AutoCompleteTextView text = (AutoCompleteTextView) v.findViewById(R.id.shopListEditTextSearch);
        text.setAdapter(adapter);
        //mTextViewList = (TextView) v.findViewById(R.id.shopListTextView);
        //mTextViewList.setMovementMethod(new ScrollingMovementMethod());
        List<String> stringList = new ArrayList<String>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + "(x"+mList.get(i).getQuantity()+")");
        }






        mListView = (ListView) v.findViewById(R.id.shopListListView);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, stringList);
        mListView.setAdapter(adapter1);
//        LstViewAdapter adapter1 = new LstViewAdapter(getActivity().getApplicationContext()
//                , R.layout.item_list_shoplist,R.id.txt, stringList);
//        mListView.setAdapter(adapter1);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = mListView.getItemAtPosition(position).toString();

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
        Button a = (Button) v.findViewById(R.id.shopListBtnAdd);
        a.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click on the add button.
             * @param v is the view
             */
            @Override
            public void onClick(View v) {

                boolean b;
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
        Button r = (Button) v.findViewById(R.id.shopListBtnRemove);
        r.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click on the remove button.
             * @param v is the view
             */
            @Override
            public void onClick(View v) {

                boolean b;
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

        //inventory button
        Button i = (Button) v.findViewById(R.id.shopListBtnInven);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = text.getText().toString().trim().toLowerCase();
                sendToInven(ingredient);
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
     * @param ingredient
     * @return
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

        //mTextViewList.setText("");
        mListView.setAdapter(null);

        List<Ingredient> list = mShoplistDB.getIngredients();
        //System.out.println(list.toString());
        for(Ingredient i: list) {
            if(!i.isInventory()) {
                mList.add(i);
            }
        }

        List<String> stringList = new ArrayList<String>();
        for(int i=0; i<mList.size(); i++) {
            stringList.add(mList.get(i).getIngredient() + " (x"+mList.get(i).getQuantity()+")");
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, stringList);
        mListView.setAdapter(adapter1);
    }

//    public class LstViewAdapter extends ArrayAdapter<String> {
//        int groupid;
//        List<String> item_list;
//        ArrayList<String> desc;
//        Context context;
//        public LstViewAdapter(Context context, int vg, int id, List<String> item_list){
//            super(context,vg, id, item_list);
//            this.context=context;
//            groupid=vg;
//            this.item_list=item_list;
//
//        }
//        // Hold views of the ListView to improve its scrolling performance
//        public class ViewHolder {
//            public TextView textview;
//            public Button button;
//
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            View rowView = convertView;
//            // Inflate the list_item.xml file if convertView is null
//            if(rowView==null){
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                rowView= inflater.inflate(groupid, parent, false);
//                ViewHolder viewHolder = new ViewHolder();
//                viewHolder.textview= (TextView) rowView.findViewById(R.id.txt);
//                viewHolder.button= (Button) rowView.findViewById(R.id.bt);
//                rowView.setTag(viewHolder);
//
//            }
//            // Set text to each TextView of ListView item
//            ViewHolder holder = (ViewHolder) rowView.getTag();
//            holder.textview.setText(item_list.get(position));
//            holder.button.setText(item_list.get(position));
//            return rowView;
//        }
//
//    }

}