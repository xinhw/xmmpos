package com.rankway.controller.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rankway.controller.R;
import com.rankway.controller.entity.PaymentStatisticsRecordEntity;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.utils.DateStringUtils;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/01
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class DeskPosPayRecordAdapter
        extends BaseExpandableListAdapter {
    private final String TAG = "DeskPosPayRecordAdapter";

    private final Context context;
    private List<PaymentStatisticsRecordEntity> dataEntity;
    //设置子级选中时index状态
    private int selectedGroupItem = -1;
    private int selectedChildItem = -1;
    private OnItemClickListener onItemClickListener;

    public DeskPosPayRecordAdapter(Context context, List<PaymentStatisticsRecordEntity> datas) {
        this.context = context;
        this.dataEntity = datas;
    }

    /**
     * 获取组的数目
     *
     * @return 返回一级列表组的数量
     */
    @Override
    public int getGroupCount() {
        return dataEntity == null ? 0 : dataEntity.size();
    }

    /**
     * 获取指定组中的子节点数量
     *
     * @param groupPosition 子元素组所在的位置
     * @return 返回指定组中的子数量
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return dataEntity.get(groupPosition).getRecordList().size();
    }

    /**
     * 获取与给定组相关联的对象
     *
     * @param groupPosition 子元素组所在的位置
     * @return 返回指定组的子数据
     */
    @Override
    public Object getGroup(int groupPosition) {
        return dataEntity.get(groupPosition);
    }

    /**
     * 获取与给定组中的给定子元素关联的数据
     *
     * @param groupPosition 子元素组所在的位置
     * @param childPosition 子元素的位置
     * @return 返回子元素的对象
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataEntity.get(groupPosition).getRecordList().get(childPosition);
    }

    /**
     * 获取组在给定位置的ID（唯一的）
     *
     * @param groupPosition 子元素组所在的位置
     * @return 返回关联组ID
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取给定组中给定子元素的ID(唯一的)
     *
     * @param groupPosition 子元素组所在的位置
     * @param childPosition 子元素的位置
     * @return 返回子元素关联的ID
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * @return 确定id 是否总是指向同一个对象
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * @return 返回指定组的对应的视图 （一级列表样式）
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.d(TAG, " getGroupView"+String.format("(%d,%d)",groupPosition,selectedGroupItem));
        ParentHolder parentHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_payment_statistics,null);
            parentHolder = new ParentHolder();
            parentHolder.rootView = convertView.findViewById(R.id.rootView);
            parentHolder.seqNo = convertView.findViewById(R.id.seqNo);
            parentHolder.transDate = convertView.findViewById(R.id.transDate);
            parentHolder.subCount = convertView.findViewById(R.id.subCount);
            parentHolder.subAmount = convertView.findViewById(R.id.subAmount);
            parentHolder.detail = convertView.findViewById(R.id.detail);
        }else{
            parentHolder = (ParentHolder) convertView.getTag();
        }
        PaymentStatisticsRecordEntity row = dataEntity.get(groupPosition);
        parentHolder.seqNo.setText(row.getSeqNo()+"");
        parentHolder.transDate.setText(row.getCdate());
        parentHolder.subCount.setText(row.getSubCount()+"");
        parentHolder.subAmount.setText(String.format("%.2f",row.getSubAmount()));
        if(isExpanded){
            parentHolder.detail.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }else{
            parentHolder.detail.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
        }
        //选中时，突出显示
        if(groupPosition == selectedGroupItem) {
            convertView.setBackgroundColor(0xfffc843b);     //  亮蓝色
            parentHolder.rootView.setSelected(true);
        }else {
            convertView.setBackgroundColor(0xFFADD8E6);
            parentHolder.rootView.setSelected(false);
        }
        parentHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=onItemClickListener) {
                    onItemClickListener.parentOnClickListener(v,groupPosition);
                }
            }
        });
        return convertView;
    }

    /**
     * @return 返回指定位置对应子视图的视图
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d(TAG, " getChildView"+String.format("(%d,%d)",groupPosition,childPosition));
        final ChildrenHolder childrenHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_payment_record,null);
            childrenHolder = new ChildrenHolder();
            childrenHolder.rootView = convertView.findViewById(R.id.rootView);
            childrenHolder.auditNo = convertView.findViewById(R.id.auditNo);
            childrenHolder.workNo = convertView.findViewById(R.id.workNo);
            childrenHolder.workName = convertView.findViewById(R.id.workName);
            childrenHolder.amount = convertView.findViewById(R.id.amount);
            childrenHolder.balance = convertView.findViewById(R.id.balance);
            childrenHolder.payWay = convertView.findViewById(R.id.payWay);
            childrenHolder.transTime = convertView.findViewById(R.id.transTime);
        }else{
            childrenHolder = (ChildrenHolder) convertView.getTag();
        }
        //设置选中child时效果
        if(groupPosition == selectedGroupItem && childPosition == selectedChildItem)
        {
            convertView.setBackgroundColor(Color.BLUE);
            childrenHolder.rootView.setSelected(true);
        }else
        {
            convertView.setBackgroundColor(Color.WHITE);
            childrenHolder.rootView.setSelected(false);
        }
        PaymentRecord item = dataEntity.get(groupPosition).getRecordList().get(childPosition);
        childrenHolder.auditNo.setText(childPosition+"");
        childrenHolder.workNo.setText(item.getWorkNo().trim());
        childrenHolder.workName.setText(item.getWorkName().trim());
        childrenHolder.amount.setText(String.format("%.2f",item.getAmount()));
        childrenHolder.balance.setText(String.format("%.2f",item.getBalance()));
        if(item.getQrType()==0){
            childrenHolder.payWay.setText("IC卡");
        }else{
            childrenHolder.payWay.setText("二维码");
        }
        childrenHolder.transTime.setText(DateStringUtils.dateToString(item.getTransTime()));
        childrenHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "childrenHolder.rootView.setOnClickListener" +String.format("(%d,%d)",groupPosition,childPosition));
                if(onItemClickListener!=null) {
                    onItemClickListener.childOnClickListener(groupPosition, childPosition, item);
                }
            }
        });

        return convertView;
    }

    /**
     * 指定位置的子元素是否可选
     *
     * @param groupPosition 子元素组所在的位置
     * @param childPosition 子元素的位置
     * @return 返回是否可选
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ParentHolder {
        public LinearLayout rootView;
        public TextView seqNo;
        public TextView transDate;
        public TextView subCount;
        public TextView subAmount;
        public ImageView detail;
    }

    class ChildrenHolder {
        LinearLayout rootView;
        TextView auditNo;
        TextView workNo;
        TextView workName;
        TextView amount;
        TextView balance;
        TextView payWay;
        TextView transTime;
    }

    public interface OnItemClickListener{
        void childOnClickListener(int groupPosition, int childPosition, PaymentRecord record);
        void parentOnClickListener(View view,int groupPosition);
    }

    public int getSelectedGroupItem() {
        return selectedGroupItem;
    }

    public void setSelectedGroupItem(int selectedGroupItem) {
        this.selectedGroupItem = selectedGroupItem;
    }

    public int getSelectedChildItem() {
        return selectedChildItem;
    }

    public void setSelectedChildItem(int selectedGroupItem,int selectedChildItem) {
        this.selectedGroupItem = selectedGroupItem;
        this.selectedChildItem = selectedChildItem;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
