package com.rankway.controller;

import static com.rankway.controller.utils.SommerUtils.bytesToInt;
import static com.rankway.controller.utils.SommerUtils.getFloat;
import static org.junit.Assert.assertEquals;
import static java.lang.String.format;

import com.rankway.controller.utils.CRCUtil;
import com.rankway.sommerlibrary.utils.Base64Utils;
import com.rankway.sommerlibrary.utils.DES3Utils;
import com.rankway.sommerlibrary.utils.DateUtil;

import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    @Ignore
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    @Ignore
    public void test_crc() {
        byte[] cmd = new byte[3];
        cmd[0] = 0x30;
        cmd[1] = 0x00;
        cmd[2] = CRCUtil.calcCrc8(cmd, 0, 2);
//        XLog.i(TAG,"cmd "+SommerUtils.bytesToHexArrString(cmd));
        cmd = new byte[11];
        cmd[0] = 0x30;
        cmd[1] = 0x61;
        cmd[2] = 0x65;
        cmd[3] = 0x75;
        cmd[4] = 0x23;
        cmd[5] = 0x68;
        cmd[6] = 0x10;
        cmd[7] = 0x19;
        cmd[8] = 0x00;
        cmd[9] = 0x60;
        cmd[10] = (byte) 0x82;
//        XLog.i(TAG,"cmd "+SommerUtils.bytesToHexArrString(cmd));
        System.out.println(Arrays.toString(cmd));
        assertEquals(cmd[10], CRCUtil.calcCrc8(cmd, 0, 10));
    }


    @Test

    public void test_time() {
        byte[] t1 = {(byte) 0xa3, (byte) 0xe6, 0x02, 0x00, 0x27, 0x31, 0x02, 0x00, (byte) 0x86, (byte) 0xcf, (byte) 0xf0, 0x42, 0x6e, 0x0b, (byte) 0xfc, 0x41};

        int date = bytesToInt(t1, 0);
        System.out.println(date);
        String dateStr = String.format("%06d", date);
        System.out.println(dateStr);
        int time = bytesToInt(t1, 4);
        System.out.println(time);
        String timeStr = String.format("%06d-%06d", date, time);
        System.out.println(timeStr);

        Date d1 = DateUtil.parseDate("yyMMdd-HHmmss", timeStr);
        System.out.println(d1);


//        long datetime = bytes2Long(t1);
//        System.out.println(datetime);
//        long millions=new Long(date).longValue()*100;
//        millions = millions*60*60*24;
//        Calendar c=Calendar.getInstance();
//        c.set(Calendar.MINUTE,date);
//        System.out.println(""+c.getTime());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString = sdf.format(c.getTime());
//        System.out.println(dateString);
        float lng = getFloat(t1, 8);
        System.out.println(lng);
        float lat = getFloat(t1, 12);
        System.out.println(lat);

        byte[] t2 = {(byte) 0x46, (byte) 0x61, 0x61, 0x00, 0x02, 0x55, 0x02, 0x00, (byte) 0x86, (byte) 0xcf, (byte) 0xf0, 0x42, 0x6e, 0x0b, (byte) 0xfc, 0x41};
        StringBuilder sb = new StringBuilder();
        String tmp;

        tmp = format("%c", t2[0]);
        sb.append(tmp);


        tmp = format("%02x", t2[1]);
        sb.append(tmp);
        tmp = format("%02x", t2[2]);
        sb.append(tmp);
        tmp = format("%02x", t2[3]);
        sb.append(tmp);
        tmp = format("%02x", t2[4]);
        sb.append(tmp);
        tmp = format("%02x", t2[5]);
        sb.append(tmp);

        System.out.println(sb.toString());


    }


    @Test
    public void testEncode() {
        String paramStr = "{\"sbbh\":\"61000255\",\"jd\":\"118.182993\",\"wd\":\"36.490714\",\"bpsj\":\"2018-08-01 16:23:01\",\"bprysfz\":\"123456789123456789\",\"uid\":\"61000001028324,61000000916433,61000000942128\",\"xmbh\":\"370100X15040023\",\"htid\":\"370101318060002\"}";
        System.out.println(paramStr);
        byte[] paramArr = DES3Utils.encryptMode(paramStr.getBytes(), DES3Utils.PASSWORD_CRYPT_KEY);
        if (paramArr == null) {
            System.out.println("paramarr is null");
            return;
        } else {
            System.out.println(new String(paramArr));
            byte[] decode1 = Base64Utils.getEncodeBytes(paramArr);
            System.out.println(new String(decode1));
        }

    }

    @Test
    public void getCrpCode() throws UnsupportedEncodingException {
//        ReportDto reportDto = new ReportDto();
//        Result result = reportDto.getRptEncode(u1);
//        if (!result.isSuccess()) {
//            System.out.println("数据编码出错：" + result.getMessage());
//
//        }
//      String  urlString = URLEncoder.encode(result.getCode(), "GBK");
//        System.out.println(urlString);
    }
}