package com.everseeker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * js传过来的日期格式为2017/4/10，转换成20170410
     * @param jsdate
     * @return
     */
    public static String jsdateTransferTomydate(String jsdate) throws Exception {
        if (jsdate == "" || jsdate == null)
            throw new Exception("日期为空");
        String[] year_month_date = jsdate.split("/");
        String month = year_month_date[1];
        String date = year_month_date[2];
        if (month.length() == 1)
            month = "0" + month;
        if (date.length() == 1)
            date = "0" + date;
        return year_month_date[0] + month + date;
    }

    /**
     * 返回给定日期(givenday)之前几日(few)的日期
     * @param givenday
     * @param few
     * @return
     */
    public static String beforeGivenDay(String givenday, int few) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(givenday.substring(0, 4)), Integer.valueOf(givenday.substring(4, 6)) - 1, Integer.valueOf(givenday.substring(6, 8)));
        cal.add(Calendar.DAY_OF_MONTH, (-1) * few);
        return getBeijingDate(cal.getTime());
    }

//    public static void main(String[] args) {
//    }
}
