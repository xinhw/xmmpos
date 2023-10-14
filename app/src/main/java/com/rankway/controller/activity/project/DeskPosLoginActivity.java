package com.rankway.controller.activity.project;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.DishTypeEntity;
import com.rankway.controller.persistence.entity.PersonInfoEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.entity.UserInfoEntity;
import com.rankway.controller.persistence.gen.UserInfoEntityDao;
import com.rankway.controller.utils.ClickUtil;
import com.rankway.controller.webapi.menu.Result;
import com.rankway.controller.webapi.payWebapi;
import com.rankway.controller.webapi.posAudit;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeskPosLoginActivity
        extends BaseActivity
        implements View.OnClickListener {

    final String TAG = "DeskPosLoginActivity";

    TextView tvTitle;
    EditText etUserCode;
    EditText etPassword;
    TextView tvAppVersion;
    TextView tvProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_pos_login);

        initView();

        initData();
    }

    private void initView() {
        Log.d(TAG, "initView");

        tvTitle = findViewById(R.id.tvTitle);
        etUserCode = findViewById(R.id.etUserCode);
        etPassword = findViewById(R.id.etPassword);
        tvAppVersion = findViewById(R.id.tvAppVersion);
        tvProcess = findViewById(R.id.tvProcess);

        TextView textView = findViewById(R.id.tvLogin);
        textView.setOnClickListener(this);
        textView = findViewById(R.id.tvExit);
        textView.setOnClickListener(this);

        etUserCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                etPassword.setText("");
            }
        });
    }

    private void initData() {
        Log.d(TAG, "initData");

        //  程序版本
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvAppVersion.setText(String.format("程序版本：1.0." + versionCode));

        //  上次缓存的用户名
        String str = SpManager.getIntance().getSpString(AppIntentString.LAST_LOGIN_USER);
        if (StringUtils.isEmpty(str)) {
            etUserCode.setText("");
        } else {
            etUserCode.setText(str);
        }
        str = SpManager.getIntance().getSpString(AppIntentString.LAST_LOGIN_PASSWORD);
        if (StringUtils.isEmpty(str)) {
            etPassword.setText("");
        } else {
            etPassword.setText(str);
        }
        tvProcess.setText("");

        beginSyncDataTask();

        int nDBVer = DBManager.getInstance().getDatabaseVerion();
        String nAppVer = "";
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            nAppVer = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        DetLog.writeLog(TAG, "启动程序，版本号：" + nAppVer + " 数据版本号：" + nDBVer);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick(v.getId())) {
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()) {
            case R.id.tvLogin:
                if (!verifyUserCode()) {
                    playSound(false);
                    break;
                }

                playSound(true);
                startActivity(DeskPosPayMainActivity.class);
                finish();

                break;

            case R.id.tvExit:
                finish();
                break;

            default:
                break;
        }
    }

    /***
     * 验证操作员密码
     * @return
     */
    private boolean verifyUserCode() {
        String struserid = etUserCode.getText().toString().trim();
        if (StringUtils.isEmpty(struserid)) {
            showToast("请输入操作员代码");
            return false;
        }
        String strpassword = etPassword.getText().toString();

        //  超级用户
        if ((struserid.equalsIgnoreCase("99521"))
                && (isAdvancedPasswordRight(strpassword))) {
            DetLog.writeLog(TAG, "Admin用户登录");
            return true;
        }

        //  普通用户
        UserInfoEntity user = DBManager.getInstance().getUserInfoEntityDao()
                .queryBuilder()
                .where(UserInfoEntityDao.Properties.UserCode.eq(struserid))
                .unique();
        if (null == user) {
            showToast("操作员代码或密码错误");
            return false;
        }

        if (user.getUserPassword().equalsIgnoreCase(strpassword)) {
            //  缓存登录用户名和密码
            SpManager.getIntance().saveSpString(AppIntentString.LAST_LOGIN_USER,
                    etUserCode.getText().toString().trim());
            SpManager.getIntance().saveSpString(AppIntentString.LAST_LOGIN_PASSWORD,
                    etPassword.getText().toString().trim());

            return true;
        }
        showToast("操作员代码或密码错误");
        return false;
    }

    /***
     * 开启数据同步任务
     */
    private void beginSyncDataTask() {
        AsynSyncData task = new AsynSyncData();
        task.execute();
    }

    /***
     * 后台同步数据的异步任务
     * 		1.操作员信息
     * 		2.菜品种类和菜品明细（只下载上架的菜品）
     * 		3.IC卡黑名单
     * 		4.二维码黑名单
     * 	    5.上传离线交易
     */
    private class AsynSyncData extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("同步中,请稍等...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int n = 0;

            payWebapi obj = payWebapi.getInstance();
            PosInfoBean posInfoBean = getPosInfoBean();
            if (null != posInfoBean) {
                obj.setServerIP(posInfoBean.getServerIP());
                obj.setPortNo(posInfoBean.getPortNo());

                obj.setMenuServerIP(posInfoBean.getMenuServerIP());
                obj.setMenuPortNo(posInfoBean.getMenuPortNo());

                //  获取POS机流水号
                posAudit audit = obj.getPosAuditNo(posInfoBean.getCposno());
                if (null != audit) {
                    //  设置POS流水号
                    posInfoBean.setAuditNo(audit.getPosCno());
                }
                savePosInfoBean(posInfoBean);
            }

            //  1. 操作员信息
            sendProccessMessage("同步 操作员信息，请稍等...");
            List<UserInfoEntity> listUserInfo = obj.getUserInfoList();
            if (listUserInfo == null) {
                sendProccessMessage("同步 操作员信息 失败");
            } else {
                sendProccessMessage("同步 操作员信息 成功");
                Log.d(TAG, "操作员个数：" + listUserInfo.size());
                DBManager.getInstance().getUserInfoEntityDao().deleteAll();
                DBManager.getInstance().getUserInfoEntityDao().saveInTx(listUserInfo);
                n++;
            }

            //  2. 菜品种类和菜品明细（只下载上架的菜品）
            String posno = "";
            if (null != posInfoBean) posno = posInfoBean.getCposno();
            sendProccessMessage("同步 菜品信息，请稍等...");
            Result result = obj.getDishType(posno);
            if (null == result) {
                sendProccessMessage("同步 菜品信息 失败");
            } else {
                sendProccessMessage("同步 菜品信息 成功");
                saveDishType(result);
                n++;
            }

            //  3. IC卡白名单
            sendProccessMessage("同步 IC卡白名单信息，请稍等...");
            List<PersonInfoEntity> listCardBlist = obj.getPersonInfoList();
            if (null == listCardBlist) {
                sendProccessMessage("同步 IC卡白名单信息 失败");
            } else {
                sendProccessMessage("同步 IC卡白名单信息 成功");
                Log.d(TAG, "IC卡白名单个数：" + listCardBlist.size());
                DBManager.getInstance().getPersonInfoEntityDao().deleteAll();
                DBManager.getInstance().getPersonInfoEntityDao().saveInTx(listCardBlist);
                n++;
            }

            //  4. 二维码黑名单
            sendProccessMessage("同步 二维码黑名单信息，请稍等...");
            List<QrBlackListEntity> listQrBlacklist = obj.getQrBlackList();
            if (null == listQrBlacklist) {
                sendProccessMessage("同步 二维码黑名单信息 失败");
            } else {
                sendProccessMessage("同步 二维码黑名单信息 成功");
                Log.d(TAG, "二维码黑名单个数：" + listQrBlacklist.size());
                DBManager.getInstance().getQrBlackListEntityDao().deleteAll();
                DBManager.getInstance().getQrBlackListEntityDao().saveInTx(listQrBlacklist);
                n++;
            }

            //  5. 上传离线交易
            sendProccessMessage("上传 离线交易，请稍等...");
            uploadPaymentRecords();
            sendProccessMessage("上传 离线交易 完成");

            //  上次同步时间
            if (4 == n) {
                sendProccessMessage("同步 完成");
                setLongInfo(AppIntentString.LAST_SYNC_TIME, System.currentTimeMillis());
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            missProDialog();
        }
    }

    private void sendProccessMessage(String msg) {
        Message message = new Message();
        message.what = 100;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == msg) return;
            switch (msg.what) {
                case 100:       //  显示对话框信息
                    String str = (String) msg.obj;
                    if (null != str) {
                        setProDialogText(str);
                        tvProcess.setText(str);
                    }
                    break;

                default:
                    break;
            }
        }
    };


    /**
     * 生成测试擦品种类和菜品
     */
    private void newTestDish() {
        Log.d(TAG, "newTestDish");

        DBManager.getInstance().getDishEntityDao().deleteAll();
        DBManager.getInstance().getDishTypeEntityDao().deleteAll();

        //  荤菜
        DishTypeEntity dishTypeEntity = new DishTypeEntity("10001", "大荤");
        DBManager.getInstance().getDishTypeEntityDao().save(dishTypeEntity);

        DishEntity dishEntity = new DishEntity("10001-01", "虾仁豆腐炖蛋", 190, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-02", "砂锅鱼头煲", 180, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-03", "农家小炒肉", 170, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-04", "蛤蜊炖蛋", 160, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-05", "海鲜毛血旺", 110, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-06", "香煎鲍鱼", 300, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-07", "炸里脊", 250, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-08", "鱼香茄子", 200, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-09", "红烧肉", 210, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-10", "回锅肉", 120, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("10001-11", "清蒸鲈鱼", 210, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        //  小荤
        dishTypeEntity = new DishTypeEntity("20001", "小荤");
        DBManager.getInstance().getDishTypeEntityDao().save(dishTypeEntity);

        dishEntity = new DishEntity("20001-01", "西红柿炒鸡蛋", 110, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("20001-02", "干锅花菜", 120, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("20001-03", "千叶豆腐", 130, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        //  素菜
        dishTypeEntity = new DishTypeEntity("30001", "素菜");
        DBManager.getInstance().getDishTypeEntityDao().save(dishTypeEntity);

        dishEntity = new DishEntity("30001-01", "蒜蓉菠菜", 50, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("30001-02", "清炒鸡毛菜", 40, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("30001-03", "红烧土豆", 70, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        //  主食
        dishTypeEntity = new DishTypeEntity("40001", "主食");
        DBManager.getInstance().getDishTypeEntityDao().save(dishTypeEntity);

        dishEntity = new DishEntity("40001-01", "米饭", 10, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("40001-02", "馒头", 5, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("40001-03", "葱油拌面", 50, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        //  汤
        dishTypeEntity = new DishTypeEntity("50001", "汤");
        DBManager.getInstance().getDishTypeEntityDao().save(dishTypeEntity);

        dishEntity = new DishEntity("50001-01", "西红柿蛋汤", 150, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

        dishEntity = new DishEntity("50001-02", "榨菜紫菜汤", 100, dishTypeEntity);
        DBManager.getInstance().getDishEntityDao().save(dishEntity);

    }


}