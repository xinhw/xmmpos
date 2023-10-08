package com.rankway.sommerlibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String formatStr_ddMMyyyyHHmm= "dd-MMM-yyyy HH:mm";

    public static Date parseDateFromformat(String dateValue) {
        return parseDate(formatStr_ddMMyyyyHHmm, dateValue);
    }

    //把字符串日期格式转化为日期类型。
    //格式化时间
    public static Date parseDate(String strFormat, String dateValue) {
        if (dateValue == null) {
            return null;
        }
        if (strFormat == null) {
            strFormat = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date newDate = null;
        try {
            newDate = dateFormat.parse(dateValue);
        } catch (ParseException pe) {
            newDate = null;
        }
        return newDate;
    }

    public static   String getDateStr (Date date){
        SimpleDateFormat   sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static   String getDateDoc (Date date){
        SimpleDateFormat   sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(date);
    }

    public static   String getDateDStr (Date date){
        SimpleDateFormat   sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }
    public static   String getDateDStr (long lms){
        SimpleDateFormat   sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(lms);
    }

    public static   String getDateShortStr (Date date){
        SimpleDateFormat   sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(date);
    }

    public static   String getDateLogStr (Date date){
        SimpleDateFormat   sdf =  new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        return sdf.format(date);
    }



}
