package com.rankway.controller.common;

import android.Manifest;

public class AppIntentString {

    public static final String PROJECT_ID = "projectId";

    public static final String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
    };

    // APP更新的地址
    public static final String APP_DOWNLOAD_URL = "http://121.36.16.185:6061/api/appVersions/posapp";

    public static final String POS_INFO_BEAN = "posInfoBean";           //  POS相关信息
    public static final String LAST_LOGIN_USER = "lastLoginUser";       //  上次登录用户
    public static final String LAST_LOGIN_PASSWORD = "lastLoginpswd";   //  上次登录密码
    public static final String HTTP_OVER_TIME = "httpOverTime";         //  HTTP超时
    public static final String LAST_SYNC_TIME = "lastSyncTime";         //  上次同步时间
    public static final String DISH_TYPE_VER = "dishTypeVer";           //  菜品类型信息
    public static final String OFFLINE_MAX_AMOUNT = "offlineMaxAmount";           //  脱机消费最大金额
    public static final String PRINTER_HEADER = "printerHeader";           //  打印头
}