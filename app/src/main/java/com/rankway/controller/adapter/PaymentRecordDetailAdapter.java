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
public class PaymentRecordDetailAdapter extends BaseQuickAdapter<PaymentRecord, BaseViewHolder> {

    public PaymentRecordDetailAdapter(int layoutResId, @Nullable @androidx.annotation.Nullable List<PaymentRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PaymentRecord item) {
        helper.setText(R.id.cno, item.getAuditNo()+"");
        helper.setText(R.id.workNo, item.getWorkNo());
        helper.setText(R.id.amount, String.format("%.2f",item.getAmount()));
        helper.setText(R.id.transTime, DateStringUtils.dateToString(item.getTransTime()));
    }

    private int getMyColor(int green) {
        return mContext.getResources().getColor(green);
    }
}

