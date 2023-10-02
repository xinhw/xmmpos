package com.rankway.controller.utils;

import android.os.SystemClock;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/02
 *   desc  : 防止按钮连击，在MIN_DOUBLE_TIME_MS时间内，只容许点击一次
 *   version: 1.0
 * </pre>
 */
public class ClickUtil {
    //  上次点击时间（毫秒）
    private static long lastClickTime = 0;
    //  上次点击的按钮ID
    private static int lastId = 0;
    //  最小点击间隔（毫秒）
    private static final long MIN_DOUBLE_TIME_MS = 500;

    public static boolean isFastDoubleClick(int id){

//        if(lastId!=id){
//            lastId = id;
//            return false;
//        }
        lastId = id;

        long time = SystemClock.elapsedRealtime();
        long timeD = time -lastClickTime;
        if((timeD>=0)&&(timeD<MIN_DOUBLE_TIME_MS)){
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
