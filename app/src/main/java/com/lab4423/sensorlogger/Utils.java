package com.lab4423.sensorlogger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class Utils {

    /***
     * コレクションがnullの場合、新たにコレクションを生成します。
     * 非nullの場合、その値を返します。
     * @param iter
     * @param <T>
     * @return
     */
    public static <T> Iterable<T> nullGuard(Iterable<T> iter){
        return iter == null ? Collections.<T>emptyList() : iter;
    }


    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}
