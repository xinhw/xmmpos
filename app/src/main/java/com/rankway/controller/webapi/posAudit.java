package com.rankway.controller.webapi;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/27
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class posAudit {
    //{"PosNo":"30001","PosName":"艾雷斯POS1","PosCno":425}
    String PosNo;
    String PosName;
    int PosCno;

    public String getPosNo() {
        return PosNo;
    }

    public void setPosNo(String posNo) {
        PosNo = posNo;
    }

    public String getPosName() {
        return PosName;
    }

    public void setPosName(String posName) {
        PosName = posName;
    }

    public int getPosCno() {
        return PosCno;
    }

    public void setPosCno(int posCno) {
        PosCno = posCno;
    }

    @Override
    public String toString() {
        return "posAudit{" +
                "PosNo='" + PosNo + '\'' +
                ", PosName='" + PosName + '\'' +
                ", PosCno=" + PosCno +
                '}';
    }
}
