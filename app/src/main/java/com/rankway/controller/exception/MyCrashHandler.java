package com.rankway.controller.exception;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {
    private final String TAG="MyCrashHandler";

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("程序出现异常了", "Thread = " + t.getName() + "\nThrowable = " + e.getMessage());
        String stackTraceInfo = getStackTraceInfo(e);
        Log.e("stackTraceInfo", stackTraceInfo);
        saveThrowableMessage(stackTraceInfo);
    }
   /**
     * 获取错误的信息
     *
     * @param throwable
     * @return
     */
    private String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();
    }

    private String logFilePath = Environment.getExternalStorageDirectory() + File.separator + "Log" +
            File.separator + "crashLog";

    private void saveThrowableMessage(String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }
        File file = new File(logFilePath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                writeStringToFile(errorMessage, file);
            }
        } else {
            writeStringToFile(errorMessage, file);
        }
    }


    private void writeStringToFile(final String errorMessage, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"崩溃日志："+logFilePath+"\t内容："+errorMessage);

                writeCrashLog(errorMessage);
            }

            private void writeCrashLog(String errorMessage){
                try {
                    FileWriter writer = null;
                    String fileName = logFilePath+File.separator+"WXSemiconCrash.txt";
                    // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                    writer = new FileWriter(fileName, true);

                    //  错误发生日期时间
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");// HH:mm:ss //获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    String strtime = simpleDateFormat.format(date);

                    writer.write(strtime + "\t"  + errorMessage+"\r\n");
                    writer.flush();
                    if (writer != null) {
                        //关闭流
                        writer.close();
                    }
                } catch (IOException e) {
                    Log.d("DetLog",String.format("写日志失败:%s",e.getMessage()));
                    e.printStackTrace();
                    return;
                }
                return;
            }
        }).start();
    }

}
