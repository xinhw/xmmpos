package com.rankway.controller.printer;

import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.persistence.entity.Dish;
import com.rankway.controller.persistence.entity.PaymentRecord;
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

    public void printPayItem(PrinterBase printer,
                             PosInfoBean posInfoBean,
                             PaymentRecord record,
                             List<Dish> dishList){
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

        //  班次号
        s = String.format(" 班次号：%s",posInfoBean.getShiftNo());
        printer.printString(s);

        //  POS机号
        s = String.format(" POS号：%s",posInfoBean.getCposno());
        printer.printString(s);

        //  流水号
        s = String.format(" 流水号：%d",posInfoBean.getAuditNo());
        printer.printString(s);

        //  工号
        s = String.format(" 工号：%s",record.getWorkNo());
        printer.printString(s);

        //  支付方式
        if(record.getQrType()==0){
            s = " 方式：IC卡";
        }else{
            s = " 方式：二维码";
        }
        printer.printString(s);

        //  时间
        s = String.format(" 时间：%s",DateStringUtils.dateToString(record.getTransTime()));
        printer.printString(s);

        s = "--------------------------------";
        printer.printString(s);

        int totoalAmount = 0;
        for(Dish dish:dishList){
            //  商品名称打印1行
            s = " " + dish.getDishName().trim();
            printer.printString(s);

            // 空格[6]+ 单价[9] + 数量[5] + 金额[11] 空额[1] (32)
            float f = (float)(dish.getPrice()*0.01);
            String sprice = padLeftSpace(String.format("%.2f",f),9);
            String scount = padLeftSpace(dish.getCount()+"",5);
            int amount = dish.getCount()* dish.getPrice();
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
        printer.printString(" " + s+samount);

        f = record.getRemain() - f;
        samount = padLeftSpace(String.format("%.2f",f),11);
        s = padRightSpace("剩余金额：",10);
        printer.printString(" " + s+samount);

        //  走纸3行
        printer.printBytes(PrinterFormatUtils.getFeedCommand(3));
        return;
    }

    /**
     * 结班打印
     */
    public void printShiftSettle(PrinterBase printer,
                                 PosInfoBean posInfoBean,
                                 List<PaymentRecord> records){

        int totalCount,cardCount,qrCount;
        float totalAmount,cardAmount,qrAmount;

        totalCount = cardCount = qrCount = 0;
        totalAmount = cardAmount = qrAmount = 0.00f;

        for(PaymentRecord record:records){
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

        //  班次号
        s = String.format(" 班次号：%s",posInfoBean.getShiftNo());
        printer.printString(s);

        //  POS机号
        s = String.format(" POS号：%s",posInfoBean.getCposno());
        printer.printString(s);

        //  收银员
        s = String.format(" 收银员：%d",posInfoBean.getUsercode());
        printer.printString(s);

        //  起始时间
        s = String.format(" 起始时间：%s",DateStringUtils.dateToString(posInfoBean.getStartTime()));
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
}
