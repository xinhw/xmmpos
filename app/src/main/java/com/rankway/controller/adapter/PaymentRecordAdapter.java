package com.rankway.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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
public class PaymentRecordAdapter
        extends BaseQuickAdapter<PaymentRecord, BaseViewHolder> {

    public PaymentRecordAdapter(int layoutResId, @Nullable @androidx.annotation.Nullable List<PaymentRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PaymentRecord item) {
        helper.setText(R.id.auditNo,"序号:"+item.getAuditNo());
        helper.setText(R.id.workNo,"工号:"+item.getWorkNo());
        helper.setText(R.id.workName,"姓名:"+item.getWorkName());

        helper.setText(R.id.amount,String.format("金额：%.2f",item.getAmount()));
        helper.setText(R.id.balance,String.format("余额: %.2f",item.getBalance()));

        if(item.getQrType()==0){
            helper.setText(R.id.payWay,"方式:IC卡");
        }else{
            helper.setText(R.id.payWay,"方式:二维码");
        }
        helper.setText(R.id.transTime, "时间："+DateStringUtils.dateToString(item.getTransTime()));
    }

    private int getMyColor(int green) {
        return mContext.getResources().getColor(green);
    }
}
