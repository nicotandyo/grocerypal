package group4.tcss450.uw.edu.grocerypal450.adapters;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
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
     * Constructor for the RecyclerViewAdapter.
     * @param context is the application context
     * @param theList is a list of recipe
     */
    public RecyclerViewAdapter(Context context, List<Recipe> theList) {
        mDB = ((ProfileActivity)context).getDB();
        mRecipeSearchResults = theList;
        mContext = context;
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
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    /**
     * {@inheritDoc}
     * @param customViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final Recipe tempRecipe = mRecipeSearchResults.get(i);

        //Render image using Picasso library
            Picasso.with(mContext).load(tempRecipe.getImgUrl())
                    .into(customViewHolder.imageView);

        customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = ((FragmentActivity)mContext).getFragmentManager().beginTransaction();
                RecipeResults fragment = new RecipeResults();
                Bundle args = new Bundle();
                args.putSerializable("RECIPE", tempRecipe);
                fragment.setArguments(args);
                ft.add(R.id.fragmentContainer, fragment, RecipeResults.TAG);
                ft.addToBackStack(RecipeResults.TAG).commit();
            }
        });
        customViewHolder.rightButton.setId(i);
        customViewHolder.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempIndex = v.getId();

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
        /** The left button. */
        protected ImageView leftButton;
        /** The right button. */
        protected ImageButton rightButton;
        /** The TextView. */
        protected TextView textView;

        /**
         * The constructor for the CustomViewHolder.
         * @param view is the view
         */
        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.recipe_imageView);
            leftButton = (ImageView) view.findViewById(R.id.add_planner_button);
            rightButton = (ImageButton) view.findViewById(R.id.add_favorite_button);
            textView = (TextView) view.findViewById(R.id.recipe_name_textView);
        }
    }
}

