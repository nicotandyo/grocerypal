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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private List<Recipe> mRecipeSearchResults;
    private Context mContext;
    private GroceryDB mDB;
    private MyCustomInterface mCustomInterface;

    public RecyclerViewAdapter(Context context, List<Recipe> theList, MyCustomInterface theInterface) {
        mDB = ((ProfileActivity) context).getDB();
        mRecipeSearchResults = theList;
        mContext = context;
        mCustomInterface = theInterface;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_card_layout, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final Recipe tempRecipe = mRecipeSearchResults.get(i);

        Log.d("Recipe DATE = ", String.valueOf(tempRecipe.mDate.get(Calendar.MONTH)));
        //Render image using Picasso library
        Picasso.with(mContext).load(tempRecipe.getImgUrl())
                .into(customViewHolder.imageView);

        customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
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

            customViewHolder.plannerButton.setImageResource(android.R.drawable.ic_input_add);
        } else {
            Log.d("else set delete", "");
            int year = tempRecipe.mDate.get(Calendar.YEAR);
            int month = tempRecipe.mDate.get(Calendar.MONTH);
            int day = tempRecipe.mDate.get(Calendar.DAY_OF_MONTH);
            StringBuilder str = new StringBuilder();
            str.append(month + "-" + day + "-" + year);
            customViewHolder.dateText.setText(str);
            customViewHolder.plannerButton.setImageResource(android.R.drawable.ic_input_delete);
        }

        // onClickListenr for planner button
        customViewHolder.plannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = customViewHolder.getLayoutPosition();
                Log.d("planner clicked ", "inside adapter");
                mCustomInterface.onPlannerClicked(position);
            }
        });

        // Set appropriate icon depending on status of isFav value.
        if (tempRecipe.getIsFav()) {
            customViewHolder.favButton.setImageResource(R.drawable.ic_heart_full);
        } else {
            customViewHolder.favButton.setImageResource(R.drawable.ic_heart_transparent);
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

    @Override
    public int getItemCount() {
        return (null != mRecipeSearchResults ? mRecipeSearchResults.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected ImageButton plannerButton;
        protected ImageButton favButton;
        protected TextView textView;
        protected TextView dateText;

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

