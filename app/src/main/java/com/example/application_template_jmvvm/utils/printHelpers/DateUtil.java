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

    public static String getDateFormat(String dateText) {
        String[] array = dateText.split("/");
        return array[0] + array[1] + array[2].substring(2);
    }

    public static String getTimeFormat(String timeText) {
        String[] array = timeText.split(":");
        return array[0] + array[1] + array[2].substring(2);
    }

    public static String getFormattedDate(String dateText) {
        String year = dateText.substring(0, 4);
        String month = dateText.substring(4, 6);
        String day = dateText.substring(6, 8);

        return day + "-" + month + "-" + year;
    }

    public static String getFormattedTime(String timeText) {
        String hour = timeText.substring(0, 2);
        String minute = timeText.substring(2, 4);
        String second = timeText.substring(4, 6);

        return hour + ":" + minute + ":" + second;
    }

    public static boolean isCurrentDay(String dateText) {
        if (dateText.isEmpty()) {
            return false;
        }
        String date = getDateFormat(dateText);
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime()).equals(date);
    }
}
