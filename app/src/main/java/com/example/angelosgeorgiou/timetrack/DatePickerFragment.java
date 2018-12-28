package com.example.angelosgeorgiou.timetrack;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {

    private Calendar c;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        int intCalendar = bundle.getInt("calendar");
        int year = intCalendar/10000;
        int month = intCalendar / 100 % 100 - 1;
        int day = intCalendar % 100;

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }



}
