package com.rankway.controller.common;

/***
 * 赛米平台服务接口地址
 */
public class SemiServerAddress {


    //接口： 上传日志接口
    public static String getUploadLogURL(){
        return "https://47.117.132.63:6062/logs/upload";
    }

    //  移动推送消息应答地址
    public static String getPushMessageReponseURL(){
        return "http://47.117.132.63:6061/api/handsets/pushMessageResponse?sn=%s&user=%s";
    }
}
