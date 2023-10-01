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
import com.rankway.controller.persistence.entity.Dish;

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
public class DishAdapter
    extends RecyclerView.Adapter<DishAdapter.DishAdapterViewHolder>{
    private final String TAG ="DishAdapter";

    List<Dish> data;
    private OnItemClickListener onItemClickListener;

    private int selectedItem = -1;
    private Context mContext;

    public DishAdapter(Context context,List<Dish> dishes){
        this.mContext = context;
        this.data = dishes;
    }


    @NonNull
    @androidx.annotation.NonNull
    @Override
    public DishAdapterViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_dish,viewGroup,false);
        DishAdapterViewHolder holder = new DishAdapterViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull DishAdapterViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.itemView.setSelected(i==selectedItem);
        Dish item = data.get(i);

        float price = (float)(item.getPrice()*0.01);
        String str = String.format("%s\n(%.2f)",item.getDishName().trim(),price);
        holder.tvDish.setText(str);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = i;
                if (onItemClickListener != null) onItemClickListener.onDishItemClick(v, i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener{
        void onDishItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class DishAdapterViewHolder extends RecyclerView.ViewHolder{
        View rootView;
        TextView tvDish;

        public DishAdapterViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            tvDish  = itemView.findViewById(R.id.tvDish);
        }
    }

}
