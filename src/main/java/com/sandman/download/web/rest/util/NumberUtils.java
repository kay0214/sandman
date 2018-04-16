package com.sandman.download.web.rest.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by wangj on 2018/4/16.
 */
public class NumberUtils {//四舍五入保留小数处理，默认保留2位小数
    /**
     * 根据string类型参数获取double类型返回值，保留point位小数
     * */
    public static Double getDoubleByStr(String number,int point){
        point=(point==0)?2:point;
        BigDecimal bigDecimal = new BigDecimal(number);
        return bigDecimal.setScale(point,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /**
     * 根据string类型参数获取string类型返回值，保留point位小数
     * */
    public static String getStringByStr(String number,int point){
        String format = "#.";
        for(int i=0;i<point;i++){
            format += "0";
        }
        return new DecimalFormat(format).format(Double.parseDouble(number));
    }
    /**
     * 根据double类型参数获取double类型返回值，保留point位小数
     * */
    public static Double getDoubleByDouble(Double number,int point){
        return getDoubleByStr(String.valueOf(number),point);
    }
    /**
     * 根据double类型参数获取string类型返回值，保留point位小数
     * */
    public static String getStringByDouble(Double number,int point){
        return getStringByStr(String.valueOf(number),point);
    }
}
