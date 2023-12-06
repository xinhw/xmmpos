package com.rankway.controller.activity.project;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.eventbus.MessageEvent;
import com.rankway.controller.activity.project.manager.DataCleanManager;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppConstants;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.utils.AsyncHttpCilentUtil;
import com.rankway.controller.utils.HttpUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 本机设置界面
 */
public class MobilePosSettingsActivity
        extends BaseActivity
        implements View.OnClickListener {

    private final String TAG = "MobilePosSettingsActivity";

    private TextView tvPosName;
    private TextView tvPosNo;
    private TextView tvUserCode;
    private TextView tvAuditNo;
    private TextView tvServerIP;
    private TextView tvServerPort;
    private TextView tvHttpTimeout;
    private TextView tvOfflineMaxAmount;
    private TextView menuServerIP;
    private TextView menuServerPort;
    private TextView tvPrintHeader;
    private TextView appUpgradeUrl;

    private boolean passAdvancedPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        initSupportActionBar(R.string.home_local_setting);

        initView();

        initData();
    }

    private void initView() {
        int[] viewIds = {R.id.viewPosNo, R.id.viewUserCode,
                R.id.viewServerIP, R.id.viewServerPort,
                R.id.upload_log, R.id.about,
                R.id.recover_data,
                R.id.tvHttpTimeout, R.id.tvOfflineMaxAmount,
                R.id.menuServerIP, R.id.menuServerPort,
                R.id.tvPrintHeader, R.id.tvPrintSuffix,
                R.id.appUpgradeUrl};
        setOnClickListener(viewIds);

        tvPosName = findViewById(R.id.posname);
        tvPosNo = findViewById(R.id.posno);
        tvUserCode = findViewById(R.id.usercode);
        tvAuditNo = findViewById(R.id.auditNo);
        tvServerIP = findViewById(R.id.serverIP);
        tvServerPort = findViewById(R.id.serverPort);
        tvHttpTimeout = findViewById(R.id.tvHttpTimeout);
        tvOfflineMaxAmount = findViewById(R.id.tvOfflineMaxAmount);
        tvPrintHeader = findViewById(R.id.tvPrintHeader);

        menuServerIP = findViewById(R.id.menuServerIP);
        menuServerPort = findViewById(R.id.menuServerPort);

        appUpgradeUrl = findViewById(R.id.appUpgradeUrl);

    }

    protected void setOnClickListener(int[] viewIds) {
        for (int id : viewIds) {
            View view = findViewById(id);
            view.setOnClickListener(this);
        }
        return;
    }

    private void initData() {
        Log.d(TAG, "initData");

        PosInfoBean infoBean = getPosInfoBean();
        if (null == infoBean) {
            String str = "";
            tvPosName.setText(str);
            tvPosNo.setText(str);
            tvUserCode.setText(str);
            tvAuditNo.setText(str);
            tvServerIP.setText(str);
            tvServerPort.setText(str);
            menuServerIP.setText("");
            menuServerPort.setText("");
            appUpgradeUrl.setText("");
        } else {
            tvPosName.setText(infoBean.getCposno());
            tvPosNo.setText(infoBean.getCposno());
            tvUserCode.setText(infoBean.getUsercode());
            tvAuditNo.setText(infoBean.getAuditNo() + "");
            tvServerIP.setText(infoBean.getServerIP());
            tvServerPort.setText(infoBean.getPortNo() + "");
            menuServerIP.setText(infoBean.getMenuServerIP());
            menuServerPort.setText(infoBean.getMenuPortNo() + "");
            appUpgradeUrl.setText(infoBean.getUpgradeUrl());
        }

        //  通信超时
        int ret = SpManager.getIntance().getSpInt(AppIntentString.HTTP_OVER_TIME);
        if (ret <= 0) ret = HttpUtil.DEFAULT_OVER_TIME;
        tvHttpTimeout.setText(ret + "");

        //  脱机消费最大金额
        ret = SpManager.getIntance().getSpInt(AppIntentString.OFFLINE_MAX_AMOUNT);
        if (ret <= 0) ret = 30;
        tvOfflineMaxAmount.setText(ret + "");

        //  打印头
        String str = SpManager.getIntance().getSpString(AppIntentString.PRINTER_HEADER);
        if (TextUtils.isEmpty(str)) str = "上海报业大厦餐厅";
        tvPrintHeader.setText(str);

        return;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:
                startActivity(AboutActivity.class);
                break;

            case R.id.upload_log:
                selectLogToPost();
                break;

            case R.id.viewPosNo:
            case R.id.viewUserCode:
            case R.id.viewServerIP:
            case R.id.viewServerPort:
            case R.id.tvHttpTimeout:
            case R.id.tvOfflineMaxAmount:
            case R.id.menuServerIP:
            case R.id.menuServerPort:
            case R.id.appUpgradeUrl:
                showAdvanedSetting(v.getId());
                break;

            case R.id.checkall:
                for (CheckBox cb : checkBoxs) {
                    cb.setChecked(true);
                }
                break;

            case R.id.uncheckall:
                for (CheckBox cb : checkBoxs) {
                    cb.setChecked(false);
                }


            case R.id.recover_data:
                showCleanDialog();
                break;

            case R.id.tvPrintHeader:
                showPrintHeaderDialog();
                break;

            case R.id.tvPrintSuffix:
                showPrintSuffixDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private AlertDialog alertDialog;
    private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();

    private void selectLogToPost() {
        final String TAG = "LOG";

        //  日志文件路径
        String path = Environment.getExternalStorageDirectory() + "/Log/"; //文件路径
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss //获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date dtnow = new Date();

        //  7日内的日志文件名称
        ArrayList<String> logFiles = new ArrayList<String>();
        ArrayList<String> lstLogs = new ArrayList<String>();

        Calendar c = Calendar.getInstance();
        int n = 0;

        //  删除合并的日志文件
        File[] subFiles = new File(path).listFiles();
        if (null != subFiles) {
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) continue;

                String filename = subFile.getName();
                if ("MERGELOG".equalsIgnoreCase(filename.substring(0, 8))) {
                    Log.d(TAG, "删除合并日志文件：" + filename);
                    subFile.delete();
                }
            }
        }
        //  一个月之内，最新7个日志
        for (int i = 0; i < 30; i++) {
            c.setTime(dtnow);
            c.add(Calendar.HOUR, -24 * i);
            long lastmillis = c.getTimeInMillis();
            Date dtlast = new Date(lastmillis);

            String fileName = path + String.format("WXPOS%s.txt", simpleDateFormat.format(dtlast));
            File logfile = new File(fileName);
            if (logfile.exists()) {
                Log.d(TAG, "日志文件：" + fileName);
                logFiles.add(fileName);
                lstLogs.add(sdf.format(dtlast));
                n++;
            }
//            if(n>=MAX_LOG_FILE_NUM) break;
        }
        if (0 == n) {
            showToast("无日志需要上传");
            playSound(false);
            return;
        }

        // 动态加载布局
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
                R.layout.item_checkbox_group, null);
        TextView tv = view.findViewById(R.id.title);
        tv.setText("请选择日志：");

        tv = view.findViewById(R.id.checkall);
        tv.setOnClickListener(this);
        tv = view.findViewById(R.id.uncheckall);
        tv.setOnClickListener(this);

        LinearLayout linearLayout = view.findViewById(R.id.linelayout_group);
        linearLayout.removeAllViews();

        // 给指定的checkbox赋值
        checkBoxs.clear();
        for (int i = 0; i < logFiles.size(); i++) {
            // 先获得checkbox.xml的对象
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(
                    R.layout.checkbox, null);
            checkBox.setTextSize(18);
            checkBoxs.add(checkBox);
            checkBoxs.get(i).setText(lstLogs.get(i));

            // 实现了在
            linearLayout.addView(checkBox, i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                ArrayList<String> selectedLogs = new ArrayList<String>();
                for (int i = 0; i < logFiles.size(); i++) {
                    if (checkBoxs.get(i).isChecked()) {
                        Log.d(TAG, "选中上传日志：" + logFiles.get(i));
                        selectedLogs.add(logFiles.get(i));
                    }
                }
                if (0 == selectedLogs.size()) {
                    Log.d(TAG, "未选中上传日志！");
                    playSound(false);
                    return;
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String destFile = path + String.format("MERGELOG%s.zip", simpleDateFormat.format(dtnow));

                //  合并日志
                // mergeLogFiles(selectedLogs,destFile);

                zipFiles(logFiles, destFile);

                File logfile = new File(destFile);
                if (!logfile.exists()) {
                    Toast.makeText(MobilePosSettingsActivity.this, "无 日志 需要上传！", Toast.LENGTH_SHORT).show();
                    playSound(false);
                    return;
                }
                //  上传日志
                postLogFile(selectedLogs, logfile);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    /***
     *
     * @param logfile
     */
    private void postLogFile(final ArrayList<String> logfiles, final File logfile) {
        //  上传地址
        String url = getUploadLogUrl();
        if(StringUtils.isEmpty(url)) return;

        //  HTTP请求
        ProgressDialog pDialog = ProgressDialog.show(mContext, "提示", "请稍等...", true, false);
        pDialog.show();

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpsPostFile(url, null, "file", logfile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContext, "日志 上传失败", Toast.LENGTH_LONG).show();
                playSound(false);
                if (null != pDialog) pDialog.dismiss();
                logfile.delete();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Toast.makeText(mContext, "日志 上传成功", Toast.LENGTH_LONG).show();
                playSound(true);
                //  将合并的原始日志文件删除
                for (String filename : logfiles) {
                    File file = new File(filename);
                    Log.d("LOG", "删除日志文件：" + filename);
                    file.delete();
                }

                if (null != pDialog) pDialog.dismiss();
                logfile.delete();
            }
        });
    }

    /***
     * 参数设置先验证高级密码，如果密码正确才能设置
     * @param type
     */
    private void showAdvanedSetting(int type) {
        //  如果通过了高级密码验证，直接设置
        if (passAdvancedPassword) {
            inputParam(type);
            return;
        }

        // 展示提示框，进行数据清除
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_intput, null);
        EditText editPossword = view.findViewById(R.id.edit_msg);
        editPossword.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(view);
        builder.setTitle("请输入设置密码:");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String possword = editPossword.getText().toString().trim();
                if (TextUtils.isEmpty(possword)) {
                    showToast("请输入密码后编辑！");
                } else {
                    if (isAdvancedPasswordRight(possword)) {
                        passAdvancedPassword = true;
                        inputParam(type);
                    } else {
                        showToast("密码错误！");
                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void inputParam(int type) {
        // 展示提示框，进行数据输入
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_intput, null);
        EditText editMsg = view.findViewById(R.id.edit_msg);

        switch (type) {
            case R.id.viewPosNo:
                builder.setTitle("POS机号：");
                editMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(5)
                });
                break;

            case R.id.viewUserCode:
                builder.setTitle("操作员号：");
                editMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(5)
                });
                break;

            case R.id.viewServerIP:
                builder.setTitle("支付服务器IP：");
                editMsg.setInputType(InputType.TYPE_CLASS_TEXT);
                String digits = "0123456789.";
                editMsg.setKeyListener(DigitsKeyListener.getInstance(digits));
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(15)
                });
                break;

            case R.id.viewServerPort:
                builder.setTitle("支付服务器端口：");
                editMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(5)
                });
                break;

            case R.id.menuServerIP:
                builder.setTitle("菜品服务器IP：");
                editMsg.setInputType(InputType.TYPE_CLASS_TEXT);
                String digits1 = "0123456789.";
                editMsg.setKeyListener(DigitsKeyListener.getInstance(digits1));
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(15)
                });
                break;

            case R.id.menuServerPort:
                builder.setTitle("菜品服务器端口：");
                editMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(5)
                });
                break;

            case R.id.tvHttpTimeout:
                builder.setTitle("通信超时时间(ms)：");
                editMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(5)
                });
                break;

            case R.id.tvOfflineMaxAmount:
                builder.setTitle("脱机消费最大金额(元)：");
                editMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(3)
                });
                break;

            case R.id.appUpgradeUrl:
                builder.setTitle("APP升级地址：");
                editMsg.setInputType(InputType.TYPE_CLASS_TEXT);
//                String digits12 = "0123456789.:";
//                editMsg.setKeyListener(DigitsKeyListener.getInstance(digits12));
                editMsg.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(21)
                });
                break;
        }

        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg = editMsg.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    showToast("请输入信息！");
                    return;
                }
                PosInfoBean bean = getPosInfoBean();
                if (null == bean) bean = new PosInfoBean();

                switch (type) {
                    case R.id.viewPosNo:
                        tvPosNo.setText(msg);
                        bean.setCposno(msg);

                        //  刷新开结班POS机号
                        if(null!=DeskPosLoginActivity.getShiftEntity()) {
                            DeskPosLoginActivity.getShiftEntity().setPosNo(msg);
                            savePaymentShiftEntity(DeskPosLoginActivity.getShiftEntity());
                        }
                        break;

                    case R.id.viewUserCode:
                        tvUserCode.setText(msg);
                        bean.setUsercode(msg);
                        break;

                    case R.id.viewServerIP:
                        tvServerIP.setText(msg);
                        bean.setServerIP(msg);
                        break;

                    case R.id.viewServerPort:
                        tvServerPort.setText(msg);
                        bean.setPortNo(Integer.parseInt(msg));
                        break;

                    case R.id.menuServerIP:
                        menuServerIP.setText(msg);
                        bean.setMenuServerIP(msg);
                        break;

                    case R.id.menuServerPort:
                        menuServerPort.setText(msg);
                        bean.setMenuPortNo(Integer.parseInt(msg));
                        break;

                    case R.id.tvHttpTimeout:
                        try {
                            int ret = Integer.parseInt(msg);
                            SpManager.getIntance().saveSpInt(AppIntentString.HTTP_OVER_TIME, ret);
                            tvHttpTimeout.setText(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.tvOfflineMaxAmount:
                        try {
                            int ret = Integer.parseInt(msg);
                            SpManager.getIntance().saveSpInt(AppIntentString.OFFLINE_MAX_AMOUNT, ret);
                            tvOfflineMaxAmount.setText(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.appUpgradeUrl:
                        appUpgradeUrl.setText(msg);
                        bean.setUpgradeUrl(msg);
                        break;
                }
                savePosInfoBean(bean);

                showToast("设置成功！");
                playSound(true);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void showCleanDialog() {
        // 展示提示框，进行数据清除
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_intput, null);
        EditText editPassword = view.findViewById(R.id.edit_msg);
        builder.setTitle("请输入清除密码:");
        builder.setView(view);
        builder.setPositiveButton("确认", (dialog, which) -> {
            String password = editPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                showToast("请输入密码！");
            } else {
                if (password.equals(AppConstants.CLEAN_DATA_PASSWORD)) {
                    resetAppData();
                } else {
                    showToast("输入密码有误！");
                }
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    /***
     * 删除APP在/data/data/packageName下的信息
     * 重新启动APP
     */
    private void resetAppData() {
        DataCleanManager.deleteFile(new File("data/data/" + getPackageName()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("已恢复出厂设置，需要重启应用后生效!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //  重启APP
                ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                manager.restartPackage(getPackageName());
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    /***
     * 输入打印标题
     */
    private void showPrintHeaderDialog() {
        // 展示提示框，进行数据清除
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_intput, null);
        EditText editPassword = view.findViewById(R.id.edit_msg);
        builder.setTitle("请输入打印标题:");
        builder.setView(view);
        editPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        editPassword.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(8)
        });

        builder.setPositiveButton("确认", (dialog, which) -> {
            String password = editPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                showToast("请输入打印标题！");
                playSound(false);
            } else {
                tvPrintHeader.setText(password);

                SpManager.getIntance().saveSpString(AppIntentString.PRINTER_HEADER, password);
                playSound(true);

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void showPrintSuffixDialog() {
        // 展示提示框，进行数据清除
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_intput, null);
        EditText editPassword = view.findViewById(R.id.edit_msg);
        builder.setTitle("请输入打印后缀:");
        builder.setView(view);
        editPassword.setInputType(InputType.TYPE_CLASS_TEXT);

        String s = SpManager.getIntance().getSpString(AppIntentString.PRINTER_SUFFIX);
        if (!TextUtils.isEmpty(s)) editPassword.setText(s);

        builder.setPositiveButton("确认", (dialog, which) -> {
            String password = editPassword.getText().toString().trim();
            SpManager.getIntance().saveSpString(AppIntentString.PRINTER_SUFFIX, password);
            playSound(true);

            dialog.dismiss();
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}