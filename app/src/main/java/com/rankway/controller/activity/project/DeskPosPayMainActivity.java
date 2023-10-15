package com.rankway.controller.activity.project;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.dialog.PaymentDialog;
import com.rankway.controller.activity.service.AppService;
import com.rankway.controller.adapter.DishAdapter;
import com.rankway.controller.adapter.DishSelectedAdapter;
import com.rankway.controller.adapter.DishSubTypeAdapter;
import com.rankway.controller.adapter.DishTypeAdapter;
import com.rankway.controller.common.AppConstants;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.DishSubTypeEntity;
import com.rankway.controller.persistence.entity.DishTypeEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;
import com.rankway.controller.printer.PrinterBase;
import com.rankway.controller.printer.PrinterFactory;
import com.rankway.controller.printer.PrinterGP58;
import com.rankway.controller.printer.PrinterUtils;
import com.rankway.controller.reader.ReaderCS230Z;
import com.rankway.controller.utils.ClickUtil;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.utils.HttpUtil;

import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

public class DeskPosPayMainActivity
        extends BaseActivity
        implements View.OnClickListener,
        DishSelectedAdapter.OnItemClickListener,
        DishAdapter.OnItemClickListener,
        DishTypeAdapter.OnItemClickListener,
        DishSubTypeAdapter.OnItemClickListener,
        PaymentDialog.OnPaymentResult {

    private final String TAG = "DeskPosPayMainActivity";

    RecyclerView selectedRecyclerView;
    List<DishEntity> listSelectedDishEntities = new ArrayList<>();        //  选中菜品
    DishSelectedAdapter selectedAdapter;
    int selectedDishPosition = -1;

    TextView tvSubCount;
    TextView tvSubAmount;

    RecyclerView dishTypeRecyclerView;
    List<DishTypeEntity> listDishTypeEntities = new ArrayList<>();       //  菜品主类
    DishTypeAdapter dishTypeAdapter;

    RecyclerView dishSubTypeRecyclerView;
    List<DishSubTypeEntity> listDishSubTypeEntities = new ArrayList<>();    //  菜品子类
    DishSubTypeAdapter dishSubTypeAdapter;

    RecyclerView dishRecyclerView;
    List<DishEntity> listDishEntities = new ArrayList<>();              //  菜品明细
    DishAdapter dishAdapter;

    View noDishView;

    private PosInfoBean posInfoBean = null;

    private TextView tvPosNo;
    private TextView tvTime;
    private TextView tvTotalCount;
    private TextView tvTotalAmount;
    float fTotalAmount = 0;

    private List<PaymentRecordEntity> payRecords = new ArrayList<>();

    private PaymentDialog paymentDialog = null;

    //  打印缓存信息
    private PaymentRecordEntity printPayRecord = null;
    private List<DishEntity> listPrintDishEntities = new ArrayList<>();

    private ImageView imgNetworkConnect;
    private ImageView imgNetworkDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_desk_posactivity);

        initView();

        initData();

        checkAppUpdate();

        registerReceiver();
    }

    private void initView() {
        Log.d(TAG, "initView");

        TextView textView = findViewById(R.id.tvTitle);
        textView.setText("上海报业餐厅POS机");

        ImageView imageView = findViewById(R.id.imgSetting);
        imageView.setOnClickListener(this);

        textView = findViewById(R.id.tvSelectedTitle);
        textView.setText("选购菜品");

        tvSubCount = findViewById(R.id.tvSubCount);
        tvSubAmount = findViewById(R.id.tvSubAmount);

        //  选中菜品
        selectedRecyclerView = findViewById(R.id.selectedRecyclerView);
        selectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedAdapter = new DishSelectedAdapter(mContext, listSelectedDishEntities);
        selectedRecyclerView.setAdapter(selectedAdapter);
        selectedAdapter.setOnItemClickListener(this);

        //  菜品主类
        dishTypeRecyclerView = findViewById(R.id.dishTypeRecyclerView);
        setLayoutManager(dishTypeRecyclerView);
        dishTypeAdapter = new DishTypeAdapter(mContext, listDishTypeEntities);
        dishTypeRecyclerView.setAdapter(dishTypeAdapter);
        dishTypeAdapter.setOnItemClickListener(this);

        //  菜品子类
        dishSubTypeRecyclerView = findViewById(R.id.dishSubTypeRecyclerView);
        setLayoutManager(dishSubTypeRecyclerView);
        dishSubTypeAdapter = new DishSubTypeAdapter(mContext,listDishSubTypeEntities);
        dishSubTypeRecyclerView.setAdapter(dishSubTypeAdapter);
        dishSubTypeAdapter.setOnItemClickListener(this);

        //  菜品明细
        noDishView = findViewById(R.id.noDishView);
        dishRecyclerView = findViewById(R.id.dishRecyclerView);
        setLayoutManager(dishRecyclerView);
        dishAdapter = new DishAdapter(mContext, listDishEntities);
        dishRecyclerView.setAdapter(dishAdapter);
        dishAdapter.setOnItemClickListener(this);

        int[] ids = {R.id.tvExit, R.id.tvClearSelected, R.id.tvQuatity,
                R.id.tvCardPay, R.id.tvQRPay, R.id.tvPrintAgain};
        findViewIdSetOnClickListener(ids);

        tvPosNo = findViewById(R.id.tvPosNo);
        tvTime = findViewById(R.id.tvTime);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        imgNetworkConnect = findViewById(R.id.imgNetworkConnect);
        imgNetworkDisconnect = findViewById(R.id.imgNetworkDisconnect);
    }

    private void findViewIdSetOnClickListener(int[] ids) {
        for (int id : ids) {
            View view = findViewById(id);
            if (null != view) view.setOnClickListener(this);
        }
    }

    private void initData() {
        Log.d(TAG, "initData");

        listSelectedDishEntities.clear();
        selectedAdapter.notifyDataSetChanged();
        refreshSubTotal();

        tvTime.setText(String.format("时间：%s", DateStringUtils.getCurrentTime()));
        mHandler.sendEmptyMessageDelayed(121, 1000);

        startAppService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        posInfoBean = getPosInfoBean();
        if (null == posInfoBean) {
            Log.d(TAG, "第一次使用，需要配置参数");
            startActivity(MobilePosSettingsActivity.class);
            return;
        }
        tvPosNo.setText(String.format("POS号：%s", posInfoBean.getCposno()));
        if(!TextUtils.isEmpty(posInfoBean.getPosName())){
            TextView textView = findViewById(R.id.tvTitle);
            textView.setText(String.format("上海报业餐厅POS机--%s",posInfoBean.getPosName()));
        }
        //  菜品主类
        listDishTypeEntities.clear();
        List<DishTypeEntity> dishTypeList = getLocalDishType();
        Log.d(TAG, "dishTypeList " + dishTypeList.size());
        if (dishTypeList.size() > 0) listDishTypeEntities.addAll(dishTypeList);
        dishTypeAdapter.notifyDataSetChanged();

        if(listDishTypeEntities.size()>0) {
            refreshDishTypeItems(0);
        }else{
            listDishSubTypeEntities.clear();
            dishSubTypeAdapter.notifyDataSetChanged();

            listDishEntities.clear();
            dishAdapter.notifyDataSetChanged();
        }

        //  刷新合计
        fTotalAmount = 0;
        for (PaymentRecordEntity record : payRecords)
            fTotalAmount = fTotalAmount + record.getAmount();
        refreshTotalCount();

        //  在线，离线标志
        if (HttpUtil.isOnline) {
            imgNetworkConnect.setVisibility(View.VISIBLE);
            imgNetworkDisconnect.setVisibility(View.GONE);
        } else {
            imgNetworkConnect.setVisibility(View.GONE);
            imgNetworkDisconnect.setVisibility(View.VISIBLE);

            showLongToast("设备离线运行，请检查网络！");
            playSound(false);
        }
    }

    /***
     * 刷新菜品主类
     * @param index
     */
    private void refreshDishTypeItems(int index){
        Log.d(TAG,"refreshDishTypeItems");

        //  菜品子类
        List<DishSubTypeEntity> dishSubTypeList = getLocalDishSubType(listDishTypeEntities.get(index).getId());
        listDishSubTypeEntities.clear();
        listDishSubTypeEntities.addAll(dishSubTypeList);
        dishSubTypeAdapter.notifyDataSetChanged();

        listDishEntities.clear();
        if(listDishSubTypeEntities.size()>0){
            refreshDishSubTypeItems(0);
        }else{
            dishAdapter.notifyDataSetChanged();
        }
    }

    /***
     * 刷新菜品子类
     * @param index
     */
    private void refreshDishSubTypeItems(int index){
        Log.d(TAG,"refreshDishSubTypeItems");

        //  菜品明细
        List<DishEntity> dishList = getLocalDish(listDishSubTypeEntities.get(index));

        listDishEntities.clear();
        if (dishList.size() > 0) {
            dishRecyclerView.setVisibility(View.VISIBLE);
            noDishView.setVisibility(View.GONE);
            listDishEntities.addAll(dishList);
        } else {
            dishRecyclerView.setVisibility(View.GONE);
            noDishView.setVisibility(View.VISIBLE);
        }
        dishAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown " + keyCode);

        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        //USB 拔插动作, 这个方法都会被调用.
        super.onConfigurationChanged(newConfig);
    }

    private void setLayoutManager(RecyclerView mRecyclerView) {
        //设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(DeskPosPayMainActivity.this);
        //flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);//主轴为水平方向，起点在左端。
        //flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);//按正常方向换行
        //justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。

        mRecyclerView.setLayoutManager(flexboxLayoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //  停止服务
        stopAppService();

        unregisterReceiver();

        detSleep(100);

        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick(v.getId())) {
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()) {
            case R.id.tvExit:
                finishPrompt();
                break;

            case R.id.tvClearSelected:
                listSelectedDishEntities.clear();
                selectedAdapter.notifyDataSetChanged();

                refreshSubTotal();

                playSound(true);
                break;

            case R.id.tvQuatity:
                if (listSelectedDishEntities.size() == 0) return;
                if (selectedDishPosition == -1) return;

                setDishQuatityDialog("请输入[%s]数量:", selectedDishPosition);
                break;

            case R.id.tvCardPay:
                if (listSelectedDishEntities.size() == 0) return;
                showPaymentDialog(PaymentDialog.PAY_MODE_CARD);
                break;

            case R.id.tvQRPay:
                if (listSelectedDishEntities.size() == 0) return;
                showPaymentDialog(PaymentDialog.PAY_MODE_QRCODE);
                break;

            case R.id.tvPrintAgain:
                if (null == printPayRecord) break;

                PrinterBase printer = PrinterFactory.getPrinter(mContext);
                int ret = printer.openPrinter();
                if (0 != ret) {
                    playSound(false);
                    showLongToast("打印机初始化失败，请检查连接");
                } else {
                    PrinterUtils printerUtils = new PrinterUtils();
                    printerUtils.printPayItem(printer, posInfoBean, printPayRecord, listPrintDishEntities);
                    printer.closePrinter();
                }
                break;

            case R.id.imgSetting:
                startActivity(DeskPosSettingMenuActivity.class);
                break;
        }
    }

    @Override
    public void onDishTypeItemClick(View view, int position) {
        Log.d(TAG, "onDishTypeItemClick " + position);
        refreshDishTypeItems(position);
    }

    @Override
    public void onDishSubTypeItemClick(View view, int position) {
        Log.d(TAG,"onDishSubTypeItemClick");
        refreshDishSubTypeItems(position);
    }

    @Override
    public void onDishItemClick(View view, int position) {
        Log.d(TAG, "onDishItemClick " + position);

        DishEntity dishEntity = new DishEntity(listDishEntities.get(position));
        dishEntity.setCount(1);
        listSelectedDishEntities.add(dishEntity);

        dishRecyclerView.scrollToPosition(listSelectedDishEntities.size() - 1);
        selectedAdapter.setSelectedItem(listSelectedDishEntities.size() - 1);
        selectedAdapter.notifyDataSetChanged();

        selectedDishPosition = selectedAdapter.getSelectedItem();

        refreshSubTotal();
    }

    /***
     * 统计选择的菜品数量和金额
     */
    private void refreshSubTotal() {
        tvSubCount.setText("0");
        tvSubAmount.setText("0.00");

        if (listSelectedDishEntities.size() == 0) return;

        int totoalAmount = 0;
        for (DishEntity dishEntity : listSelectedDishEntities) {
            totoalAmount = totoalAmount + dishEntity.getPrice() * dishEntity.getCount();
        }
        tvSubCount.setText(listSelectedDishEntities.size() + "");
        tvSubAmount.setText(String.format("%.2f", totoalAmount * 0.01));
    }

    @Override
    public void onSelectedDishItemClick(View view, int position) {
        Log.d(TAG, "onSelectedDishItemClick " + position);

        dishRecyclerView.scrollToPosition(position);
        selectedAdapter.setSelectedItem(position);
        selectedAdapter.notifyDataSetChanged();

        selectedDishPosition = position;
    }

    @Override
    public void onSelectedDishItemLongClick(View view, int position) {
        Log.d(TAG, "onSelectedDishItemLongClick " + position);
        selectedDishPosition = position;

        showPopupMenu(view, position);
    }

    /**
     * 显示右键菜单
     *
     * @param view
     * @param index
     */
    private void showPopupMenu(View view, int index) {
        Log.d(TAG, "showPopupMenu");
        int[] location = new int[2];
        view.getLocationInWindow(location);
        View popuView = getLayoutInflater().inflate(R.layout.popuwindow_view, null, false);
        PopupWindow popupWindow = new PopupWindow(popuView, 200, 300);
        popupWindow.setFocusable(true);

        TextView vw = popuView.findViewById(R.id.delete_item);
        vw.setText("删除");
        vw.setTextSize(26);
        popuView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除条目
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                DetLog.writeLog(TAG, "删除：" + listSelectedDishEntities.get(index).toString());
                listSelectedDishEntities.remove(index);
                selectedAdapter.notifyDataSetChanged();

                refreshSubTotal();

                playSound(true);
                selectedDishPosition = -1;
            }
        });

        vw = popuView.findViewById(R.id.insert_item);
        vw.setText("数量");
        vw.setTextSize(26);
        vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置数量
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    setDishQuatityDialog("请输入[%s]数量:", index);
                }
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, location[1] + 25);
    }

    /**
     * 输入菜品数量
     *
     * @param title
     * @param position
     */
    private void setDishQuatityDialog(String title, int position) {
        Log.d(TAG, "setDishQuatityDialog " + position);

        DishEntity dishEntity = null;

        try {
            dishEntity = listSelectedDishEntities.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (dishEntity == null) return;

        // 展示提示框，进行数据输入
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.input_dialog_view, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(String.format(title, dishEntity.getDishName()));

        EditText editText = view.findViewById(R.id.inputEditText);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(2)
        });

        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = editText.getText().toString().trim();
                int count = 0;
                try {
                    count = Integer.parseInt(str);
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("输入无效！");
                    playSound(false);
                    return;
                }
                dialog.dismiss();

                Log.d(TAG, "修改数量：" + position);
                DishEntity item = listSelectedDishEntities.get(position);
                item.setCount(count);
                selectedAdapter.notifyDataSetChanged();

                DetLog.writeLog(TAG, "修改数量：" + item.toString());

                refreshSubTotal();
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == msg) return;
            switch (msg.what) {
                case 121: //    刷新时间
                    tvTime.setText(String.format("时间：%s", DateStringUtils.getCurrentTime()));
                    mHandler.sendEmptyMessageDelayed(121, 1000);
                    break;
            }
        }
    };

    @Override
    public void onPaymentSuccess(int flag, PaymentRecordEntity record) {
        Log.d(TAG, "onPaymentSuccess " + record.toString());

        //  缓存打印信息
        printPayRecord = record;
        listPrintDishEntities.clear();
        listPrintDishEntities.addAll(listSelectedDishEntities);

        //  清除缓存信息
        listSelectedDishEntities.clear();
        selectedAdapter.notifyDataSetChanged();
        refreshSubTotal();

        //  打印
        PrinterBase printer = PrinterFactory.getPrinter(mContext);
        int ret = printer.openPrinter();
        if (0 != ret) {
            playSound(false);
            showLongToast("打印机初始化失败，请检查连接");
        } else {
            //  打印
            PrinterUtils printerUtils = new PrinterUtils();
            printerUtils.printPayItem(printer, posInfoBean, printPayRecord, listPrintDishEntities);
            detSleep(100);
            printer.closePrinter();
        }

        if (flag == 0x01) {
            imgNetworkConnect.setVisibility(View.VISIBLE);
            imgNetworkDisconnect.setVisibility(View.GONE);
        } else {
            imgNetworkConnect.setVisibility(View.GONE);
            imgNetworkDisconnect.setVisibility(View.VISIBLE);
        }

        //  缓存
        payRecords.add(record);

        fTotalAmount = fTotalAmount + record.getAmount();
        refreshTotalCount();
    }

    private void refreshTotalCount() {
        tvTotalCount.setText("总数：" + payRecords.size());
        tvTotalAmount.setText(String.format("总金额：%.2f", fTotalAmount));
    }

    @Override
    public void onPaymentCancel() {
        Log.d(TAG, "onPaymentCancel 取消支付");
        playSound(false);
    }

    private AppService appService = null;
    /***
     * 服务回调函数
     */
    private ServiceConnection appServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "AppService服务连接");
            AppService.AppBinder appBinder = (AppService.AppBinder) service;
            if (null == appBinder) return;

            appService = appBinder.getService();
            if (null == appService) return;

            appService.setObjects(mContext, DeskPosPayMainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "AppService服务停止");
        }
    };

    /***
     * 启动本地服务AppService
     */
    private void startAppService() {
        Log.d(TAG, "startAppService");
        Intent intentService = new Intent(this, AppService.class);
        intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentService.setAction("scott");
        //  绑定启动服务
        //  onCreate()->onBind()->onStartCommand()
        bindService(intentService, appServiceConnection, BIND_AUTO_CREATE);
        startService(intentService);
    }

    /***
     * 停止本地服务AppService
     */
    private void stopAppService() {
        Log.d(TAG, "stopAppService");
        Intent intentService = new Intent(DeskPosPayMainActivity.this, AppService.class);
        //  解绑停止服务
        //  onBind()->onDestroy()
        if (appService != null) {
            appService = null;
            unbindService(appServiceConnection);
        }
        stopService(intentService);
    }

    /***
     * 显示支付对话框
     * @param type
     */
    private void showPaymentDialog(int type) {
        Log.d(TAG, "showPaymentDialog");

        int nAmount = 0;
        for (DishEntity dishEntity : listSelectedDishEntities) {
            nAmount = nAmount + dishEntity.getSubAmount();
        }

        paymentDialog = new PaymentDialog(mContext, this, posInfoBean, type, nAmount);
        paymentDialog.setOnPaymentResultListner(this);
        paymentDialog.setListDishes(listSelectedDishEntities);
        if (type == PaymentDialog.PAY_MODE_QRCODE) {
            paymentDialog.show(getSupportFragmentManager(), "qrcode pay");
        } else {
            paymentDialog.show(getSupportFragmentManager(), "iccard pay");
        }
    }

    /***
     * 询问是否退出APP
     */
    private void finishPrompt() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("是否要退出APP？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    @TestOnly
    private void makeUploadRecord() {
        List<PaymentRecordEntity> list = DBManager.getInstance().getPaymentRecordEntityDao()
                .queryBuilder()
                .where(PaymentRecordEntityDao.Properties.Id.le(4))
                .where(PaymentRecordEntityDao.Properties.Id.ge(3))
                .list();
        Log.d(TAG, "List size:" + list.size());
        for (PaymentRecordEntity record : list) {
            record.setUploadFlag(PaymentTotal.UNUPLOAD);
        }
        DBManager.getInstance().getPaymentRecordEntityDao().saveInTx(list);
    }


    /***
     * 监听USB设备的插入和拔出
     */
    private class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  USB设备对象
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (null == usbDevice) {
                Log.d(TAG, "usbDevice is null");
                return;
            }

            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:     //  插入USB设备
                    if ((usbDevice.getVendorId() == PrinterGP58.VENDORID)
                            && (usbDevice.getProductId() == PrinterGP58.PRODUCTID)) {
                        Log.d(TAG, "打印机插入");

                        showToast("打印机插入");
                        playSound(true);
                    }
                    if ((usbDevice.getVendorId() == ReaderCS230Z.VENDORID)
                            && (usbDevice.getProductId() == ReaderCS230Z.PRODUCTID)) {
                        Log.d(TAG, "IC卡读卡器插入");

                        showToast("读卡器插入");
                        playSound(true);
                    }
                    if ((usbDevice.getVendorId() == AppConstants.USB_QR_SCAN_VENDOR_ID)
                            && (usbDevice.getProductId() == AppConstants.USB_QR_SCAN_PRODUCT_ID)) {
                        Log.d(TAG, "二维码扫描头插入");

                        showLongToast("二维码扫描头被插入");
                        playSound(false);
                    }
                    if ((usbDevice.getVendorId() == AppConstants.USB_QRSCAN_NLSFR20_VENDOR_ID)
                            && (usbDevice.getProductId() == AppConstants.USB_QRSCAN_NLSFR20_PRODUCT_ID)) {
                        Log.d(TAG, "二维码扫描头插入");

                        showLongToast("二维码扫描头被插入");
                        playSound(false);
                    }
                    break;

                case UsbManager.ACTION_USB_DEVICE_DETACHED:     //  拔出USB设备
                    if ((usbDevice.getVendorId() == PrinterGP58.VENDORID)
                            && (usbDevice.getProductId() == PrinterGP58.PRODUCTID)) {
                        Log.d(TAG, "打印机拔出");

                        showLongToast("打印机被拔出，消费无法打印");
                        playSound(false);
                    }
                    if ((usbDevice.getVendorId() == ReaderCS230Z.VENDORID)
                            && (usbDevice.getProductId() == ReaderCS230Z.PRODUCTID)) {
                        Log.d(TAG, "IC卡读卡器拔出");

                        showLongToast("读卡器被拔出，不支持IC卡消费");
                        playSound(false);
                    }
                    if ((usbDevice.getVendorId() == AppConstants.USB_QR_SCAN_VENDOR_ID)
                            && (usbDevice.getProductId() == AppConstants.USB_QR_SCAN_PRODUCT_ID)) {
                        Log.d(TAG, "二维码扫描头拔出");

                        showLongToast("二维码扫描头被拔出，不支持二维码消费");
                        playSound(false);
                    }
                    if ((usbDevice.getVendorId() == AppConstants.USB_QRSCAN_NLSFR20_VENDOR_ID)
                            && (usbDevice.getProductId() == AppConstants.USB_QRSCAN_NLSFR20_PRODUCT_ID)) {
                        Log.d(TAG, "二维码扫描头拔出");

                        showLongToast("二维码扫描头被拔出，不支持二维码消费");
                        playSound(false);
                    }
                    break;

                default:
                    Log.d(TAG, String.format("未知设备：VENTORID=%d PRODUCTID=%d",
                            usbDevice.getVendorId(),
                            usbDevice.getProductId()));
                    break;
            }
        }
    }

    USBReceiver mUsbReceiver = null;

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        mUsbReceiver = new USBReceiver();
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    private void unregisterReceiver() {
        if (null != mUsbReceiver) {
            mContext.unregisterReceiver(mUsbReceiver);
        }
        mUsbReceiver = null;
    }
}