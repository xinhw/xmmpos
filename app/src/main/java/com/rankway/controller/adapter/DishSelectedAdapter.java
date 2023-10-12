package com.rankway.controller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.persistence.entity.DishEntity;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/30
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class DishSelectedAdapter
        extends RecyclerView.Adapter<DishSelectedAdapter.DishSelectedViewHolder> {
    private final String TAG = "DishSelectedAdapter";

    List<DishEntity> data;
    private OnItemClickListener onItemClickListener;

    private int selectedItem = -1;
    private Context mContext;

    public DishSelectedAdapter(Context context, List<DishEntity> records) {
        this.mContext = context;
        this.data = records;
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public DishSelectedViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_selected_dish, viewGroup, false);
        DishSelectedViewHolder viewHolder = new DishSelectedViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull DishSelectedViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.itemView.setSelected(i == selectedItem);
        DishEntity item = data.get(i);
        holder.seqNo.setText((i + 1) + "");
        holder.dishName.setText(item.getDishName().trim());
        holder.dishPrice.setText(String.format("%.2f", item.getPrice() * 0.01));
        holder.dishCount.setText(item.getCount() + "");
        int amount = item.getCount() * item.getPrice();
        holder.dishAmount.setText(String.format("%.2f", amount * 0.01));


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = i;
                if (onItemClickListener != null) onItemClickListener.onSelectedDishItemClick(v, i);
            }
        });

        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedItem = i;
                if (onItemClickListener != null)
                    onItemClickListener.onSelectedDishItemLongClick(v, i);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public interface OnItemClickListener {
        void onSelectedDishItemClick(View view, int position);

        void onSelectedDishItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class DishSelectedViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView seqNo;
        TextView dishName;
        TextView dishPrice;
        TextView dishCount;
        TextView dishAmount;

        public DishSelectedViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            seqNo = itemView.findViewById(R.id.seqNo);
            dishName = itemView.findViewById(R.id.dishName);
            dishPrice = itemView.findViewById(R.id.dishPrice);
            dishCount = itemView.findViewById(R.id.dishCount);
            dishAmount = itemView.findViewById(R.id.dishAmount);
        }
    }
}
