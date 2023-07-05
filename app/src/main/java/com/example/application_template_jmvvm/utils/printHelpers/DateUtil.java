package com.example.application_template_jmvvm.utils.printHelpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String getDate(String format) {
        Date calDate = Calendar.getInstance().getTime();
        return new SimpleDateFormat(format, Locale.getDefault()).format(calDate);
    }

    public static String getTime(String format) {
        Date calDate = Calendar.getInstance().getTime();
        return new SimpleDateFormat(format, Locale.getDefault()).format(calDate);
    }

    public static String getFormattedDate(String dateText) {
        String[] array = dateText.split("/");
        return array[0] + array[1] + array[2].substring(2);
    }

    public static boolean isCurrentDay(String dateText) {
        if (dateText.isEmpty()) {
            return false;
        }
        String date = getFormattedDate(dateText);
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        return sdf.format(Calendar.getInstance().getTime()).equals(date);
    }
}
