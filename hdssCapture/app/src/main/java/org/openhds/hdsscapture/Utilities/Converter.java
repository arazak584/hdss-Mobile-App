package org.openhds.hdsscapture.Utilities;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converter {

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date fromTimestamp(String value) {
        try {
            return value == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(value);
        } catch (ParseException e) {
            e.printStackTrace(); // Log exception or handle it as needed
            return null;
        }
    }

    // Converts a Date to a String (in "yyyy-MM-dd HH:mm:ss" format)
    @TypeConverter
    public static String dateToTimestamp(Date date) {
        return date == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
    }
}
