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

    public static final String POS_INFO_BEAN = "posInfoBean";            //  POS相关信息
    public static final String HTTP_OVER_TIME = "httpOverTime";          //  HTTP通信超时
}