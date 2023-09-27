package com.rankway.controller.reader;

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
public abstract class ReaderBase {
    public Context mContext;

    private String errMessage;

    public ReaderBase(Context context){
        this.mContext = context;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public abstract int opneReader();
    public abstract void closeReader();
    public abstract String getCardNo();
    public abstract byte[] readBlock(byte blockNo);
    public abstract int authenticate(byte sectorNo,byte keyType,byte[] key);
    public abstract int writeBlock(byte blockNo,byte[] block);
    public abstract int rats();
    public abstract byte[] apdu(byte[] cmd);

}
