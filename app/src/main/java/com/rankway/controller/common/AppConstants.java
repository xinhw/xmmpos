package com.rankway.controller.common;



public  class AppConstants {

    // 储存下载的Id
    public  static final String DOWNLOAD_APK_ID_PREFS = "download_apk_id_prefs";

    public final static String HostIp="www.laputatotoru.com";

    public final static String ProjectFileDownload = "/mbdzlgtxzx/servlet/DzlgLyffJsonServlert";

    public final static String ProjectReport = "/mbdzlgtxzx/servlet/DzlgSysbJsonServlert";

    public final static String OfflineDownload = "/mbdzlgtxzx/servlet/DzlgMmlxxzJsonServlert";

    public final static String OnlineDownload = "/mbdzlgtxzx/servlet/DzlgMmxzJsonServlert";

    public final static String DanningServer = "http://qq.mbdzlg.com";
    public final static String DanningTestServer = "http://test.mbdzlg.com";

    //  项目中雷管最大数量
    public static final int MAX_DET_NUM = 30;

    // 数据恢复时需要输入的校验密码
    public static final String CLEAN_DATA_PASSWORD = "202102";

    public static final String ETEK_ONLINE_GET_PSWD ="https://47.117.132.63:6062/sms/pword/reset?phone=%s";

    public static final String ETEK_UPLOAD_HANDSET_INFO="https://47.117.132.63:6062/handsets/info";

    public static final String ETEK_UPLOAD_EVENT = "https://47.117.132.63:6062/handsets/uploadevents";

    //  SEISMIC 工程状态
    public static final int SEISMIC_PROJECT_STATUS_NORMAL = 0;          //  正常
    public static final int SEISMIC_PROJECT_STATUS_CLOSED = 1;          //  关闭
    public static final int SEISMIC_PROJECT_STATUS_DELETED = 2;         //  删除

    // SEISMIC 孔状态
    public static final int SEISMIC_HOLE_STATUS_NORMAL = 0;         //  正常
    public static final int SEISMIC_HOLE_STATUS_FILLED = 1;         //  已填埋
    public static final int SEISMIC_HOLE_STATUS_BLASTED = 2;        //  已起爆
    public static final int SEISMIC_HOLE_STATUS_DELETED = 3;        //  删除

    //  SEISMIC 雷管状态
    public static final int SEISMIC_DET_STATUS_NORMAL = 0;          //  正常
    public static final int SEISMIC_DET_STATUS_MISSED = 1;          //  失联
    public static final int SEISMIC_DET_STATUS_MISCONNECTED = 2;    //  误接
    public static final int SEISMIC_DET_STATUS_BLASTED = 3;         //  已起爆
    public static final int SEISMIC_DET_STATUS_DELETED = 4;         //  删除
    public static final int SEISMIC_DET_STATUS_BLASTED_FAILURE = 5;         //  起爆失败
    public static final int SEISMIC_DET_STATUS_FUSE_FAILURE = 6;

    public static final int SEISMIC_BLASTER_MIN_VOLTAGE = 3650;     //  Blaster最小电压
    public static final int SEISMIC_BLASTER_MID_VOLTAGE = 3750;     //  Blaster充电电压

    public static final String FEB_TYPE = "febMode";
    public static final int FEB_TYPE_CHECK = 0;         //  灭火弹 检查
    public static final int FEB_TYPE_BOUND = 1;         //  灭火弹 装订
    public static final int FEB_TYPE_LAUNCH = 2;        //  灭火弹 发射


    // FEB工程状态
    public static final int FEB_PROJECT_STATUS_NORMAL = 0;       //  创建
    public static final int FEB_PROJECT_STATUS_CHECKED = 1;      //  已检查
    public static final int FEB_PROJECT_STATUS_BOUND = 2;        //  已装订
    public static final int FEB_PROJECT_STATUS_LAUNCH = 3;       //  已发射
    public static final int FEB_PROJECT_STATUS_DELETED = 4;      //  删除
    public static final int FEB_PROJECT_STATUS_FAILURE = 5;      //  发射失败

    //  火箭状态
    public static final int FEB_ROCKET_STATUS_NORMAL = 0;       //  正常
    public static final int FEB_ROCKET_STATUS_LAUNCHING = 1;       //  发射中
    public static final int FEB_ROCKET_STATUS_LAUNCHED = 2;       //  已射中
    public static final int FEB_ROCKET_STATUS_FAILURE = 3;       //  失败

    //  发射方式
    public static final String FEB_FIRE_MODE  ="febFireMode";
    public static final int FEB_FIRE_SINGLE = 0;
    public static final int FEB_FIRE_ALL = 1;

    public static final String APP_VERSION_NAME = "1.0.7";
    public static final String NOTIFICATION_CHANNEL_ID = "2019";

}