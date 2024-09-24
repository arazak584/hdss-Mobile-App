package org.openhds.hdsscapture.Utilities;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    // Converts a String (in "yyyy-MM-dd HH:mm:ss" format) to a Date
    @TypeConverter
    public static Date fromTimestamp(String value) {
        try {
            return value == null ? null : dateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Converts a Date to a String (in "yyyy-MM-dd HH:mm:ss" format)
    @TypeConverter
    public static String dateToTimestamp(Date date) {
        return date == null ? null : dateFormat.format(date);
    }
}

