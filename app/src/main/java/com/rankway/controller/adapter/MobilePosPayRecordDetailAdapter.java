package com.rankway.controller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.persistence.entity.PaymentRecord;
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
public class MobilePosPayRecordDetailAdapter
        extends RecyclerView.Adapter<MobilePosPayRecordDetailAdapter.PaymentRecordDetailViewHolder> {

    private final String TAG = "PaymentRecordDetailAdapter";
    private List<PaymentRecord> data;
    private int selectedItem = -1;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private long lastClickTime = 0;
    private final int MIN_CLICK_INTERVAL = 500; //

    public MobilePosPayRecordDetailAdapter(Context context, List<PaymentRecord> records){
        this.mContext = context;
        this.data = records;
    }

    private int getMyColor(int green) {
        return mContext.getResources().getColor(green);
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public PaymentRecordDetailViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_payment_record_detail,viewGroup,false);
        PaymentRecordDetailViewHolder holder = new PaymentRecordDetailViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull PaymentRecordDetailViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.itemView.setSelected(i==selectedItem);
        PaymentRecord item = data.get(i);
        holder.cno.setText(item.getAuditNo()+"");
        holder.workNo.setText(item.getWorkNo());
        holder.amount.setText(String.format("￥%.2f",item.getAmount()));
        holder.transTime.setText(DateStringUtils.dateToString(item.getTransTime()));
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
                if(onItemClickListener!=null) onItemClickListener.onItemLongClick(v,i);
                selectedItem = i;
                lastClickTime = SystemClock.elapsedRealtime();
                return false;
            }
        });

    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PaymentRecordDetailViewHolder extends RecyclerView.ViewHolder{
        View rootView;
        TextView cno;
        TextView workNo;
        TextView amount;
        TextView transTime;

        public PaymentRecordDetailViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            cno = itemView.findViewById(R.id.cno);
            workNo = itemView.findViewById(R.id.workNo);
            amount = itemView.findViewById(R.id.amount);
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

