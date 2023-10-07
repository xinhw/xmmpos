package com.rankway.controller.hardware.util;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetLog {

    /**
     * 追加文件：使用FileWriter
     * @param content
     */
    public static void writeLog(String TAG,String content) {
        Log.d(TAG,content);
        Log.d("DataConverter",String.format("日志内容：%s",content));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String fileName = String.format("WXPOS%s.txt",simpleDateFormat.format(date));

        String path = Environment.getExternalStorageDirectory() + "/Log/"; //文件路径
        FileWriter writer = null;
        try {
            File file = new File(path);
            if (!file.exists()) {  //没有创建文件夹则创建
                file.mkdirs();
            }
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(path + fileName, true);
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            date = new Date(System.currentTimeMillis());
            String strtime = simpleDateFormat.format(date);

            writer.write(strtime + "\t" +TAG + "\t" + content+"\r\n");
            writer.flush();
            if (writer != null) {
                //关闭流
                writer.close();
            }
        } catch (IOException e) {
            Log.d("DetLog",String.format("写日志失败:%s",e.getMessage()));
            e.printStackTrace();
        }
    }

    /***
     * 整理日志目录，删除1个月之前的所有日志
     */
    public static void zapLog(){
        String path = Environment.getExternalStorageDirectory() + "/Log/"; //文件路径
        File dir = new File(path);
        if(null==dir) return;

        File flist[] = dir.listFiles();
        if(null==flist) return;
        if(flist.length==0)  return;

        //  一个月之前
        Date dtnow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dtnow);
        c.add(Calendar.MONTH,-1);
        long lastmonth = c.getTimeInMillis();

        for(File f:flist){
            // 文件最后修改时间
            long lastModified = f.lastModified();
            if(lastmonth>lastModified){
                Date dtlast = new Date(lastModified);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.d("DetLog","文件："+f.getName()+" 最后修改日期："+simpleDateFormat.format(dtlast).toString());
                f.delete();
            }
        }

        return;
    }

    public static void debugObjectInJSON(String TAG,String strdemo,Object obj){
        String strjson = JSON.toJSONString(obj);
        Log.d(TAG,strdemo + "\t" +strjson);
    }
}
