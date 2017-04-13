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
     * 返回给定日期(givenday)之前几日(few)的日期; 如果few为负数，则表示之后的日期
     * @param givenday
     * @param few
     * @return
     */
    public static String beforeGivenday(String givenday, int few) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(givenday.substring(0, 4)), Integer.valueOf(givenday.substring(4, 6)) - 1, Integer.valueOf(givenday.substring(6, 8)));
        cal.add(Calendar.DAY_OF_MONTH, (-1) * few);
        return getBeijingDate(cal.getTime());
    }

    /**
     * 如果day=1，表示返回givenday之前的第一个周日；day=-1，表示之后的第一个周日
     * @param givenday
     * @param day: 表示星期几, 周日～周六分别用1～7表示
     * @return
     */
    public static String firstDayBeforeGivenday(String givenday, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(givenday.substring(0, 4)), Integer.valueOf(givenday.substring(4, 6)) - 1, Integer.valueOf(givenday.substring(6, 8)));
        int offsetDay = (day > 0) ? (day - cal.get(Calendar.DAY_OF_WEEK) - 7) % 7 : (7 - day - cal.get(Calendar.DAY_OF_WEEK)) % 7;
        if (offsetDay == 0 && day > 0)
            cal.add(Calendar.DAY_OF_MONTH, -7);
        else
            cal.add(Calendar.DAY_OF_MONTH, offsetDay);
        return getBeijingDate(cal.getTime());
    }

    /**
     * 返回距离givenday最近的月末
     * @param givenday
     * @param direction 1: 表示前一个月月末; 0: 表示当月月末; -1: 表示下月月末
     * @return
     */
    public static String monthTailBeforeGivenday(String givenday, int direction) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(givenday.substring(0, 4)), Integer.valueOf(givenday.substring(4, 6)) - 1, Integer.valueOf(givenday.substring(6, 8)));
        if (direction == 1)
            cal.add(Calendar.DAY_OF_MONTH, (-1) * cal.get(Calendar.DAY_OF_MONTH));
        else if (direction == 0) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 2);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        return getBeijingDate(cal.getTime());
    }

    /**
     * 将20170410这种格式转成js可以直接转化的格式: 2017/04/10
     * @param date
     * @return
     */
    public static String formatDate(String date) {
        return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
    }

//    public static void main(String[] args) {
//        System.out.println(monthTailBeforeGivenday("20171231", 1));
//    }
}
