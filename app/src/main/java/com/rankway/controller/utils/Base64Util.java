package com.rankway.controller.utils;

import android.util.Base64;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class Base64Util {
    public static String  EncodeString(String ori){
        return    Base64.encodeToString(ori.getBytes(), Base64.NO_WRAP);
    }
    public static byte[] getEncodeBytes(byte[] arrBase64){
        return  Base64.encode(arrBase64, Base64.NO_WRAP);
    }
    public static String Decode2String(String strBase64){
        return  new String(Base64.decode(strBase64.getBytes(), Base64.NO_WRAP));
    }

    public static byte[] getDecodeBytes(String strBase64){
        return  Base64.decode(strBase64.getBytes(), Base64.NO_WRAP);
    }
}
