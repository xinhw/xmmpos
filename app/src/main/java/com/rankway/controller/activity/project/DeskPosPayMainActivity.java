package com.rankway.controller.activity.project;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
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
import com.rankway.controller.adapter.DishTypeAdapter;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.DishTypeEntity;
import com.rankway.controller.persistence.entity.PaymentItemEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.printer.PrinterBase;
import com.rankway.controller.printer.PrinterFactory;
import com.rankway.controller.printer.PrinterUtils;
import com.rankway.controller.utils.ClickUtil;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.utils.HttpUtil;
import com.rankway.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeskPosPayMainActivity
        extends BaseActivity
        implements View.OnClickListener,
        DishSelectedAdapter.OnItemClickListener,
        DishAdapter.OnItemClickListener,
        DishTypeAdapter.OnItemClickListener,
        PaymentDialog.OnPaymentResult{

    private final String TAG = "DeskPosPayMainActivity";

    RecyclerView selectedRecyclerView;
    List<DishEntity> listSelectedDishEntities = new ArrayList<>();        //  选中的菜品
    DishSelectedAdapter selectedAdapter;
    int selectedDishPosition = -1;

    TextView tvSubCount;
    TextView tvSubAmount;

    RecyclerView dishTypeRecyclerView;
    List<DishTypeEntity> listDishTypeEntities = new ArrayList<>();       //  菜品类别
    DishTypeAdapter dishTypeAdapter;
    int selectedDishTypePosition = -1;

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
    }

    private void initView() {
        Log.d(TAG,"initView");

        TextView textView = findViewById(R.id.tvTitle);
        textView.setText("上海报业餐厅POS机");

        ImageView imageView = findViewById(R.id.imgSetting);
        imageView.setOnClickListener(this);

        textView = findViewById(R.id.tvSelectedTitle);
        textView.setText("选购菜品");

        tvSubCount = findViewById(R.id.tvSubCount);
        tvSubAmount = findViewById(R.id.tvSubAmount);

        //  选中的菜品
        selectedRecyclerView = findViewById(R.id.selectedRecyclerView);
        selectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedAdapter = new DishSelectedAdapter(mContext, listSelectedDishEntities);
        selectedRecyclerView.setAdapter(selectedAdapter);
        selectedAdapter.setOnItemClickListener(this);

        //  菜品种类
        dishTypeRecyclerView = findViewById(R.id.dishTypeRecyclerView);
        setLayoutManager(dishTypeRecyclerView);
        dishTypeAdapter = new DishTypeAdapter(mContext, listDishTypeEntities);
        dishTypeRecyclerView.setAdapter(dishTypeAdapter);
        dishTypeAdapter.setOnItemClickListener(this);

        //  菜品明细
        noDishView = findViewById(R.id.noDishView);
        dishRecyclerView = findViewById(R.id.dishRecyclerView);
        setLayoutManager(dishRecyclerView);
        dishAdapter = new DishAdapter(mContext, listDishEntities);
        dishRecyclerView.setAdapter(dishAdapter);
        dishAdapter.setOnItemClickListener(this);

        int[] ids = {R.id.tvExit,R.id.tvClearSelected,R.id.tvQuatity,
                R.id.tvCardPay,R.id.tvQRPay,R.id.tvPrintAgain};
        findViewIdSetOnClickListener(ids);

        tvPosNo = findViewById(R.id.tvPosNo);
        tvTime = findViewById(R.id.tvTime);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        imgNetworkConnect = findViewById(R.id.imgNetworkConnect);
        imgNetworkDisconnect = findViewById(R.id.imgNetworkDisconnect);
    }

    private void findViewIdSetOnClickListener(int[] ids){
        for(int id:ids){
            View view = findViewById(id);
            if(null!=view) view.setOnClickListener(this);
        }
    }

    private void initData() {
        Log.d(TAG,"initData");

        listSelectedDishEntities.clear();
        selectedAdapter.notifyDataSetChanged();
        refreshSubTotal();

        tvTime.setText(String.format("时间：%s", DateStringUtils.getCurrentTime()));
        mHandler.sendEmptyMessageDelayed(121,1000);

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
        tvPosNo.setText(String.format("POS号：%s",posInfoBean.getCposno()));

        //  菜品种类
        listDishTypeEntities.clear();
        List<DishTypeEntity> dishTypeList = getLocalDishType();
        Log.d(TAG,"dishTypeList "+dishTypeList.size());
        if(dishTypeList.size()>0) listDishTypeEntities.addAll(dishTypeList);
        dishTypeAdapter.notifyDataSetChanged();

        //  菜品明细（选中第一个）
        listDishEntities.clear();
        if(listDishTypeEntities.size()>0){
            List<DishEntity> dishList = getLocalDish(listDishTypeEntities.get(0));
            if(dishList.size()>0) listDishEntities.addAll(dishList);
        }
        if(listDishEntities.size()>0){
            dishRecyclerView.setVisibility(View.VISIBLE);
            noDishView.setVisibility(View.GONE);
            dishAdapter.notifyDataSetChanged();
        }else{
            dishRecyclerView.setVisibility(View.GONE);
            noDishView.setVisibility(View.VISIBLE);
        }

        //  刷新合计
        fTotalAmount = 0;
        for(PaymentRecordEntity record:payRecords) fTotalAmount = fTotalAmount + record.getAmount();
        refreshTotalCount();

        //  在线，离线标志
        if(HttpUtil.isOnline){
            imgNetworkConnect.setVisibility(View.VISIBLE);
            imgNetworkDisconnect.setVisibility(View.GONE);
        }else{
            imgNetworkConnect.setVisibility(View.GONE);
            imgNetworkDisconnect.setVisibility(View.VISIBLE);

            showLongToast("设备离线运行，请检查网络！");
            playSound(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"onKeyDown "+keyCode);

        //  右下角返回键
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }
        if(KeyEvent.KEYCODE_HOME == keyCode){
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private void setLayoutManager(RecyclerView mRecyclerView){
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

        detSleep(100);

        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick(v.getId())){
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()){
            case R.id.tvExit:
                finishPrompt();
                break;

            case R.id.tvClearSelected:
                listSelectedDishEntities.clear();
                selectedAdapter.notifyDataSetChanged();
                playSound(true);
                break;

            case R.id.tvQuatity:
                if(listSelectedDishEntities.size()==0) return;
                if(selectedDishPosition==-1) return;

                setDishQuatityDialog("请输入[%s]数量:",selectedDishPosition);
                break;

            case R.id.tvCardPay:
                if(listSelectedDishEntities.size()==0) return;
                showPaymentDialog(PaymentDialog.PAY_MODE_CARD);
                break;

            case R.id.tvQRPay:
                if(listSelectedDishEntities.size()==0) return;
                showPaymentDialog(PaymentDialog.PAY_MODE_QRCODE);
                break;

            case R.id.tvPrintAgain:
                if(null==printPayRecord) break;

                PrinterBase printer = PrinterFactory.getPrinter(mContext);
                int ret = printer.openPrinter();
                if(0!=ret){
                    playSound(false);
                    showLongToast("打印机初始化失败，请检查连接");
                }else {
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
        Log.d(TAG,"onDishTypeItemClick "+ position);
        if(selectedDishTypePosition==position) return;

        selectedDishTypePosition = position;

        DishTypeEntity dishTypeEntity = listDishTypeEntities.get(position);
        listDishEntities.clear();

        List<DishEntity> dishList = getLocalDish(dishTypeEntity);
        if(dishList.size()>0){
            dishRecyclerView.setVisibility(View.VISIBLE);
            noDishView.setVisibility(View.GONE);
            listDishEntities.addAll(dishList);
        }else{
            dishRecyclerView.setVisibility(View.GONE);
            noDishView.setVisibility(View.VISIBLE);
        }
        dishAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDishItemClick(View view, int position) {
        Log.d(TAG,"onDishItemClick "+ position);
        DishEntity dishEntity = listDishEntities.get(position);

        dishEntity.setCount(1);
        listSelectedDishEntities.add(dishEntity);

        dishRecyclerView.scrollToPosition(listSelectedDishEntities.size()-1);
        selectedAdapter.setSelectedItem(listSelectedDishEntities.size()-1);
        selectedAdapter.notifyDataSetChanged();

        selectedDishPosition = selectedAdapter.getSelectedItem();

        refreshSubTotal();
    }

    /***
     * 统计选择的菜品数量和金额
     */
    private void refreshSubTotal(){
        tvSubCount.setText("0");
        tvSubAmount.setText("0.00");

        if(listSelectedDishEntities.size()==0) return;

        int totoalAmount = 0;
        for(DishEntity dishEntity : listSelectedDishEntities){
            totoalAmount = totoalAmount + dishEntity.getPrice()* dishEntity.getCount();
        }
        tvSubCount.setText(listSelectedDishEntities.size()+"");
        tvSubAmount.setText(String.format("%.2f",totoalAmount*0.01));
    }

    @Override
    public void onSelectedDishItemClick(View view, int position) {
        Log.d(TAG,"onSelectedDishItemClick "+ position);

        dishRecyclerView.scrollToPosition(position);
        selectedAdapter.setSelectedItem(position);
        selectedAdapter.notifyDataSetChanged();

        selectedDishPosition = position;
    }

    @Override
    public void onSelectedDishItemLongClick(View view, int position) {
        Log.d(TAG,"onSelectedDishItemLongClick "+ position);
        selectedDishPosition = position;

        showPopupMenu(view,position);
    }

    /**
     * 显示右键菜单
     * @param view
     * @param index
     */
    private void showPopupMenu(View view,int index){
        Log.d(TAG,"showPopupMenu");
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
                listSelectedDishEntities.remove(index);
                selectedAdapter.notifyDataSetChanged();

                refreshSubTotal();

                playSound(true);
                selectedDishPosition  = -1;
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
                    setDishQuatityDialog("请输入[%s]数量:",index);
                }
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, location[1] + 25);
    }

    /**
     * 输入菜品数量
     * @param title
     * @param position
     */
    private void setDishQuatityDialog(String title,int position){
        Log.d(TAG,"setDishQuatityDialog");

        DishEntity dishEntity = null;

        try {
            dishEntity = listSelectedDishEntities.get(position);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        if(dishEntity==null) return;

        // 展示提示框，进行数据输入
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.input_dialog_view, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(String.format(title,dishEntity.getDishName()));

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
                    ToastUtils.showShort(mContext, "输入无效！");
                    playSound(false);
                    return;
                }
                dialog.dismiss();

                DishEntity item = listSelectedDishEntities.get(position);
                item.setCount(count);
                selectedAdapter.notifyDataSetChanged();

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
            switch (msg.what){
                case 121: //    刷新时间
                    tvTime.setText(String.format("时间：%s", DateStringUtils.getCurrentTime()));
                    mHandler.sendEmptyMessageDelayed(121,1000);
                    break;
            }
        }
    };

    @Override
    public void onPaymentSuccess(int flag,PaymentRecordEntity record) {
        Log.d(TAG,"onPaymentSuccess "+record.toString());
        int n = 1;

        List<PaymentItemEntity> items = new ArrayList<>();
        for(DishEntity dishEntity : listSelectedDishEntities){
            PaymentItemEntity item = new PaymentItemEntity(n,record.getId(), dishEntity);
            items.add(item);
            n++;
        }
        DBManager.getInstance().getPaymentItemEntityDao().saveInTx(items);

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
        if(0!=ret){
            playSound(false);
            showLongToast("打印机初始化失败，请检查连接");
        }else{
            //  打印
            PrinterUtils printerUtils = new PrinterUtils();
            printerUtils.printPayItem(printer,posInfoBean,printPayRecord, listPrintDishEntities);
            detSleep(100);
            printer.closePrinter();
        }

        if(HttpUtil.isOnline){
            imgNetworkConnect.setVisibility(View.VISIBLE);
            imgNetworkDisconnect.setVisibility(View.GONE);
        }else{
            imgNetworkConnect.setVisibility(View.GONE);
            imgNetworkDisconnect.setVisibility(View.VISIBLE);
        }

        //  缓存
        payRecords.add(record);

        fTotalAmount = fTotalAmount + record.getAmount();
        refreshTotalCount();
    }

    private void refreshTotalCount(){
        tvTotalCount.setText("总数："+payRecords.size());
        tvTotalAmount.setText(String.format("总金额：%.2f",fTotalAmount));
    }
    @Override
    public void onPaymentCancel() {
        Log.d(TAG,"onPaymentCancel 取消支付");
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


    private void enumAllUsbDevice(){
        Log.d(TAG,"enumAllUsbDevice");

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d(TAG,"UsbDeviceCount: "+deviceList.size());

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if(null!=device) Log.d(TAG,devicesString(device));

            //获取设备接口
            for (int i = 0; i < device.getInterfaceCount(); ) {
                // 一般来说一个设备都是一个接口，你可以通过getInterfaceCount()查看接口的个数
                // 这个接口上有两个端点，分别对应OUT 和 IN
                UsbInterface usbInterface = device.getInterface(i);
                if(null==usbInterface) continue;

                Log.d(TAG,"UsbInterface Id:"+usbInterface.getId()+" Name:"+usbInterface.getName()
                        + " Class:"+usbInterface.getClass()+" Procotocol:"+usbInterface.getInterfaceProtocol()
                        +" EndPointCount:"+usbInterface.getEndpointCount());

                for(int j=0;j< usbInterface.getEndpointCount();j++){
                    UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                    if(endpoint==null) continue;

                    Log.d(TAG,"UsbEndpoint Address:"+endpoint.getAddress() +
                            " Attributes:"+endpoint.getAttributes() +
                            " Direction:"+endpoint.getDirection());
                }
                break;
            }
        }
    }

    private String devicesString(UsbDevice device){
        StringBuilder builder = new StringBuilder("UsbDevice Name=" + device.getDeviceName() +
                " VendorId=" + device.getVendorId() + " ProductId=" + device.getProductId() +
                " mClass=" + device.getClass() + " mSubclass=" + device.getDeviceSubclass() +
                " mProtocol=" + device.getDeviceProtocol() + " mManufacturerName=" +" mSerialNumber=" +
                " InterfaceCount="+device.getInterfaceCount() +
                "  ");
        return builder.toString();
    }

    /***
     * 显示支付对话框
     * @param type
     */
    private void showPaymentDialog(int type){
        Log.d(TAG,"showPaymentDialog");

        int nAmount = 0;
        for(DishEntity dishEntity:listSelectedDishEntities){
            nAmount = nAmount + dishEntity.getSubAmount();
        }

        paymentDialog = new PaymentDialog(mContext,this,posInfoBean,type,nAmount);
        paymentDialog.setOnPaymentResultListner(this);
        if(type==PaymentDialog.PAY_MODE_QRCODE) {
            paymentDialog.show(getSupportFragmentManager(), "qrcode pay");
        }else{
            paymentDialog.show(getSupportFragmentManager(),"iccard pay");
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
}