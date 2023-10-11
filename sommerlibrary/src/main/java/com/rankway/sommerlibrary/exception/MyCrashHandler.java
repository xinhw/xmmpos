package com.rankway.sommerlibrary.exception;


import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.rankway.sommerlibrary.app.BaseApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {
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

    private String logFilePath = Environment.getExternalStorageDirectory() + File.separator + "Android" +
            File.separator + "data" + File.separator + BaseApplication.getAppContext().getPackageName() + File.separator + "crashLog";

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
//                FileOutputStream outputStream = null;
//                try {
//                    ByteArrayInputStream inputStream = new ByteArrayInputStream(errorMessage.getBytes());
//
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//                    Date dtnow = new Date();
//                    String filename = String.format("Semicon_%s.log",simpleDateFormat.format(dtnow));
////                    outputStream = new FileOutputStream(new File(file, System.currentTimeMillis() + ".txt"));
//                    outputStream = new FileOutputStream(new File(file, filename));
//
//                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                    String error = "Crash发生在："+simpleDateFormat.format(dtnow);
//                    byte[] arrerror = error.getBytes();
//                    outputStream.write(arrerror);
//
//                    int len = 0;
//                    byte[] bytes = new byte[1024];
//                    while ((len = inputStream.read(bytes)) != -1) {
//                        outputStream.write(bytes, 0, len);
//                    }
//                    outputStream.flush();
//                    Log.e("程序出异常了", "写入本地文件成功：" + file.getAbsolutePath());
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                writeCrashLog(errorMessage);
            }

            private void writeCrashLog(String errorMessage){
                try {
                    FileWriter writer = null;
                    String fileName = logFilePath+File.separator+"SUMGPOSCrash.log";
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
