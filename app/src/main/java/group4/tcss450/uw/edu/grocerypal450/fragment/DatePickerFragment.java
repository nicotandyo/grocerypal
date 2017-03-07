package group4.tcss450.uw.edu.grocerypal450.fragment;

import group4.tcss450.uw.edu.grocerypal450.R;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 * This fragment is used to pick the date for the meal planner.
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class DatePickerFragment extends DialogFragment {
    /**
     * The DatePicker for the Meal Planner.
     */
    private DatePicker mDatePicker;

    /**
     * Interface for the DateDialogListener.
     */
    public interface DateDialogListener {
        void onFinishDialog(Date date);
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date,null);
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            /**
                             * {@inheritDoc}
                             * @param dialog
                             * @param which
                             */
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int mon = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,mon,day).getTime();
                                DateDialogListener activity = (DateDialogListener) getActivity();
                                activity.onFinishDialog(date);
                                dismiss();
                            }
                        })
                .create();
    }
}