package com.rankway.controller.utils;

import android.app.Activity;

import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.hardware.callback.HttpCallback;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Sommer on 2015/10/28.
 */
public class AsyncHttpCilentUtil {

    private final String TAG = "AsyncHttpCilentUtil";

    private int timeOut = 5;       //  5秒

    public AsyncHttpCilentUtil(){
        //  通信超时
        int ret = SpManager.getIntance().getSpInt(AppIntentString.HTTP_OVER_TIME);
        if(ret<=0) ret = timeOut;
        timeOut = ret;
    }


    /**
     * Post请求 异步
     * 使用 Callback 回调可返回子线程中获得的网络数据
     *
     * @param url
     * @param params 参数
     */
    public void httpPost(final String url, final Map<String, String> params, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut,TimeUnit.SECONDS)
                    .writeTimeout(timeOut,TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if(params!=null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    formBodyBuilder.add(key, value);
                }
            }

            FormBody formBody = formBodyBuilder.build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(url)
                    .build();
            //Response response = null;
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }

    public void httpPostNew(Activity activity, final String url, final Map<String, String> params, final HttpCallback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut,TimeUnit.SECONDS)
                    .writeTimeout(timeOut,TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if(params!=null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    formBodyBuilder.add(key, value);
                }
            }

            FormBody formBody = formBodyBuilder.build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onFaile(e);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onSuccess(response);
                            }
                        }
                    });
                }
            });
        }).start();
    }
    public final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void httpPostJson(final String url, final String jsonStr, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut,TimeUnit.SECONDS)
                    .writeTimeout(timeOut,TimeUnit.SECONDS)
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory())
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request
                    .Builder()
                    .post(body)
                    .url(url)
                    .build();
            //Response response = null;
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }


    /**
     * Get请求
     */
    public void httpGet(final String url, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut,TimeUnit.SECONDS)
                    .writeTimeout(timeOut,TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();

            Request request = new Request
                    .Builder()
                    .get()
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }


    private class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


    /**
     * 不认证的https访问
     * @param activity
     * @param url
     * @param params
     * @param callback
     */
    public void httpsPost(Activity activity,final String url, final Map<String, String> params, final HttpCallback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut,TimeUnit.SECONDS)
                    .writeTimeout(timeOut,TimeUnit.SECONDS)
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory())
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if(params!=null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    formBodyBuilder.add(key, value);
                }
            }

            FormBody formBody = formBodyBuilder.build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onFaile(e);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onSuccess(response);
                            }
                        }
                    });
                }
            });
        }).start();
    }

    public void httpsPostJson(final String url, final String jsonStr, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .readTimeout(timeOut,TimeUnit.SECONDS)
                    .writeTimeout(timeOut,TimeUnit.SECONDS)
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory())
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request
                    .Builder()
                    .post(body)
                    .url(url)
                    .build();
            //Response response = null;
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }


    /***
     *
     * @param url
     * @param map
     * @param keyname
     * @param file
     * @param callback
     */
    public void httpsPostFile(final String url,
                              final Map<String, String> map,
                              final String keyname, final File file,
                              final Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut,TimeUnit.SECONDS)
                .writeTimeout(timeOut,TimeUnit.SECONDS)
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .sslSocketFactory(createSSLSocketFactory())
                .build();

        MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if(null!=file){
            RequestBody fileBody = RequestBody.create(MediaType.parse("text/plain"),file);
            body.addFormDataPart(keyname,file.getName(),fileBody);
        }

        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                body.addFormDataPart(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        Request request = new Request
                .Builder()
                .post(body.build())
                .url(url)
                .build();

        //Response response = null;
        okHttpClient.newCall(request).enqueue(callback);
    }
}
