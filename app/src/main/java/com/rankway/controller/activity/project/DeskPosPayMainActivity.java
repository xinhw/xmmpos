package com.rankway.controller.activity.project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
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
import com.rankway.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
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

    private List<PaymentRecordEntity> records = new ArrayList<>();

    private PaymentDialog paymentDialog = null;

    //  打印缓存信息
    private PaymentRecordEntity printPayRecord = null;
    private List<DishEntity> listPrintDishEntities = new ArrayList<>();

    private PrinterBase printer = null;

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
    }

    private void findViewIdSetOnClickListener(int[] ids){
        for(int id:ids){
            View view = findViewById(id);
            if(null!=view) view.setOnClickListener(this);
        }
    }

    private void initData() {
        Log.d(TAG,"initData");

        listDishTypeEntities.clear();
        listDishTypeEntities = getLocalDishType();
        dishTypeAdapter.notifyDataSetChanged();

        listDishEntities.clear();
        if(listDishTypeEntities.size()>0){
            listDishEntities = getLocalDish(listDishTypeEntities.get(0));
        }
        dishAdapter.notifyDataSetChanged();

        listSelectedDishEntities.clear();
        selectedAdapter.notifyDataSetChanged();
        refreshSubTotal();

        posInfoBean = getPosInfoBean();
        if (null == posInfoBean) {
            Log.d(TAG, "第一次使用，需要配置参数");
            startActivity(MobilePosSettingsActivity.class);
            return;
        }

        tvPosNo.setText(String.format("POS号：%s",posInfoBean.getCposno()));

        tvTime.setText(String.format("时间：%s", DateStringUtils.getCurrentTime()));
        mHandler.sendEmptyMessageDelayed(121,1000);

        printer = PrinterFactory.getPrinter(mContext);
        int ret = printer.openPrinter();
        if(0!=ret){
            playSound(false);
            showLongToast("打印机初始化失败，请检查连接");
        }
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

        //  关闭打印机
        if(null!=printer) printer.closePrinter();
        printer = null;
    }


    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick(v.getId())){
            showToast("请勿连续点击!");
            return;
        }

        switch (v.getId()){
            case R.id.tvExit:
                finish();
                break;

            case R.id.tvClearSelected:
                listSelectedDishEntities.clear();
                selectedAdapter.notifyDataSetChanged();
                playSound(true);
                break;

            case R.id.tvQuatity:
                if(listSelectedDishEntities.size()==0) return;
                if(selectedDishPosition==-1) return;

                setDishQuatityDialog("请输入菜品数量:",selectedDishPosition);
                break;

            case R.id.tvCardPay:
                paymentDialog = new PaymentDialog(mContext,this,posInfoBean,PaymentDialog.PAY_MODE_CARD,0);
                paymentDialog.setOnPaymentResultListner(this);
                paymentDialog.show(getSupportFragmentManager(),"iccard pay");
                break;

            case R.id.tvQRPay:
                paymentDialog = new PaymentDialog(mContext,this,posInfoBean,PaymentDialog.PAY_MODE_QRCODE,0);
                paymentDialog.setOnPaymentResultListner(this);
                paymentDialog.show(getSupportFragmentManager(),"qrcode pay");
                break;

            case R.id.tvPrintAgain:
                if(null!=printPayRecord){
                    PrinterUtils printerUtils = new PrinterUtils();
                    printerUtils.printPayItem(printer,posInfoBean,printPayRecord, listPrintDishEntities);
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
        listDishEntities = getLocalDish(dishTypeEntity);
        if(listDishEntities.size()>0){
            dishRecyclerView.setVisibility(View.VISIBLE);
            noDishView.setVisibility(View.GONE);
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

        listSelectedDishEntities.add(dishEntity);

        dishRecyclerView.scrollToPosition(listSelectedDishEntities.size()-1);
        selectedAdapter.setSelectedItem(listSelectedDishEntities.size()-1);
        selectedAdapter.notifyDataSetChanged();

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
        PopupWindow popupWindow = new PopupWindow(popuView, 400, 800);
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
                    setDishQuatityDialog("请输入菜品数量:",index);
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

        View view = getLayoutInflater().inflate(R.layout.input_dialog_view,null,false);
        TextView textView = view.findViewById(R.id.tvTitle);
        textView.setText(title);
        EditText editText =view.findViewById(R.id.inputEditText);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(2)
        });

        showDialogMessage(null, null,
                "确定", new DialogInterface.OnClickListener() {
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

                        DishEntity dishEntity = listSelectedDishEntities.get(position);
                        dishEntity.setCount(count);
                        selectedAdapter.notifyDataSetChanged();

                        refreshSubTotal();
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                view);

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
    public void onPaymentSuccess(PaymentRecordEntity record) {
        Log.d(TAG,"onPaymentSuccess "+record.toString());
        int n = 1;

        List<PaymentItemEntity> items = new ArrayList<>();
        for(DishEntity dishEntity : listSelectedDishEntities){
            PaymentItemEntity item = new PaymentItemEntity(n,record.getId(), dishEntity);
            items.add(item);
            n++;
        }
        DBManager.getInstance().getPaymentItemEntityDao().saveInTx(items);

        //  清除缓存信息
        listSelectedDishEntities.clear();
        selectedAdapter.notifyDataSetChanged();

        refreshSubTotal();

        //  缓存打印信息
        printPayRecord = record;
        listPrintDishEntities.clear();
        listPrintDishEntities.addAll(listSelectedDishEntities);

        //  打印
        PrinterUtils printerUtils = new PrinterUtils();
        printerUtils.printPayItem(printer,posInfoBean,printPayRecord, listPrintDishEntities);
    }

    @Override
    public void onPaymentCancel() {
        Log.d(TAG,"onPaymentCancel 取消支付");
        playSound(false);
    }
}