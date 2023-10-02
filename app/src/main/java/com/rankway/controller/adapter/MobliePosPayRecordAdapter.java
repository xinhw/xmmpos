package com.rankway.controller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.utils.DateStringUtils;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class MobliePosPayRecordAdapter
        extends RecyclerView.Adapter<MobliePosPayRecordAdapter.PaymentRecordViewHolder> {

    private final String TAG = "MobliePosPayRecordAdapter";
    private List<PaymentRecordEntity> data;
    private int selectedItem = -1;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private long lastClickTime = 0;
    private final int MIN_CLICK_INTERVAL = 500; //

    public MobliePosPayRecordAdapter(Context context, List<PaymentRecordEntity> records){
        this.mContext = context;
        this.data = records;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int i) {
        Log.d(TAG,"setSelectedItem选中的是："+i);
        this.selectedItem = i;
    }


    private int getMyColor(int green) {
        return mContext.getResources().getColor(green);
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public PaymentRecordViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_payment_record,viewGroup,false);
        PaymentRecordViewHolder holder = new PaymentRecordViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull PaymentRecordViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.itemView.setSelected(i==selectedItem);
        PaymentRecordEntity item = data.get(i);
        holder.auditNo.setText("序号:"+item.getAuditNo());
        holder.workNo.setText("工号:"+item.getWorkNo());
        holder.workName.setText("姓名:"+item.getWorkName());
        holder.amount.setText(String.format("金额：￥%.2f",item.getAmount()));
        holder.balance.setText(String.format("余额：￥%.2f",item.getBalance()));
        if(item.getQrType()==0){
            holder.payWay.setText("方式:IC卡");
        }else{
            holder.payWay.setText("方式:二维码");
        }
        holder.transTime.setText("时间："+ DateStringUtils.dateToString(item.getTransTime()));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long t1 = SystemClock.elapsedRealtime();

                if(selectedItem==i){
                    if((t1-lastClickTime)<MIN_CLICK_INTERVAL){
                        if (onItemClickListener != null) onItemClickListener.onItemDoubleClick(v, i);
                        lastClickTime = t1;
                    }else {
                        if (onItemClickListener != null) onItemClickListener.onItemClick(v, i);
                        lastClickTime = 0;
                    }
                }else {
                    if (onItemClickListener != null) onItemClickListener.onItemClick(v, i);
                    selectedItem = i;
                    lastClickTime = t1;
                }
            }
        });

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(null!=onItemClickListener) onItemClickListener.onItemLongClick(v,i);
                selectedItem = i;
                lastClickTime = SystemClock.elapsedRealtime();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class PaymentRecordViewHolder extends RecyclerView.ViewHolder{
        View rootView;
        TextView auditNo;
        TextView workNo;
        TextView workName;
        TextView amount;
        TextView balance;
        TextView payWay;
        TextView transTime;


        public PaymentRecordViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            auditNo = itemView.findViewById(R.id.auditNo);
            workNo = itemView.findViewById(R.id.workNo);
            workName = itemView.findViewById(R.id.workName);
            amount = itemView.findViewById(R.id.amount);
            balance = itemView.findViewById(R.id.balance);
            payWay = itemView.findViewById(R.id.payWay);
            transTime = itemView.findViewById(R.id.transTime);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);
        void onItemDoubleClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
