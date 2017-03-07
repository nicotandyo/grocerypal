package group4.tcss450.uw.edu.grocerypal450.models;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

/**
 *
 * @author Michael Lambion
 * @author Nico Tandyo
 * @author Patrick Fitzgerald
 */
public class mDateSetListener implements DatePickerDialog.OnDateSetListener {

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // getCalender();
        int mYear = year;
        int mMonth = monthOfYear;
        int mDay = dayOfMonth;

    }
}