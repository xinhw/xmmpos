package com.rankway.controller.printer;

import android.content.Context;
import android.os.Build;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/12/13
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class PrinterSPI extends PrinterBase {
    private static final String TAG = "SpiPrinter";

    FileOutputStream mFileOutputStream;
    FileInputStream mFileInputStream;

    private SerialPort mSerialPort;

    public PrinterSPI(Context context) {
        super(context);
    }

    @Override
    public int openPrinter() {
        Log.d(TAG, "OpenSpiPrinter: " + Build.MODEL);
        switch (Build.MODEL) {
            case "Z2":
            case "M2-203":
            case "tb8766p1_64_bsp":
//                OpenMtkPort();
                OpenPrint();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initSpiMtk();
                break;
            case "Z2W":
            case "M2-202":
                OpenMtkPort();
                OpenZ2wPrint();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initSpiZ2w();
                break;
            default:
                OpenPort();
                OpenPrint();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initSPI();
                break;
        }
        return 0;
    }


    private void initSPI(){
        Log.d(TAG, "initSPI: /dev/spidev0.0");
        File file = new File("/dev/spidev0.0");
        try {
            mFileOutputStream=new FileOutputStream(file);
            mFileInputStream=new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initSpiMtk(){

        String node = "";
        if (Build.MODEL.contains("tb8766p1_64_bsp")) {
            node= "/dev/spidev1.0";
        } else {
            node ="/dev/spidev32765.0";
        }
        Log.d(TAG, "initSpiMtk: node= " + node);
        File file = new File(node);
        try {
            mFileOutputStream=new FileOutputStream(file);
            mFileInputStream=new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSpiZ2w(){

        File file = new File("/dev/spidev32766.0");
        try {
            mFileOutputStream=new FileOutputStream(file);
            mFileInputStream=new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------打印机上电
    private void OpenZ2wPrint(){
        try {
            //上电
            Log.d(TAG,"OpenPrint() printer power on");
            BufferedWriter bw = new BufferedWriter(new FileWriter("/sys/devices/platform/soc/soc:ns_power/ns_power"));
            bw.write("0x100");
            bw.close();
        } catch (IOException e) {
            Log.d(TAG, "Unable to write result file " + e.getMessage());
        }
    }

    private void OpenMtkPort() {
        try {
            String mPort;
            mPort = "/dev/ttyMT1";
            mSerialPort = new SerialPort(new File(mPort), iBaudRate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closePrinter() {
        Log.d(TAG, "closeSpiPrinter: ");
        ClosePrint();
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close(); // 关闭流
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        if (mFileInputStream != null) {
            try {
                mFileInputStream.close(); // 关闭流
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        if (mOutputStream != null) {
            try {
                mOutputStream.close(); // 关闭流
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        if (mInputStream != null) {
            try {
                mInputStream.close(); // 关闭流
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        if(mSerialPort!=null) {
            mSerialPort.close();
            mSerialPort=null;
        }

    }

    @Override
    public int printString(String s) {
        byte[] bytes = null;
        try {
            bytes = s.getBytes("gbk");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(null==bytes) return -1;

        printBytes(bytes);
        printBytes(PrinterFormatUtils.NEW_LINE);

        return 0;
    }

    @Override
    public int printBytes(byte[] bytes) {
        return writeSpi(bytes);
    }

    //----------------------------------------------------打印机上电
    private void OpenPrint(){
        Log.d(TAG, "OpenPrint: ");
        try {
            //上电
            BufferedWriter bw = new BufferedWriter(new FileWriter("/sys/devices/platform/ns_power/ns_power"));
            bw.write("0x100");
            bw.close();
        } catch (IOException e) {
        }
    }

    //----------------------------------------------------打印机下电
    private void ClosePrint(){
        Log.d(TAG, "ClosePrint: ");
        //下电
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/sys/devices/platform/ns_power/ns_power"));
            bw.write("0x101");
            bw.close();
        } catch (IOException e) {
        }
    }

/*
 spi 打印机需要支持串口通讯
 */

    private final int iBaudRate=115200;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private void OpenPort()  {
        try {
            String sPort;
            sPort = "/dev/ttyS0";
            mSerialPort =  new SerialPort(new File(sPort), iBaudRate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private boolean syncLock;
    public synchronized void lock() {
        while(syncLock) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        syncLock = true;
    }

    public synchronized void unlock() {
        syncLock = false;
        notifyAll();
    }



    public int writeSpi(byte[] buf) {
        if(buf==null)return -1;
        Log.d(TAG, "writeSpi: =  " + Arrays.toString(buf));
        lock();
        int length = buf.length;
        int offset = 0;
        int actual_length;
        try
        {
            byte[] write_buf = new byte[4096];
            while (offset < length) {
                int write_size = 4096;

                if (offset + write_size > length) {
                    write_size = length - offset;
                }
                System.arraycopy(buf, offset, write_buf, 0, write_size);
                actual_length = write_size;
                mFileOutputStream.write(write_buf);

                offset += actual_length;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            return -1;
        }
        unlock();
        return length;
    }

    public int readSpi(byte[] bufRead,byte[] bufWrite) {
        if (true) {
            return 1;
        }
        try {
            mFileOutputStream.write(bufWrite);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int a;
            a = mInputStream.read(bufRead);
            if(bufRead==null){
                return 0;
            }else {
                return bufRead.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
