package com.rankway.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rankway.controller.R;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.adapter.MessageAdapter;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.MessageDetail;
import com.rankway.controller.persistence.gen.MessageDetailDao;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {
    private RecyclerView recycleView;
    private View noDataView;

    private List<MessageDetail> messageList;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        initView();

        initData();
    }

    private void initData() {

    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        backImag.setOnClickListener(this);

        TextView tv = findViewById(R.id.text_title);
        tv.setText("消息");

        noDataView = findViewById(R.id.nodata_view);

        messageList = new ArrayList<MessageDetail>();

        mAdapter = new MessageAdapter(R.layout.item_message_digest, messageList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        recycleView = findViewById(R.id.message_recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //  缓存信息
        List<MessageDetail> templist = DBManager.getInstance().getMessageDetailDao().queryBuilder()
                .orderDesc(MessageDetailDao.Properties.Id)
                .list();

        if (templist.size() > 0) {
            noDataView.setVisibility(View.GONE);

            messageList.clear();
            messageList.addAll(templist);

            mAdapter.notifyDataSetChanged();
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDetail md = messageList.get(position);
        if (null == md) return;

        Intent mainIntent = new Intent(this, NotificationDetail.class);
        mainIntent.putExtra("TITLE", md.getTitle());
        mainIntent.putExtra("CONTENT", md.getContent());
        mainIntent.putExtra("TIME", md.getTime());
        mainIntent.putExtra("ID", md.getId());
        mainIntent.putExtra("FROM", md.getFrom());
        startActivity(mainIntent);

        md.setBreaded(true);
        DBManager.getInstance().getMessageDetailDao().save(md);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDetail md = messageList.get(position);
        if (null == md) return false;

        int[] location = new int[2];
        view.getLocationInWindow(location);
        View popuView = getLayoutInflater().inflate(R.layout.popuwindow_view, null, false);
        PopupWindow popupWindow = new PopupWindow(popuView, 180, 100);
        popupWindow.setFocusable(true);

        TextView insertView = popuView.findViewById(R.id.insert_item);
        insertView.setVisibility(View.GONE);

        popuView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageList.remove(md);
                DBManager.getInstance().getMessageDetailDao().delete(md);
                mAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, location[1] + 25);

        return false;
    }
}