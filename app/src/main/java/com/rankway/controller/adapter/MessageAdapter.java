package com.rankway.controller.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rankway.controller.R;
import com.rankway.controller.persistence.entity.MessageDetail;
import com.rankway.sommerlibrary.utils.DateUtil;

import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<MessageDetail, BaseViewHolder> {
    public MessageAdapter(int layoutResId, @Nullable List<MessageDetail> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDetail item) {
        if(item.isBreaded()) {
            helper.setTextColor(R.id.message_title, Color.BLACK);
        }else{
            helper.setTextColor(R.id.message_title, Color.BLUE);
        }
        helper.setText(R.id.message_title,"标题：" + item.getTitle());
        String content = item.getContent();
        if(content.length()>60) content = item.getContent().substring(0,60)+"...";
        helper.setText(R.id.message_content,content);
        helper.setText(R.id.message_source,"来自:"+ item.getFrom());
        helper.setText(R.id.message_time, "时间：" + DateUtil.getDateDStr(item.getTime()));
    }
}
