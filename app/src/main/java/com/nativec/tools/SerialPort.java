package com.nativec.tools;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/***
 * 百富手持机驱动部分
 * 包括： 串口操作动态库和电平、电源操作CMD
 */
public class SerialPort {
    private static final String TAG = "PAXSerialPort";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    static {
        System.loadLibrary("serial_port");
    }

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        Log.d(TAG,"SerialPort");
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                throw new SecurityException();
            }
        }

        this.mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (this.mFd == null) {
            Log.e("SerialPort", "native open returns null");
            throw new IOException();
        } else {
            Log.d(TAG,"SerialPort mFd is valid"+mFd.toString());
            this.mFileInputStream = new FileInputStream(this.mFd);
            this.mFileOutputStream = new FileOutputStream(this.mFd);
        }
    }

    public FileInputStream getInputStream() {
        return this.mFileInputStream;
    }

    public FileOutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    private static native FileDescriptor open(String var0, int var1, int var2);

    public native void close();





    public static String vcc_en_gpio = "/sys/devices/soc/7af6000.i2c/i2c-6/6-006b/vcc_en_gpio";         //  打开机器5V电源输出
    public static String uhf_en_gpio = "/sys/devices/soc/7af6000.i2c/i2c-6/6-006b/uhf_en_gpio";         //  gpio_3.3(121)
    public static String handle_pwr = "/sys/devices/soc/7af6000.i2c/i2c-6/6-006b/handle_pwr";           //  gpio_1.8(74)
    public static String tla2021_vol = "/sys/devices/soc/7af6000.i2c/i2c-6/6-0048/tla2021_vol";         //  ICC检测
    public static String uhf_rfid_switch = "/sys/devices/soc/7af6000.i2c/i2c-6/6-006b/uhf_rfid_switch";

    public boolean haveRoot() {
        boolean mHaveRoot = false;

        int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
        if (ret != -1) {
            Log.i(TAG, "have root!");
            mHaveRoot = true;
        } else {
            Log.i(TAG, "not root!");
        }

        return mHaveRoot;
    }

    /**
     * 执行命令并且输出结果
     */
    public String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("sh");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 执行命令但不关注结果输出
     */
    public int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("sh");
            dos = new DataOutputStream(p.getOutputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void execLinuxCommand(String cmd){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process localProcess = runtime.exec("sh");
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();
        } catch (IOException e) {
            Log.i(TAG,"strLine:"+e.getMessage());
            e.printStackTrace();
        }
    }

}
