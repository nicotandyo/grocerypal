package group4.tcss450.uw.edu.grocerypal450.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import group4.tcss450.uw.edu.grocerypal450.R;
import group4.tcss450.uw.edu.grocerypal450.models.Recipe;

/**
 * Created by fitz on 2/25/2017.
 */

public class ListViewTextImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mSuggestedIngredients;
    private static LayoutInflater inflater=null;

    public ListViewTextImageAdapter(Context context, int resource, String[] theList) {
        super(context, R.layout.suggested_ingredient_list_item);
        mContext = context;
        mSuggestedIngredients = theList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mSuggestedIngredients.length;
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.suggested_ingredient_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.suggested_text);
        holder.tv.setText(mSuggestedIngredients[position]);

        return rowView;
    }

}