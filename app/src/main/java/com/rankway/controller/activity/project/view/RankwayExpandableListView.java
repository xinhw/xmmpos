package com.rankway.controller.activity.project.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/01
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class RankwayExpandableListView extends ExpandableListView {
    public RankwayExpandableListView(Context context) {
        super(context);
    }

    public RankwayExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RankwayExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RankwayExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void scroll(int groupIndex, int childIndex) {
        int position = 0;
        for (int i = 0; i < groupIndex; i++) {
            position++;
            if (isGroupExpanded(i)) {
                position = position + getExpandableListAdapter().getChildrenCount(i);
            }
        }
        position++;
        position = position + childIndex;
        super.setSelection(position);
    }
}
