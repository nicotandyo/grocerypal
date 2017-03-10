package group4.tcss450.uw.edu.grocerypal450.adapters;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.Interface.MyCustomInterface;
import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.activities.ProfileActivity;
import group4.tcss450.uw.edu.grocerypal450.fragment.RecipeResults;
import group4.tcss450.uw.edu.grocerypal450.models.GroceryDB;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;

/**
 * This is a custom adapter for the RecyclerView.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    /**
     * A list of recipe search results.
     */
    private List<Recipe> mRecipeSearchResults;
    /**
     * The application context.
     */
    private Context mContext;
    /**
     * The grocery database.
     */
    private GroceryDB mDB;
    /**
     * Custom interface.
     */
    private MyCustomInterface mCustomInterface;
    /**
     * Toggle button for the meal planner.
     */
    private boolean mPlannerToggle = false;

    /**
     * Constructor for the RecyclerViewAdapter.
     * @param context is the context
     * @param theList is the list of recipes
     * @param theInterface is the custom interface
     */
    public RecyclerViewAdapter(Context context, List<Recipe> theList, MyCustomInterface theInterface) {
        mDB = ((ProfileActivity) context).getDB();
        mRecipeSearchResults = theList;
        mContext = context;
        mCustomInterface = theInterface;
    }

    /**
     * {@inheritDoc}
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_card_layout, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    /**
     * {@inheritDoc}
     * @param customViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final Recipe tempRecipe = mRecipeSearchResults.get(i);

        Log.d("Recipe DATE = ", String.valueOf(tempRecipe.mDate.get(Calendar.MONTH)));
        //Render image using Picasso library
        Picasso.with(mContext).load(tempRecipe.getImgUrl())
                .into(customViewHolder.imageView);

        customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             * @param v
             */
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = ((FragmentActivity) mContext).getFragmentManager().beginTransaction();
                RecipeResults fragment = new RecipeResults();
                Bundle args = new Bundle();
                args.putSerializable("RECIPE", tempRecipe);
                fragment.setArguments(args);
                ft.add(R.id.fragmentContainer, fragment, RecipeResults.TAG);
                ft.addToBackStack(RecipeResults.TAG).commit();
            }
        });

        // Set appropriate icon depending on status of DATE value.

        Log.d("date = ", String.valueOf(tempRecipe.mDate.get(Calendar.MONTH)));
        if (Integer.valueOf(tempRecipe.mDate.get(Calendar.YEAR)) == 1900) {

            customViewHolder.plannerButton.setImageResource(R.drawable.ic_green_plus);
        } else {
            Log.d("else set delete", "");

            String[] daysOfWeek = {"SAT", "SUN", "MON", "TUES", "WED", "THURS", "FRI", "SAT"};
            String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"};

            int weekday = tempRecipe.mDate.get(Calendar.DAY_OF_WEEK);
            Log.d("Day of week,", String.valueOf(tempRecipe.mDate.get(Calendar.DAY_OF_WEEK)));

            int theMonth = tempRecipe.mDate.get(Calendar.MONTH);
            Log.d("month", String.valueOf(tempRecipe.mDate.get(Calendar.MONTH)));

            String day = daysOfWeek[weekday];
            int year = tempRecipe.mDate.get(Calendar.YEAR);

            String month = months[theMonth];
            int date = tempRecipe.mDate.get(Calendar.DAY_OF_MONTH);
            Log.d("int day = ", String.valueOf(tempRecipe.mDate.get(Calendar.DAY_OF_MONTH)));

            StringBuilder str = new StringBuilder();
            str.append(day + " " + month  + " " + date + ", " + year);
            customViewHolder.dateText.setText(str);
            customViewHolder.plannerButton.setImageResource(R.drawable.ic_minus_red);
        }

        // onClickListenr for planner button
        customViewHolder.plannerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             * @param v
             */
            @Override
            public void onClick(View v) {
                int position = customViewHolder.getLayoutPosition();
                Log.d("planner clicked ", "inside adapter");
                boolean a = false;

                //mPlannerToggle ^= true;
//                if(mPlannerToggle) {
//                    Log.d("REMOVE", "Planner");
//                    tempRecipe.mDate.set(1900, 1, 1);
//                    customViewHolder.dateText.setText("");
//                    customViewHolder.plannerButton.setImageResource(R.drawable.ic_green_plus);
//                }
                a = mCustomInterface.onPlannerClicked(position);
                if(a) {
                    tempRecipe.mDate.set(1900, 1, 1);
                    customViewHolder.dateText.setText("");
                    customViewHolder.plannerButton.setImageResource(R.drawable.ic_green_plus);
                }


            }
        });

        // Set appropriate icon depending on status of isFav value.
        if (tempRecipe.getIsFav()) {
            customViewHolder.favButton.setImageResource(R.drawable.ic_heart_full);
        } else {
            customViewHolder.favButton.setImageResource(R.drawable.ic_heart_empty);
        }
        // onClickListener for fav button.

        customViewHolder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = customViewHolder.getLayoutPosition();
                Log.d("IsFave = ", String.valueOf(tempRecipe.getIsFav()));
                mCustomInterface.onFavClicked(position);
            }
        });
        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(tempRecipe.getRecipeName()));
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int getItemCount() {
        return (null != mRecipeSearchResults ? mRecipeSearchResults.size() : 0);
    }

    /**
     * Custom view holder for the RecyclerView.
     */
    class CustomViewHolder extends RecyclerView.ViewHolder {
        /** The image view. */
        protected ImageView imageView;
        /** The recipe planner button. */
        protected ImageButton plannerButton;
        /** The favorites button. */
        protected ImageButton favButton;
        /** TextView for the view holder. */
        protected TextView textView;
        /** TextView for the date. */
        protected TextView dateText;

        /**
         * The constructor for the CustomViewHolder.
         * @param view is the view
         */
        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.recipe_imageView);
            plannerButton = (ImageButton) view.findViewById(R.id.add_planner_button);
            favButton = (ImageButton) view.findViewById(R.id.add_favorite_button);
            textView = (TextView) view.findViewById(R.id.recipe_name_textView);
            dateText = (TextView) view.findViewById(R.id.planner_date);
        }
    }
}

