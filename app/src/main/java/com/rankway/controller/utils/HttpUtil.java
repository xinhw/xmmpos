package com.rankway.controller.utils;

import android.util.Log;

import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class HttpUtil {
    private final String TAG = "HttpUtil";
    private int responseCode;

    public static int DEFAULT_OVER_TIME = 15000;

    private int OVER_TIME_MS = DEFAULT_OVER_TIME;      //  ms

    public HttpUtil(){
        //  通信超时
        int ret = SpManager.getIntance().getSpInt(AppIntentString.HTTP_OVER_TIME);
        if(ret<=0) ret = DEFAULT_OVER_TIME;
        OVER_TIME_MS = ret;
    }

    public String httpPost(String url,String contentType,String content){
        Log.d(TAG,String.format("httpPost(%s,%s)",url,content));

        try {
            URL mUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //  设置链接超时时间
            httpURLConnection.setConnectTimeout(OVER_TIME_MS);
            //  设置读取超时时间
            httpURLConnection.setReadTimeout(OVER_TIME_MS);
            //  设置请求参数
            httpURLConnection.setRequestMethod("POST");
            //  添加Header
            httpURLConnection.setRequestProperty("Connection","Keep-Alive");

            httpURLConnection.setRequestProperty("Content-Type",contentType);

            //  设置发送数据长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));

            //  接收输入流
            httpURLConnection.setDoInput(true);
            //  传递参数时需要开启
            httpURLConnection.setDoOutput(true);
            //  POST方式不能缓存，需要手工设置为False
            httpURLConnection.setUseCaches(false);


            httpURLConnection.getOutputStream().write(content.getBytes());
            httpURLConnection.getOutputStream().flush();
            //  执行完dataOutputStream.close()之后，POST请求结束
            httpURLConnection.getOutputStream().close();

            //  获取代码返回值
            this.responseCode = httpURLConnection.getResponseCode();
            Log.d(TAG,"responseCode:"+responseCode);

            //  获取返回内容类型
            String type = httpURLConnection.getContentType();
            Log.d(TAG,"type:"+type);

            //  获取返回内容的字符编码
            String encoding = httpURLConnection.getContentEncoding();
            Log.d(TAG,"encoding:"+encoding);

            //  获取返回内容长度，单位字节
            int length = httpURLConnection.getContentLength();
            Log.d(TAG,"length:"+length);

//            if(responseCode==200){
                //  获取响应的输入流对象
                InputStream inputStream = null;

                if(responseCode>=400)
                    inputStream = httpURLConnection.getErrorStream();
                else
                    inputStream = httpURLConnection.getInputStream();
                //  创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                //  定义读取的长度
                int len = 0;
                //  定义缓冲区
                byte buffer[] = new byte[1024];

                //  按缓冲区大小，循环读取
                while ((len=inputStream.read(buffer))!=-1){
                    //  根据读取的内容长度写入到os对象中
                    message.write(buffer,0,len);
                }
                //  释放资源
                inputStream.close();
                message.close();

                //  返回字符串
                String msg = new String(message.toByteArray());
                Log.d(TAG,"msg:"+msg);
                return msg;
//            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /***
     * HTTP同步Get方法
     * @param url
     * @return
     */
    public String httpGet(String url){
        Log.d(TAG,"httpGet "+url);
        try{
            URL mUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //  设置请求参数
            httpURLConnection.setRequestMethod("GET");
            //  设置链接超时时间
            httpURLConnection.setConnectTimeout(OVER_TIME_MS);

            httpURLConnection.setUseCaches(false);

            //  设置申请Header内容
            //  httpURLConnection.setRequestProperty("Connection","Keep-Alive");

            Log.d(TAG,httpURLConnection.toString());

            //  获取代码返回值
            this.responseCode = httpURLConnection.getResponseCode();
            Log.d(TAG,"responseCode:"+responseCode);

            //  获取返回内容类型
            String type = httpURLConnection.getContentType();
            Log.d(TAG,"type:"+type);

            //  获取返回内容的字符编码
            String encoding = httpURLConnection.getContentEncoding();
            Log.d(TAG,"encoding:"+encoding);

            //  获取返回内容长度，单位字节
            int length = httpURLConnection.getContentLength();
            Log.d(TAG,"length:"+length);
            if(responseCode==200){
                //  获取响应的输入流对象
                InputStream inputStream = httpURLConnection.getInputStream();
                //  创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                //  定义读取的长度
                int len = 0;
                //  定义缓冲区
                byte buffer[] = new byte[1024];

                //  按缓冲区大小，循环读取
                while ((len=inputStream.read(buffer))!=-1){
                    //  根据读取的内容长度写入到os对象中
                    message.write(buffer,0,len);
                }
                //  释放资源
                inputStream.close();
                message.close();

                //  返回字符串
                String msg = new String(message.toByteArray());
                Log.d(TAG,"msg:"+msg);
                return msg;
            }

        }catch (IOException e){
            e.printStackTrace();
//            Log.d(TAG,e.getMessage());
            return null;
        }
        return null;
    }


    public int getResponseCode(){
        return this.responseCode;
    }


    /***
     *
     * @param url
     * @param contentType
     * @param content
     * @return
     */
    public String httpPost201(String url,String contentType,String content){
        Log.d(TAG,String.format("httpPost(%s,%s)",url,content));

        try {
            URL mUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //  设置链接超时时间
            httpURLConnection.setConnectTimeout(OVER_TIME_MS);
            //  设置读取超时时间
            httpURLConnection.setReadTimeout(OVER_TIME_MS);
            //  设置请求参数
            httpURLConnection.setRequestMethod("POST");
            //  添加Header
            httpURLConnection.setRequestProperty("Connection","Keep-Alive");

            httpURLConnection.setRequestProperty("Content-Type",contentType);

            //  设置发送数据长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));

            //  接收输入流
            httpURLConnection.setDoInput(true);
            //  传递参数时需要开启
            httpURLConnection.setDoOutput(true);
            //  POST方式不能缓存，需要手工设置为False
            httpURLConnection.setUseCaches(false);

            httpURLConnection.getOutputStream().write(content.getBytes());
            httpURLConnection.getOutputStream().flush();
            //  执行完dataOutputStream.close()之后，POST请求结束
            httpURLConnection.getOutputStream().close();

            //  获取代码返回值
            this.responseCode = httpURLConnection.getResponseCode();
            Log.d(TAG,"responseCode:"+responseCode);

            //  获取返回内容类型
            String type = httpURLConnection.getContentType();
            Log.d(TAG,"type:"+type);

            //  获取返回内容的字符编码
            String encoding = httpURLConnection.getContentEncoding();
            Log.d(TAG,"encoding:"+encoding);

            //  获取返回内容长度，单位字节
            int length = httpURLConnection.getContentLength();
            Log.d(TAG,"length:"+length);

            Map<String, List<String>> mapHeaders = httpURLConnection.getHeaderFields();
            Log.d(TAG,"mapHeaders:"+mapHeaders.toString());

            BufferedReader br = null;
            if(responseCode>=400) {
                br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "UTF-8"));
            }else{
                br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            }

            String str = "";
            String msg = "";
            while ((str = br.readLine())!=null){
                msg = msg + str;
            }
            Log.d(TAG,"msg:"+msg);

            setHeaderLocation(null);
            if(responseCode==201){
                String header = httpURLConnection.getHeaderField("Location");
                setHeaderLocation(header);
            }
            return msg;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private String headerLocation;
    public String getHeaderLocation() {
        return headerLocation;
    }
    public void setHeaderLocation(String headerLocation) {
        this.headerLocation = headerLocation;
    }
}
