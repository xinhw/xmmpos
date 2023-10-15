package com.rankway.controller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.persistence.entity.DishSubTypeEntity;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class DishSubTypeAdapter
        extends RecyclerView.Adapter<DishSubTypeAdapter.DishSubTypeViewHolder> {
    private final String TAG = "DishSubTypeAdapter";

    List<DishSubTypeEntity> data;
    private OnItemClickListener onItemClickListener;

    private int selectedItem = -1;
    private Context mContext;

    public DishSubTypeAdapter(Context context, List<DishSubTypeEntity> types) {
        this.mContext = context;
        this.data = types;
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public DishSubTypeViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_dish_sub_type, viewGroup, false);
        DishSubTypeViewHolder viewHolder = new DishSubTypeViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull DishSubTypeViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.itemView.setSelected(i == selectedItem);
        DishSubTypeEntity item = data.get(i);
        Log.d(TAG, "item:" + item.toString());
        holder.tvDishSubType.setText(item.getDishSubTypeName().trim());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = i;
                if (null != onItemClickListener) onItemClickListener.onDishSubTypeItemClick(v, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount:" + data.size());
        return data.size();
    }


    public interface OnItemClickListener {
        void onDishSubTypeItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class DishSubTypeViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView tvDishSubType;

        public DishSubTypeViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            tvDishSubType = itemView.findViewById(R.id.tvDishType);
        }
    }
}

