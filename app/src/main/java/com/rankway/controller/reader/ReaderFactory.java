package com.rankway.controller.reader;

import android.content.Context;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/23
 *   desc  : CS230Z读卡器驱动
 *   version: 1.0
 * </pre>
 */
public class ReaderFactory {
    private static ReaderBase reader = null;
    public static ReaderBase getReader(Context context){
        if(reader==null){
            reader = new ReaderCS230Z(context);
        }
        return reader;
    }

    public static void setReader(ReaderBase reader) {
        ReaderFactory.reader = reader;
    }
}
