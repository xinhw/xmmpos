package com.rankway.controller.printer;

import android.content.Context;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/23
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public abstract  class PrinterBase {
    Context mContext;
    String errMessage;

    public PrinterBase(Context context){
        this.mContext = context;
    }

    public abstract int openPrinter();
    public abstract void closePrinter();
    public abstract int printString(String s);
    public abstract int printBytes(byte[] bytes);

    public String getErrMessage(){
        return errMessage;
    }

    public void setErrMessage(String s){
        this.errMessage = s;
    }


}
