package com.rankway.controller.common;

/***
 * 赛米平台服务接口地址
 */
public class SemiServerAddress {

    //接口1：丹灵模式授权下载的数据上传接口
    public static String getOfflineUploadURL(){
        return "https://47.117.132.63:6063/danLing/offlineAuth/upload";
    }

    //接口2：离线检查上传接口
    public static String getOfflineCheckURL(){
        return "https://47.117.132.63:6063/danLing/offlineCheck/upload";
    }

    //接口3：在线检查上传接口
    public static String getOnlineCheckURL(){
        return "https://47.117.132.63:6063/danLing/onlineCheck/upload";
    }

    //接口4：  数据上报接口
    public static String getReportURL(){
        return "https://47.117.132.63:6063/danLing/dataReport/upload";
    }

    //接口5.2 赛米模式离线授权下载接口
    public static String getSemiOfflineURL(){
        return "https://47.117.132.63:6063/semicon/offlineAuth/apply?companyCode=%s&authCode=%s";
    }

    //接口6：  赛米白名单接口
    public static String getWhiteListURL(){
        return "http://47.117.132.63:6061/api/handsets/whiteList?sn=%s&user=%s";
    }

    //接口7：  赛米黑名单接口
    public static String getBlackListURL(){
        return "http://47.117.132.63:6061/api/handsets/blackList?sn=%s&user=%s";
    }

    //接口： 上传日志接口
    public static String getUploadLogURL(){
        return "https://47.117.132.63:6062/logs/upload";
    }

    //  移动推送消息应答地址
    public static String getPushMessageReponseURL(){
        return "http://47.117.132.63:6061/api/handsets/pushMessageResponse?sn=%s&user=%s";
    }
}
