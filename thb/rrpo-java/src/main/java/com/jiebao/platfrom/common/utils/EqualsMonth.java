package com.jiebao.platfrom.common.utils;

import java.util.Calendar;
import java.util.Date;


/**
 * 是否为同年或者同月
 *
 * @author yf
 */
public class EqualsMonth {


    public static void main(String[] args) {


        System.out.println(equals(new Date(), new Date(System.currentTimeMillis() + 345601000L)));
        //System.out.println(equals(new Date(), new Date(System.currentTimeMillis()+3000000000L)));

    }

    public static boolean equals(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        int yearNow = calendar1.get(Calendar.YEAR);
        int yearBefore = calendar2.get(Calendar.YEAR);
        int monthNow = calendar1.get(Calendar.MONTH) + 1;
        int monthBefore = calendar2.get(Calendar.MONTH) + 1;
        int dayNow = calendar1.get(Calendar.DAY_OF_MONTH);
        int dayBefore = calendar2.get(Calendar.DAY_OF_MONTH);
        int hourNow = calendar1.get(Calendar.HOUR_OF_DAY);
        int hourBefore = calendar2.get(Calendar.HOUR_OF_DAY);
        int minNow = calendar1.get(Calendar.MINUTE);
        int minBefore = calendar2.get(Calendar.MINUTE);
        int secNow = calendar1.get(Calendar.SECOND);
        int secBefore = calendar2.get(Calendar.SECOND);
        System.out.println("当前时间："+yearNow + "年" + monthNow + "月" + dayNow + "号 " + hourNow + ":" + minNow + ":" + secNow);
        System.out.println("之前时间或以后时间："+yearBefore + "年" + monthBefore + "月" + dayBefore + "号 " + hourBefore + ":" + minBefore + ":" + secBefore);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
    }


}