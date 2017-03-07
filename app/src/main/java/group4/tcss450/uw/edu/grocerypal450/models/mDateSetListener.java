package group4.tcss450.uw.edu.grocerypal450.models;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

/**
 * Created by fitz on 3/6/2017.
 */

public class mDateSetListener implements DatePickerDialog.OnDateSetListener {

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        // getCalender();
        int mYear = year;
        int mMonth = monthOfYear;
        int mDay = dayOfMonth;

    }
}