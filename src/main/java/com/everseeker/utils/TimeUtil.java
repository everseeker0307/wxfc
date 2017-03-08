package com.everseeker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by everseeker on 2017/3/3.
 */
public class TimeUtil {
    public static String getBeijingDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");     //yyyy-MM-dd HH:mm:ss
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));      //设置为东八区
        return df.format(date);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
