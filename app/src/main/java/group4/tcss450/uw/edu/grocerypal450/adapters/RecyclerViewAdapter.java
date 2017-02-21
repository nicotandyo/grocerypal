package group4.tcss450.uw.edu.grocerypal450.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private List<Recipe> mRecipeSearchResults;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<Recipe> feedItemList) {
        mRecipeSearchResults = feedItemList;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_card_layout, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Recipe tempRecipe = mRecipeSearchResults.get(i);

        //Render image using Picasso library
            Picasso.with(mContext).load(tempRecipe.getImgUrl())
                    .into(customViewHolder.imageView);


        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(tempRecipe.getRecipeName()));
    }

    @Override
    public int getItemCount() {
        return (null != mRecipeSearchResults ? mRecipeSearchResults.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected Button leftButton;
        protected ImageButton rightButton;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.recipe_imageView);
            leftButton = (Button) view.findViewById(R.id.add_planner_button);
            rightButton = (ImageButton) view.findViewById(R.id.add_favorite_button);
            textView = (TextView) view.findViewById(R.id.recipe_name_textView);
        }
    }
}

