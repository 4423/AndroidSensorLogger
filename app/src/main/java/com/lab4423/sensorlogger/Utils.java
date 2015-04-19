package com.lab4423.sensorlogger;

import java.util.Collections;

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
}
