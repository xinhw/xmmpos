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
import com.rankway.controller.persistence.entity.DishType;

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
public class DishTypeAdapter
    extends RecyclerView.Adapter<DishTypeAdapter.DishTypeViewHolder>{
    private final String TAG ="DishTypeAdapter";

    List<DishType> data;
    private OnItemClickListener onItemClickListener;

    private int selectedItem = -1;
    private Context mContext;

    public DishTypeAdapter(Context context,List<DishType> types){
        this.mContext = context;
        this.data = types;
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public DishTypeViewHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_dish_type,viewGroup,false);
        DishTypeViewHolder viewHolder = new DishTypeViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull DishTypeViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.itemView.setSelected(i==selectedItem);
        DishType item = data.get(i);
        holder.tvDishType.setText(item.getDishTypeName().trim());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = i;
                if(null!=onItemClickListener) onItemClickListener.onDishTypeItemClick(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface OnItemClickListener{
        void onDishTypeItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class DishTypeViewHolder extends RecyclerView.ViewHolder{
        View rootView;
        TextView tvDishType;

        public DishTypeViewHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            tvDishType  = itemView.findViewById(R.id.tvDishType);
        }
    }
}
