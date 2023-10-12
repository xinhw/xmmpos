package com.rankway.controller.printer;

import android.text.TextUtils;

import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.AppIntentString;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.persistence.entity.DishEntity;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.utils.DateStringUtils;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/30
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class PrinterUtils {

    private String title;
    public PrinterUtils(){
        String str = SpManager.getIntance().getSpString(AppIntentString.PRINTER_HEADER);
        if(TextUtils.isEmpty(str)) str = "上海报业大厦餐厅";
        title = str;
    }

    public void printPayItem(PrinterBase printer,
                             PosInfoBean posInfoBean,
                             PaymentRecordEntity record,
                             List<DishEntity> dishEntityList){
        String s = "";

        //  走纸5行
        // printBytes(PrinterFormatUtils.getFeedCommand(2));

        s = "--------------------------------";
        printer.printString(s);

        //  放大字体
        printer.printBytes(PrinterFormatUtils.getFontSizeCommand(true));
        printer.printString(centralPrintLine(16,title));

        //  字体正常
        printer.printBytes(PrinterFormatUtils.getFontSizeCommand(false));

        s = "--------------------------------";
        printer.printString(s);

//        //  班次号
//        s = String.format(" 班次号：%s",posInfoBean.getShiftNo());
//        printer.printString(s);

        //  POS机号
        s =combinePrintLine(12,"POS号：",posInfoBean.getCposno());
        printer.printString(s);

        //  流水号
        s =combinePrintLine(12,"流水号：",posInfoBean.getAuditNo()+"");
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
        s = combinePrintLine(12,"时间：",DateStringUtils.dateToString(record.getTransTime()));
        printer.printString(s);

        s = "--------------------------------";
        printer.printString(s);

        int totoalAmount = 0;
        for(DishEntity dishEntity : dishEntityList){
            //  商品名称打印1行
            s = " " + dishEntity.getDishName().trim();
            printer.printString(s);

            // 空格[6]+ 单价[9] + 数量[5] + 金额[11] 空额[1] (32)
            float f = (float)(dishEntity.getPrice()*0.01);
            String sprice = padLeftSpace(String.format("%.2f",f),9);
            String scount = padLeftSpace(dishEntity.getCount()+"",5);
            int amount = dishEntity.getCount()* dishEntity.getPrice();
            f = (float) (amount*0.01);
            String samount = padLeftSpace(String.format("%.2f",f),11);

            s = "      "+sprice+scount+samount;
            printer.printString(s);

            totoalAmount = totoalAmount + amount;
        }

        s = "--------------------------------";
        printer.printString(s);

        float f = (float) (totoalAmount*0.01);
        String samount = padLeftSpace(String.format("%.2f",f),11);
        s = padRightSpace("消费金额：",10);
        printer.printString(s+samount);

        //  仅仅在线交易打印余额
        if(record.getUploadFlag()== PaymentTotal.UPLOADED) {
            f = record.getRemain() - f;
            samount = padLeftSpace(String.format("%.2f", f), 11);
            s = padRightSpace("剩余金额：", 10);
            printer.printString(s + samount);
        }

        s = SpManager.getIntance().getSpString(AppIntentString.PRINTER_SUFFIX);
        if(!TextUtils.isEmpty(s)) {
            String s2 = "--------------------------------";
            printer.printString(s2);

            String[] strings = s.split("，");
            for (String s1 : strings) {
                printer.printString(s1);
            }
        }

        //  走纸3行
        printer.printBytes(PrinterFormatUtils.getFeedCommand(3));
        return;
    }

    /**
     * 结班打印
     */
    public void printShiftSettle(PrinterBase printer,
                                 PosInfoBean posInfoBean,
                                 List<PaymentRecordEntity> records){

        int totalCount,cardCount,qrCount;
        float totalAmount,cardAmount,qrAmount;

        totalCount = cardCount = qrCount = 0;
        totalAmount = cardAmount = qrAmount = 0.00f;

        for(PaymentRecordEntity record:records){
            if(record.getQrType()==0){
                cardCount++;
                cardAmount = cardAmount + record.getAmount();
            }else{
                qrCount++;
                qrAmount = qrAmount + record.getAmount();
            }
        }
        totalCount = cardCount + qrCount;
        totalAmount = cardAmount + qrAmount;

        String s = "";

        //  走纸5行
        // printBytes(PrinterFormatUtils.getFeedCommand(2));

        s = "--------------------------------";
        printer.printString(s);

        //  放大字体
        printer.printBytes(PrinterFormatUtils.getFontSizeCommand(true));
        printer.printString(title);

        //  字体正常
        printer.printBytes(PrinterFormatUtils.getFontSizeCommand(false));

        s = "--------------------------------";
        printer.printString(s);

//        //  班次号
//        s = String.format(" 班次号：%s",posInfoBean.getShiftNo());
//        printer.printString(s);

        //  POS机号
        s =combinePrintLine(12," POS号：",posInfoBean.getCposno());
        printer.printString(s);

        //  收银员
        s = combinePrintLine(12," 收银员：",posInfoBean.getUsercode());
        printer.printString(s);

        //  起始时间
        s = combinePrintLine(12," 起始时间：",DateStringUtils.dateToString(posInfoBean.getStartTime()));
        printer.printString(s);

        //  结束时间
        s = String.format(" 结束时间：%s",DateStringUtils.dateToString(posInfoBean.getSettleTime()));
        printer.printString(s);

        //  总笔数
        s = String.format(" 总笔数：%d",totalCount);
        printer.printString(s);

        //  总金额
        s = String.format(" 总金额：%.2f",totalAmount);
        printer.printString(s);

        s = "--------------------------------";
        printer.printString(s);

        //  打印时间
        s = String.format(" 打印时间：%s",DateStringUtils.getCurrentTime());
        printer.printString(s);
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
}
