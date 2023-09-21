package com.rankway.controller.activity.project;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;

public class PayActivity extends BaseActivity {

    private final String TAG = "PayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            char aChar = (char) event.getUnicodeChar();
            if (aChar != 0) {
                mStringBufferResult.append(aChar);
            }

            mHandler.removeCallbacks(mScanningFishedRunnable);

            Log.d(TAG,"keyCode:"+keyCode);

            //若为回车键，直接返回
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mHandler.post(mScanningFishedRunnable);
            } else {
                //延迟post，若500ms内，有其他事件
                mHandler.postDelayed(mScanningFishedRunnable, 500L);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 二维码信息原始数据容器
     * 参考：https://www.jb51.net/article/224306.htm
     */
    private StringBuilder mStringBufferResult = new StringBuilder();
    private Handler mHandler = new Handler();

    private Runnable mScanningFishedRunnable = new Runnable() {
        @Override
        public void run() {
            String qrcode = mStringBufferResult.toString();
            mStringBufferResult.setLength(0);
            if (TextUtils.isEmpty(qrcode)){
                Log.d(TAG,"qrcode is empty");
                return;
            }
            Log.d(TAG,"qrcode:"+qrcode);

//            // 扫码确定参数
//            Gson gson = new Gson();
//            try {
//                qrCodeBean = gson.fromJson(qrcode, QRCode.class);
//                // 你的代码...
//                // 如果要支持中文，数据用可以URLEncoder/URLDecoder编解码
//            } catch (JsonSyntaxException e) {
//                // 解析失败...
//            } catch (UnsupportedEncodingException e) {
//                // 解码失败...
//            } finally {
//                mStringBufferResult.setLength(0);
//            }

        }
    };

}