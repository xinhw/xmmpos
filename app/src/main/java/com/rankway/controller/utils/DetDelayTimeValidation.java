package com.rankway.controller.utils;

import android.content.Context;
import android.text.TextUtils;

import com.rankway.sommerlibrary.utils.ToastUtils;

/***
 * 雷管延时输入通用性判断
 */
public class DetDelayTimeValidation {

    //  延时的最大长度
    private static final int MAX_DELAY_TIME_LENGTH = 6;

    private static final int MIN_DELAYTIME = 1*1000;
    private static final int MAX_DELAYTIME = 120*1000;

    /***
     * 输入的字符串转为整型后的规则判断
     * @param context
     * @param strDelayTime
     * @return
     */
    public static int validateDelayTime(Context context, String strDelayTime){
        if (TextUtils.isEmpty(strDelayTime)) {
            ToastUtils.showShort(context, "请设置延时！");
            return -1;
        }
        if(strDelayTime.length()>MAX_DELAY_TIME_LENGTH){
            ToastUtils.showShort(context, String.format("模组延时设置在%d和%d之间",MIN_DELAYTIME,MAX_DELAYTIME));
            return -1;
        }

        int intTime = 0;
        try{
            intTime = Integer.parseInt(strDelayTime);
        }catch (NumberFormatException e){
            ToastUtils.showShort(context, "无效的延时设置！");
            return -1;
        }

        //  这里允许输入负数？
        if((intTime<MIN_DELAYTIME)||(intTime>MAX_DELAYTIME)){
            ToastUtils.showShort(context, String.format("模组延时设置在%d和%d之间",MIN_DELAYTIME,MAX_DELAYTIME));
            return -1;
        }
        return intTime;
    }

}
