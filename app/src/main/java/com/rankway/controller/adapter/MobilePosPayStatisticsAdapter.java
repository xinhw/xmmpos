package com.rankway.controller.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/18
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class MobilePosPayStatisticsAdapter
        extends RecyclerView.Adapter<MobilePosPayStatisticsAdapter.PaymentRecordStatisticsViewHolder> {

    private final String TAG = "MobilePosPayStatisticsAdapter";

    List<PaymentStatisticsRecordEntity> data;
    OnItemClickListener onItemClickListener;

    private int selectedItem = -1;
    private Context mContext;
    private long lastClickTime = 0;
    private final int MIN_CLICK_INTERVAL = 500; //

    public MobilePosPayStatisticsAdapter(Context context, List<PaymentStatisticsRecordEntity> records) {
        this.mContext = context;
        this.data = records;
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public PaymentRecordStatisticsViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_payment_statistics, viewGroup, false);
        PaymentRecordStatisticsViewHolder holder = new PaymentRecordStatisticsViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull PaymentRecordStatisticsViewHolder holder, int i) {
        holder.itemView.setSelected(i == selectedItem);
        PaymentStatisticsRecordEntity item = data.get(i);
        holder.seqNo.setText(item.getSeqNo() + "");
        holder.transDate.setText(item.getCdate());
        holder.subCount.setText(item.getSubCount() + "");
        holder.subAmount.setText(String.format("￥%.2f", item.getSubAmount()));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long t1 = SystemClock.elapsedRealtime();

                if (selectedItem == i) {
                    if ((t1 - lastClickTime) < MIN_CLICK_INTERVAL) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemDoubleClick(v, i);
                        lastClickTime = t1;
                    } else {
                        if (onItemClickListener != null) onItemClickListener.onItemClick(v, i);
                        lastClickTime = 0;
                    }
                } else {
                    if (onItemClickListener != null) onItemClickListener.onItemClick(v, i);
                    selectedItem = i;
                    lastClickTime = t1;
                }
            }
        });

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onItemClickListener) onItemClickListener.onItemLongClick(v, i);
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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        void onItemDoubleClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class PaymentRecordStatisticsViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView seqNo;
        TextView transDate;
        TextView subCount;
        TextView subAmount;

        public PaymentRecordStatisticsViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            seqNo = itemView.findViewById(R.id.seqNo);
            transDate = itemView.findViewById(R.id.transDate);
            subCount = itemView.findViewById(R.id.subCount);
            subAmount = itemView.findViewById(R.id.subAmount);
        }
    }
}
