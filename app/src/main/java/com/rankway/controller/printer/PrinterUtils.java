package com.rankway.controller.printer;

import android.util.Log;

import com.rankway.controller.entity.PosInfoBean;
import com.rankway.controller.persistence.entity.PaymentRecord;
import com.rankway.controller.persistence.entity.PaymentShiftEntity;
import com.rankway.controller.utils.DateStringUtils;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2024/01/07
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class PrinterUtils {
    private static final String TAG = "PrinterUtils";

    String title = "上海报业大厦";

    private String combinePrintLine(int str1len,String str1,
                                    String str2){
        String s1 = str1;
        try {
            s1 = new String(str1.getBytes("GB2312"), "ISO-8859-1");
        }catch (Exception e){
            e.printStackTrace();
        }

        int n = s1.length();
        if(n>=str1len){
            return str1 + str2;
        }
        StringBuilder sb = new StringBuilder();
        while(sb.length()<(str1len-n)){
            sb.append(' ');
        }
        return str1+sb.toString()+str2;
    }

    private String padLeftSpace(String s,int length){
        if(s.length()>=length) return s;
        StringBuilder sb = new StringBuilder();
        while(sb.length()<(length-s.length())){
            sb.append(' ');
        }
        sb.append(s);
        return sb.toString();
    }


    private String padRightSpace(String s,int length){
        if(s.length()>=length) return s;
        StringBuilder sb = new StringBuilder();
        while(sb.length()<(length-s.length())){
            sb.append(' ');
        }
        return s+sb.toString();
    }

    public void printPayment(PrinterBase printer,
                                PosInfoBean pos,
                                PaymentRecord record){
        Log.d(TAG,"printPayment");

        if(null==printer) return;
        if(null==record) return;
        if(null==pos) return;

        printer.openPrinter();

        String s = "";

        //  走纸5行
        // printBytes(PrinterFormatUtils.getFeedCommand(2));

        s = "--------------------------------";
        printer.printString(s);

        printer.printString(title);

        s = "--------------------------------";
        printer.printString(s);

        //  POS机号
        s =combinePrintLine(12,"POS号：",pos.getCposno());
        printer.printString(s);

        //  流水号
        s =combinePrintLine(12,"流水号：",pos.getAuditNo()+"");
        printer.printString(s);

        //  工号
        s =combinePrintLine(12,"工号：",record.getWorkNo());
        printer.printString(s);

        //  支付方式
        if(record.getQrType()==0){
            s = "IC卡";
        }else{
            s = "二维码";
        }
        s = combinePrintLine(12,"方式：",s);
        printer.printString(s);

        //  时间
        s = combinePrintLine(12,"时间：", DateStringUtils.dateToString(record.getTransTime()));
        printer.printString(s);

        String samount = padLeftSpace(String.format("%.2f",record.getAmount()),11);
        s = padRightSpace("消费金额：",10);
        printer.printString(s+samount);

        float f = record.getRemain() - record.getAmount();
        samount = padLeftSpace(String.format("%.2f", f), 11);
        s = padRightSpace("剩余金额：", 10);
        printer.printString(s + samount);


//        //  走纸3行
//        printer.printBytes(PrinterFormatUtils.getFeedCommand(3));

        //  切纸并且走纸
        printer.partialCut();
    }

    /***
     * 中间打印
     * @param totalLen
     * @param str1
     * @return
     */
    private String centralPrintLine(int totalLen,String str1){
        String s1 = str1;
        try {
            s1 = new String(str1.getBytes("GB2312"), "ISO-8859-1");
        }catch (Exception e){
            e.printStackTrace();
        }
        int n = s1.length();
        if(n>=totalLen) return str1;
        StringBuilder sb = new StringBuilder();
        while(sb.length()<(totalLen-n)/2){
            sb.append(' ');
        }
        return sb.toString()+str1;
    }

    /**
     * 结班打印
     */
    public void printShiftSettle(PrinterBase printer, PaymentShiftEntity shiftEntity){
        String s = "";

        //  走纸5行
        // printBytes(PrinterFormatUtils.getFeedCommand(2));
        shiftEntity.setShiftNo(DateStringUtils.getYYMMDDHHMMss(shiftEntity.getShiftOnTime())+shiftEntity.getShiftOnAuditNo());

        s = "--------------------------------";
        printer.printString(s);

        //  放大字体
        printer.printBytes(PrinterFormatUtils.getFontSizeCommand(true));
        printer.printString(centralPrintLine(16,title));

        //  字体正常
        printer.printBytes(PrinterFormatUtils.getFontSizeCommand(false));

        s = "--------------------------------";
        printer.printString(s);

        //  POS机号
        s =combinePrintLine(12,"POS号：",shiftEntity.getPosNo());
        printer.printString(s);

        //  POS机号
        s =combinePrintLine(12,"收银员：",shiftEntity.getOperatorNo());
        printer.printString(s);

        //  班次号：
        s =combinePrintLine(12,"班次号：",shiftEntity.getShiftNo());
        printer.printString(s);

        //  开班流水
        s =combinePrintLine(12,"开班流水：",shiftEntity.getShiftOnAuditNo()+"");
        printer.printString(s);

        //  起始时间
        s =combinePrintLine(12,"起始时间：", DateStringUtils.getDateDStr(shiftEntity.getShiftOnTime()));
        printer.printString(s);

//        //  结班流水
//        s =combinePrintLine(12,"结班流水：",shiftEntity.getShiftOffAuditNo()+"");
//        printer.printString(s);

        //  结束时间
        s =combinePrintLine(12,"结束时间：", DateStringUtils.getDateDStr(shiftEntity.getShiftOffTime()));
        printer.printString(s);

        //  交易次数
        s =combinePrintLine(12,"交易次数：",shiftEntity.getTotalCount()+"");
        printer.printString(s);

        //  交易金额
        s =combinePrintLine(12,"交易金额：",String.format("￥%.2f",shiftEntity.getTotalAmount()*0.01));
        printer.printString(s);

        //  报表时间
        s =combinePrintLine(12,"打印时间：", DateStringUtils.getDateDStr(shiftEntity.getReportTime()));
        printer.printString(s);


        //  切纸并且走纸
        printer.partialCut();

        return;
    }
}
