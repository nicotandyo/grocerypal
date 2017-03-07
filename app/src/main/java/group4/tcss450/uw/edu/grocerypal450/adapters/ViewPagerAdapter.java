package group4.tcss450.uw.edu.grocerypal450.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

/**
 * This is a custom adapter class.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class ViewPagerAdapter extends PagerAdapter {
    /**
     * The application context.
     */
    private Context mContext;
    /**
     * Vector view for the pages.
     */
    private Vector<View> pages;

    /**
     * Constructor of ViewPagerAdapter.
     * @param context is the context
     * @param pages is the vector view
     */
    public ViewPagerAdapter(Context context, Vector<View> pages) {
        this.mContext=context;
        this.pages=pages;
    }

    /**
     * {@inheritDoc}
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = pages.get(position);
        container.addView(page);
        return page;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int getCount() {
        return pages.size();
    }

    /**
     * {@inheritDoc}
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    /**
     * {@inheritDoc}
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}