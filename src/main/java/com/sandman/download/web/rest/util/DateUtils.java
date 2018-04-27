package com.sandman.download.web.rest.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangj on 2018/4/12.
 */
public class DateUtils {
    public static Long getLongTime(){
        Date date = new Date();
        return date.getTime();
    }
    public static Long getMinLater(int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,minute);
        Date date = calendar.getTime();
        return date.getTime();
    }
}
