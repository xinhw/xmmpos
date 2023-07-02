package com.rankway.controller.webapi;

import android.util.Log;

import com.rankway.controller.hardware.util.DataConverter;
import com.rankway.controller.utils.Base64Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class decodeQRCode {
    private static final String TAG = "decodeQRCode";

    final static int MIN_LENGTH = 25;
    public static cardInfo decode(String qrcode){
        Log.d(TAG,"decode("+qrcode+")");

        byte[] data = Base64Util.getDecodeBytes(qrcode);
        if(null==data){
            Log.d(TAG,"二维码无法deBase64:"+qrcode);
            return null;
        }
        if(data.length<MIN_LENGTH){
            Log.d(TAG,"二维码长度太小："+data.length);
            return null;
        }

        if(data[0]!=0x35){
            Log.d(TAG,"TAG1错误："+data[0]);
            return null;
        }
        if(data[1]!=0x31){
            Log.d(TAG,"TAG2错误："+data[1]);
            return null;
        }

        int n = 2;

        byte[] value = null;
        boolean done = false;

        // TLV解析
        try{
            while(true){
                //  Type
                byte type = data[n];
                n++;

                //  Len
                byte len = data[n];
                int nlen = DataConverter.getByteValue(len);
                value = new byte[nlen];
                n++;

                //  Value
                System.arraycopy(data,n,value,0,nlen);
                n = n + nlen;

                if(type!=0x03) continue;
    //            if(value[0]!=0x50) continue;
                if(value[1]!=(byte)0xfd) continue;
                if(value[2]!=0x12) continue;

                done = true;
                break;
            }
        }catch (Exception e){
            done = false;
            data = null;
        }

        if(!done){
            Log.d(TAG,"未解析到支付二维码信息");
            return null;
        }
        if(null==data){
            Log.d(TAG,"支付二维码信息为空");
            return null;
        }
        Log.d(TAG,"信息："+DataConverter.bytes2HexString(value));

        verifyMac(value);

        cardInfo obj = new cardInfo();
        //  SystemId;
        obj.setStatusid(value[3]);
        //  QRType
        obj.setQrType(value[4]);

        //  UserID
        long l = 0;
        for(int i=5;i<13;i++){
            l = l *0x100 + DataConverter.getByteValue(value[i]);
        }
        obj.setUserId(String.format("%d",l));

        //  时间戳
        l = 0;
        for(int i=13;i<17;i++){
            l = l *0x100 + DataConverter.getByteValue(value[i]);
        }
        obj.setTimeStamp(l*1000);

        Date dt = new Date(obj.getTimeStamp());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = format.format(dt);
        Log.d(TAG,"二维码生成时间："+s);

        //  MAC
        byte[] mac = new byte[4];
        System.arraycopy(value,17,mac,0,4);
        obj.setMac(DataConverter.bytes2HexString(mac));

        Log.d(TAG,"解码信息："+obj.toString());

        return obj;
    }

    /***
     * 验证二维码的有效性
     * @param value
     * @return
     */
    private static boolean verifyMac(byte[] value){
        Log.d(TAG,"verifyMac");

        String str = DataConverter.bytes2HexString(value);

        long l1 = Long.parseLong(str.substring(2,10),16);
        long l2 = Long.parseLong(str.substring(10,18),16);
        long l3 = Long.parseLong(str.substring(18,26),16);
        long l4 = Long.parseLong(str.substring(26,34),16);
        long mac = Long.parseLong(str.substring(34),16);

        long l0 = (l1+l2+l3+l4)&0xffffffffL;
        Log.d(TAG,"l0:"+String.format("%08X",l0));
        Log.d(TAG,"mac:"+String.format("%08X",mac));

        boolean b = (l0==mac);
        Log.d(TAG,"Verify:"+b);
        return b;
    }
}
