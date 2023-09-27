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
public class PrinterFactory {
    private static PrinterBase printer = null;

    public static PrinterBase getPrinter(Context context){
        if(printer==null) {
            printer = new PrinterGP58(context);
        }
        return printer;
    }

    public static void setPrinter(PrinterBase printer) {
        PrinterFactory.printer = printer;
    }
}
