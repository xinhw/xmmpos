package com.rankway.controller.webapi;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.PaymentRecordEntity;
import com.rankway.controller.persistence.entity.PaymentShiftEntity;
import com.rankway.controller.persistence.entity.PaymentTotal;
import com.rankway.controller.persistence.entity.PersonInfoEntity;
import com.rankway.controller.persistence.entity.QrBlackListEntity;
import com.rankway.controller.persistence.entity.UserInfoEntity;
import com.rankway.controller.utils.AsyncHttpCilentUtil;
import com.rankway.controller.utils.Base64Util;
import com.rankway.controller.utils.DateStringUtils;
import com.rankway.controller.utils.HttpUtil;
import com.rankway.controller.webapi.menu.Result;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class payWebapi {
    private final String TAG = "payWebapi";

    private String secret = "6D119911B0B34BE894AB1A0C82518281";
    private String clientId = "1234567887654321";

    private String serverIP = "10.100.31.4";
    private int portNo = 8806;

    private String cposno;
    private String cusercode;

    final String CONTENT_TYPE_JSON = "application/json";
    final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";

    private int errCode;
    private String errMsg;

    private String menuServerIP = "10.100.31.2";
    private int menuPortNo = 6068;

    private String sessionAccessToken = "9e9a3635b1caff8d0ae6512e8d5e303d";
    private final int MAX_TRY_TIMES = 3;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getMenuServerIP() {
        return menuServerIP;
    }

    public void setMenuServerIP(String menuServerIP) {
        this.menuServerIP = menuServerIP;
    }

    public int getMenuPortNo() {
        return menuPortNo;
    }

    public void setMenuPortNo(int menuPortNo) {
        this.menuPortNo = menuPortNo;
    }

    public static payWebapi getInstance(){
        return SingletonHoler.sInstance;
    }

    public static class SingletonHoler{
        private static final payWebapi sInstance = new payWebapi();
    }

    public payWebapi(){

    }

    /***
     * 获取AccessTOKEN
     * @return
     */
    public String accessToken() {
        Log.d(TAG,"accessToken");

        String accessToken = "9e9a3635b1caff8d0ae6512e8d5e303d";

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url =  serverPort + "/api/Token?key=DF754DDD43F843E9BC8B0FAD6E58BB76&secret=6D119911B0B34BE894AB1A0C82518281";
        Log.d(TAG,"url="+url);

        HttpUtil httpUtil = new HttpUtil();
        String ret = null;

        //  循环3次
        for(int i=0;i<MAX_TRY_TIMES;i++) {
            ret = httpUtil.httpGet(url);
            if(ret!=null) break;
            sleep(100);
        }
        Log.d(TAG, "ret:" + ret);

        if(null!=ret) {
            HashMap retMap = (HashMap) fromJsonString(ret, HashMap.class);
            accessToken = (String) retMap.get("AccessToken");

            sessionAccessToken = accessToken;

            return accessToken;
        }else{
            errCode = httpUtil.getResponseCode();
            errMsg = "网络不通，请检查网络连接";
            return null;
        }
    }

    /***
     * 根据POS机号查询最大流水号
     * @param posno     POS机号
     * @return
     */
    public posAudit getPosAuditNo(String posno){
        Log.d(TAG,"getPosAuditNo");

        String accessToken = accessToken();
        Log.d(TAG,"accessToken:"+accessToken);
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String jsonData = String.format("posno=%s",posno);

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/pos?AccessToken=" + accessToken;
        Log.d(TAG,String.format("url=%s, %s",url,jsonData));

        HttpUtil httpUtil = new HttpUtil();
        String ret = httpUtil.httpPost(url,CONTENT_TYPE_URLENCODED,jsonData);
        Log.d(TAG,"ret:"+ret);

        errCode = httpUtil.getResponseCode();
        if(null==ret){
            errMsg = "平台返回信息为空";
            return null;
        }

        HashMap respParam = (HashMap) fromJsonString(ret, HashMap.class);
        if(null==respParam){
            Log.d(TAG,"respParam is null");
            errMsg = "后台返回信息格式出错";
            return null;
        }

        try {
            errCode = (int)Double.parseDouble(String.valueOf(respParam.get("errcode")));
            errMsg = String.valueOf(respParam.get("errmsg"));
        }catch (Exception e) {
            errCode = 101;
            errMsg = e.getMessage();
        }
        if (errCode != 0) return null;

        //  {"Result":{"PosNo":"30001","PosName":"艾雷斯POS1","PosCno":425},"errcode":0,"errmsg":"ok"}
        HashMap retMap = (HashMap) fromJsonString(String.valueOf(respParam.get("Result")), HashMap.class);
        Log.d(TAG,"retMap"+retMap);

        posAudit obj = new posAudit();
        //  POS机名称
        String posName = String.valueOf(retMap.get("PosName"));
        Log.d(TAG,"posName"+posName);
        obj.setPosName(posName);

        //  最大流水号+1
        String poscno = String.valueOf(retMap.get("PosCno"));
        Log.d(TAG,"poscno:"+poscno);

        Double d = Double.parseDouble(poscno);
        int auditNo = d.intValue();
        Log.d(TAG,"auditNo:"+auditNo);
        obj.setPosCno(auditNo);

//        String str = JSON.toJSONString(retMap);
//        Log.d(TAG,"retMap:"+ str);
//        return str;

        obj.setPosNo(posno);
        return obj;
    }

    /***
     * 根据工号（WorkNo）查询个人信息
     * @param workNo        工号
     * @return
     */
    public cardInfo getPersonInfoByWorkNo(String workNo){
        Log.d(TAG,"getPersonInfoByWorkNo");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + String.format("/api/personinfo/%s?accessToken=",workNo) + accessToken;
        Log.d(TAG,"URL:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpGet(url);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            //  {"Result":{"gremain":0.00,"WorkNo":"V1752010","Status":2,"Name":"李柏林","Cellphone":null,"CardNo":50022},"errcode":0,"errmsg":"ok"}
            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                Log.d(TAG,"respParam is null");
                errMsg = "后台返回信息格式出错";
                return null;
            }

            errCode = (int)Double.parseDouble(String.valueOf(responseBody.get("errcode")));
            errMsg = String.valueOf(responseBody.get("errmsg"));
            if (errCode != 0) return null;

            //  {"gremain":0.00,"WorkNo":"V1752010","Status":2,"Name":"李柏林","Cellphone":null,"CardNo":50022}
            HashMap retMap = (HashMap) fromJsonString(String.valueOf(responseBody.get("Result")), HashMap.class);
//            String str = JSON.toJSONString(retMap);

            cardInfo obj = new cardInfo();

            //  gremain
            String str = String.valueOf(retMap.get("gremain"));
            Log.d(TAG,"gremain:"+str);

            Double d = Double.parseDouble(str);
            obj.setGremain(d.floatValue());

            //  WorkNo
            obj.setGno(workNo);

            //  Status
            str = String.valueOf(retMap.get("Status"));
            Log.d(TAG,"Status:"+str);

            d = Double.parseDouble(str);
            obj.setStatusid(d.intValue());

            //  Name
            str = String.valueOf(retMap.get("Name"));
            Log.d(TAG,"Name"+str);
            obj.setName(str);

            //  Cellphone
            //  CardNo
            str = String.valueOf(retMap.get("CardNo"));
            Log.d(TAG,"CardNo:"+str);

            d = Double.parseDouble(str);
            obj.setCardno(d.intValue());

            return obj;
        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 根据卡唯一号查询个人信息
     * @param sno
     * @return
     */
    public cardInfo getPersonInfoBySNO(String sno){
        Log.d(TAG,"getPersonBySNO");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + String.format("/api/PersoninfoFromSnoV2/%s?accessToken=",sno) + sessionAccessToken;
        Log.d(TAG,"URL:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost(url,CONTENT_TYPE_JSON,"");
            Log.d(TAG,"ret:"+ret);

            WebapiResponse resp = JSON.parseObject(ret,WebapiResponse.class);
            Log.d(TAG,"resp:" +resp.toString());

            errMsg = resp.getErrmsg();
            if (resp.getError() != 0) return null;

            cardInfo obj = new cardInfo();

            //  gremain
            obj.setGremain((float)resp.getResult().getGremain());

            //  gno
            obj.setGno(resp.getResult().getGno());

            //  gname
            obj.setName(resp.getResult().getGname().toString().trim());

            //  StatusId
            obj.setStatusid(resp.getResult().getStatusId());

            //  cardno
            obj.setCardno(resp.getResult().getCardno());

            return obj;
        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 通过卡号查询个人信息
     * @param cardno       卡号（写入卡内的号码）
     * @return
     */
    public cardInfo getPersonInfoByCardNo(String cardno) {
        Log.d(TAG,"getCardPersonInfo");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/personinfo?accessToken=" + accessToken;
        Log.d(TAG,"URL:"+url);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("cardno", cardno);

        String reqParamsSet = Base64Util.EncodeString(toJsonString(requestMap));
        Log.d(TAG,"Base64编码前："+toJsonString(requestMap));
        Log.d(TAG,"Base64编码后："+reqParamsSet);

        Long timestamp = new Date().getTime();

        String data = secret + clientId + reqParamsSet + timestamp;
        String mac = DigestUtils.md5Hex(data);

        Log.d(TAG,String.format("计算mac字符串:{%s}，mac:{%s}", data, mac));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String reqTime = format.format(new Date(timestamp));

        Map body = new HashMap();
        body.put("clientID", clientId);
        body.put("reqTime", reqTime);
        body.put("reqParamsSet", reqParamsSet);
        body.put("mac", mac);

        String jsonData = JSON.toJSONString(body);
        Log.d(TAG,"jsonData:"+jsonData);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost(url,CONTENT_TYPE_JSON,jsonData);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            // {"Result":{"cardno":0,"gremain":0.0},"errcode":0,"errmsg":"ok"}
            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                errMsg = "后台返回信息格式出错";
                return null;
            }

            errCode = (int)Double.parseDouble(String.valueOf(responseBody.get("errcode")));
            errMsg = String.valueOf(responseBody.get("errmsg"));
            if (errCode != 0) return null;

            //  {"cardno":0,"gremain":0.0}
            HashMap retMap = (HashMap) fromJsonString(String.valueOf(responseBody.get("Result")), HashMap.class);

            cardInfo obj = new cardInfo();

            //  cardno
            String str = String.valueOf(retMap.get("cardno"));
            Log.d(TAG,"cardno:"+str);

            Double d = Double.parseDouble(str);
            obj.setCardno(d.intValue());

            //  gremain
            str = String.valueOf(retMap.get("gremain"));
            Log.d(TAG,"gremain:"+str);

            d = Double.parseDouble(str);
            obj.setGremain(d.floatValue());

            return obj;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 根据二维码信息查询个人信息
     * @param systemId
     * @param qrType
     * @param userId
     * @return
     */
    public cardInfo getPersonInfoByQrCode(int systemId,int qrType,String userId) {
        Log.d(TAG,"getQrPersonInfo");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/qr/personinfo?accessToken=" + sessionAccessToken;
        Log.d(TAG,"url:"+url);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("systemid", systemId);
        requestMap.put("qrtype", qrType);
        requestMap.put("userid", userId);

        String reqParamsSet = Base64Util.EncodeString(toJsonString(requestMap));
        Log.d(TAG,"Base64编码前："+toJsonString(requestMap));
        Log.d(TAG,"Base64编码后："+reqParamsSet);

        Long timestamp = new Date().getTime();

        String data = secret + clientId + reqParamsSet + timestamp;
        String mac = DigestUtils.md5Hex(data);

        Log.d(TAG,String.format("计算mac字符串:{%s}，mac:{%s}", data, mac));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String reqTime = format.format(new Date(timestamp));

        Map body = new HashMap();
        body.put("clientID", clientId);
        body.put("reqTime", reqTime);
        body.put("reqParamsSet", reqParamsSet);
        body.put("mac", mac);

        String jsonData = JSON.toJSONString(body);
        Log.d(TAG,"jsonData:"+jsonData);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost(url,CONTENT_TYPE_JSON,jsonData);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                errMsg = "后台返回信息格式出错";
                return null;
            }

            String respParamSet = (String) responseBody.get("respParamSet");
            if(null==respParamSet){
                errMsg = "后台返回信息respParamSet格式出错";
                return null;
            }

            // {"Result":{"gremain":567.39,"WorkNo":"00002203","Status":2,"Name":"杨欢","Cellphone":null,"CardNo":30943},
            // "errcode":0,"errmsg":"ok"}
            String responseParam = Base64Util.Decode2String(respParamSet);
            if(null==respParamSet){
                errMsg = "后台返回信息Base64解码出错";
                return null;
            }

            HashMap respParam = (HashMap) fromJsonString(responseParam, HashMap.class);
            Log.d(TAG,"responseParam:"+responseParam);

            //  {"Result":{"gremain":567.39,"WorkNo":"00002203","Status":2,"Name":"杨欢","Cellphone":null,"CardNo":30943},
            //  "errcode":0,"errmsg":"ok"}
            errCode = (int)Double.parseDouble(String.valueOf(respParam.get("errcode")));
            errMsg = String.valueOf(respParam.get("errmsg"));
            if(errCode!=0) return null;

            // log.info("对respParamSet中的Result进行JSON转换,内容:{}", String.valueOf(respParam.get("Result")));
            HashMap retMap = (HashMap) fromJsonString(String.valueOf(respParam.get("Result")), HashMap.class);

            //  {"gremain":567.39,"WorkNo":"00002203","Status":2,"Name":"杨欢","Cellphone":null,"CardNo":30943}
            cardInfo obj = new cardInfo();

            //  gremain
            String str = String.valueOf(retMap.get("gremain"));
            Log.d(TAG,"gremain:"+str);

            Double d = Double.parseDouble(str);
            obj.setGremain(d.floatValue());

            //  WorkNo
            str = String.valueOf(retMap.get("WorkNo"));
            Log.d(TAG,"WorkNo:"+str);
            obj.setGno(str);

            //  Status
            str = String.valueOf(retMap.get("Status"));
            Log.d(TAG,"Status:"+str);

            d = Double.parseDouble(str);
            obj.setStatusid(d.intValue());

            //  Name
            str = String.valueOf(retMap.get("Name"));
            Log.d(TAG,"Name"+str);
            obj.setName(str);

            //  Cellphone
            //  CardNo
            str = String.valueOf(retMap.get("CardNo"));
            Log.d(TAG,"CardNo:"+str);

            d = Double.parseDouble(str);
            obj.setCardno(d.intValue());

            obj.setSystemId(systemId);
            obj.setQrType(qrType);
            obj.setUserId(userId);

            return obj;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 根据卡号查询交易记录
     * @param cardsno
     * @return
     */
    public String getRecordsByCardSNO(String cardsno){
        Log.d(TAG,"cardPayRecords");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + String.format("/api/payrecords/%s/?accessToken=%s",cardsno,accessToken);
        Log.d(TAG,"url:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpGet(url);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            HashMap respParam = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==respParam){
                errMsg = "后台返回信息格式出错";
                return null;
            }

            errCode = (int)Double.parseDouble(String.valueOf(respParam.get("errcode")));
            errMsg = String.valueOf(respParam.get("errmsg"));
            if (errCode != 0) return null;

            String s = respParam.get("count").toString();
            Log.d(TAG,"返回记录数："+s);

            /***
             * └amount	    交易金额
             * └remain	    余额
             * └transtype	交易类型
             * └canteenname	餐厅（设备）名称
             * └transtime	交易时间
             */
            String str = JSONObject.toJSONString(respParam.get("records"));
            Log.d(TAG,"RESULT:"+ str);
            return str;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 根据二维码信息查询历史交易记录
     * @param systemId      二维码中的SystemId
     * @param qrType        二维码中的类型
     * @param userId        二维码中的UserId
     * @return
     */
    public String getRecordsByQrCode(int systemId,int qrType,String userId){
        Log.d(TAG,"qrPayRecords");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/qr/V2/payrecords?accessToken=" + accessToken;
        Log.d(TAG,"url:"+url);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("systemid", systemId);
        requestMap.put("qrtype", qrType);
        requestMap.put("userid", userId);
        requestMap.put("transtype", 2);

        String reqParamsSet = Base64Util.EncodeString(toJsonString(requestMap));
        Log.d(TAG,"Base64编码前："+toJsonString(requestMap));
        Log.d(TAG,"Base64编码后："+reqParamsSet);

        Long timestamp = new Date().getTime();

        String data = secret + clientId + reqParamsSet + timestamp;
        String mac = DigestUtils.md5Hex(data);

        Log.d(TAG,String.format("计算mac字符串:{%s}，mac:{%s}", data, mac));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String reqTime = format.format(new Date(timestamp));

        Map body = new HashMap();
        body.put("clientID", clientId);
        body.put("reqTime", reqTime);
        body.put("reqParamsSet", reqParamsSet);
        body.put("mac", mac);

        String jsonData = JSON.toJSONString(body);
        Log.d(TAG,"jsonData:"+jsonData);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost(url,CONTENT_TYPE_JSON,jsonData);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                errMsg = "后台返回信息格式出错";
                return null;
            }

            String respParamSet = (String) responseBody.get("respParamSet");
            if(null==respParamSet){
                errMsg = "后台返回信息respParamSet格式出错";
                return null;
            }

            String responseParam = Base64Util.Decode2String(respParamSet);
            if(null==respParamSet){
                Log.d(TAG,"responseParam is null");
                errMsg = "后台返回信息Base64解码出错";
                return null;
            }

            HashMap respParam = (HashMap) fromJsonString(responseParam, HashMap.class);
            Log.d(TAG,"respParam:"+responseParam);

            errCode = (int)Double.parseDouble(String.valueOf(respParam.get("errcode")));
            errMsg = String.valueOf(respParam.get("errmsg"));
            if (errCode != 0) return null;

            // {"Result":[
            // {"PosNo":"68001","Cno":"5324    ","CanteenName":"文新医务室","Amount":29.8,"Remain":567.39,"TransType":0,"TransTime":"2022-12-16T14:55:54"},
            // {"PosNo":"22001","Cno":"879655  ","CanteenName":"文新1楼超市","Amount":21.0,"Remain":597.19,"TransType":0,"TransTime":"2022-12-16T11:52:36"},
            // {"PosNo":"21003","Cno":"122196  ","CanteenName":"文新1F咖啡吧","Amount":49.0,"Remain":618.19,"TransType":0,"TransTime":"2022-12-14T12:44:13"},
            // {"PosNo":"28004","Cno":"34451   ","CanteenName":"上报大食堂(梅龙镇)","Amount":17.0,"Remain":667.19,"TransType":0,"TransTime":"2022-12-13T11:14:48"},
            // {"PosNo":"28004","Cno":"33797   ","CanteenName":"上报大食堂(梅龙镇)","Amount":28.0,"Remain":684.19,"TransType":0,"TransTime":"2022-12-12T11:42:25"},
            // {"PosNo":"28002","Cno":"11756   ","CanteenName":"上报大食堂(梅龙镇)","Amount":17.0,"Remain":712.19,"TransType":0,"TransTime":"2022-12-09T11:35:04"},
            // {"PosNo":"28004","Cno":"30732   ","CanteenName":"上报大食堂(梅龙镇)","Amount":22.0,"Remain":729.19,"TransType":0,"TransTime":"2022-12-06T11:23:27"},
            // {"PosNo":"28001","Cno":"6186    ","CanteenName":"上报大食堂(梅龙镇)","Amount":60.0,"Remain":751.19,"TransType":0,"TransTime":"2022-12-05T17:43:01"},
            // {"PosNo":"88888","Cno":"581188  ","CanteenName":"文新后台管理","Amount":800.0,"Remain":811.19,"TransType":1,"TransTime":"2022-11-30T15:02:47"},
            // {"PosNo":"21003","Cno":"120151  ","CanteenName":"文新1F咖啡吧","Amount":102.0,"Remain":11.19,"TransType":0,"TransTime":"2022-11-25T15:41:59"},
            // {"PosNo":"28006","Cno":"8129    ","CanteenName":"上报大食堂(梅龙镇)","Amount":17.0,"Remain":113.19,"TransType":0,"TransTime":"2022-11-24T11:17:46"},
            // {"PosNo":"28005","Cno":"8379    ","CanteenName":"上报大食堂(梅龙镇)","Amount":120.0,"Remain":130.19,"TransType":0,"TransTime":"2022-11-23T15:55:14"},
            // {"PosNo":"28005","Cno":"8378    ","CanteenName":"上报大食堂(梅龙镇)","Amount":35.0,"Remain":250.19,"TransType":0,"TransTime":"2022-11-23T15:13:48"},
            // {"PosNo":"68001","Cno":"4767    ","CanteenName":"文新医务室","Amount":29.8,"Remain":285.19,"TransType":0,"TransTime":"2022-11-22T15:00:48"},
            // {"PosNo":"28005","Cno":"7766    ","CanteenName":"上报大食堂(梅龙镇)","Amount":30.0,"Remain":314.99,"TransType":0,"TransTime":"2022-11-22T10:59:40"},
            // {"PosNo":"28005","Cno":"7763    ","CanteenName":"上报大食堂(梅龙镇)","Amount":6.0,"Remain":344.99,"TransType":0,"TransTime":"2022-11-22T10:45:30"},
            // {"PosNo":"50007","Cno":"10268   ","CanteenName":"盒马分组","Amount":1100.0,"Remain":350.99,"TransType":0,"TransTime":"2022-11-21T14:02:35"},
            // {"PosNo":"22001","Cno":"867517  ","CanteenName":"文新1楼超市","Amount":10.5,"Remain":1450.99,"TransType":0,"TransTime":"2022-11-15T13:52:22"},
            // {"PosNo":"28004","Cno":"19661   ","CanteenName":"上报大食堂(梅龙镇)","Amount":2.0,"Remain":1461.49,"TransType":0,"TransTime":"2022-11-15T08:57:45"},
            // {"PosNo":"28004","Cno":"19081   ","CanteenName":"上报大食堂(梅龙镇)","Amount":30.0,"Remain":1463.49,"TransType":0,"TransTime":"2022-11-14T11:33:45"}],
            // "errcode":0,"errmsg":"ok"}
            String str = JSONObject.toJSONString(respParam.get("Result"));
            Log.d(TAG,"RESULT:"+ str);
            return str;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * IC卡支付
     * @param auditNo   本地流水号
     * @param cardno    卡唯一号
     * @param cdate     本机交易日期时间
     * @param cmoney    交易金额
     * @return
     */
    public int cardPayment(int auditNo,int cardno,Date cdate,int cmoney){
        Log.d(TAG,"cardPayment");

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/Payinfoes?accessToken=" + sessionAccessToken;
        Log.d(TAG,"url:"+url);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String reqTime = format.format(cdate);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("clientID", clientId);

        requestMap.put("cno", auditNo+"");
        requestMap.put("cposno", cposno);
        requestMap.put("cusercode", cusercode);
        requestMap.put("cardno", cardno);
        double famount = (double) (0.01 * cmoney);
        requestMap.put("cmoney", famount);
        requestMap.put("cdate",reqTime);

        String reqParamsSet = hashMapEncode(requestMap);
        Log.d(TAG,"数据："+reqParamsSet);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost201(url,CONTENT_TYPE_URLENCODED,reqParamsSet);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            Log.d(TAG,"ResponseCode:"+errCode);
            if(errCode==201) return 0;

            if(null==ret){
                errMsg = "平台返回信息为空";
                return -1;
            }

            // {"cno":"430","cposno":"30001","cpostype":"0","cpayway":"2","cusercode":"91001","cardno":4943,"cdate":"2022-12-27T14:43:43.984+08:00",
            // "cmoney":0.12,"cremain":0.0,"cnote":null,"typeid":100,"localtime":null,"SystemID":null,"personinfo":null}
            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                errMsg = "后台返回信息格式出错";
                return -1;
            }

            String str = JSON.toJSONString(responseBody);
            return -1;
        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return -1;
    }

    /***
     * 二维码支付接口
     * @param auditNo       本地流水号
     * @param systemId      二维码中的SystemId
     * @param qrType        二维码中的类型
     * @param userId        二维码中用户Id
     * @param cdate         本地的交易日期时间
     * @param cmoney        交易金额
     * @return
     */
    public int qrPayment(int auditNo,int systemId,int qrType,String userId,Date cdate,int cmoney){
        Log.d(TAG,"qrPayment");

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/qr/payinfoes?accessToken=" + sessionAccessToken;
        Log.d(TAG,"url:"+url);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String reqTime = format.format(cdate);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("cno", auditNo);
        requestMap.put("cposno", cposno);
        requestMap.put("cusercode", cusercode);
        requestMap.put("systemid", systemId);
        requestMap.put("qrtype", qrType);
        requestMap.put("userid", userId);
        requestMap.put("cdate", reqTime);
        double famount = (double)(cmoney*0.01);
        requestMap.put("cmoney", famount);
        Log.d(TAG,"requestMap "+requestMap.toString());

        String reqParamsSet = Base64Util.EncodeString(toJsonString(requestMap));

        String data = secret + clientId + reqParamsSet + cdate.getTime();
        String mac = DigestUtils.md5Hex(data);

        Log.d(TAG,String.format("消费（二维码），mac前字符串:{%s}，mac后字符串:{%s}", data, mac));

        Map body = new HashMap();
        body.put("clientID", clientId);
        body.put("reqTime", reqTime);
        body.put("reqParamsSet", reqParamsSet);
        body.put("mac", mac);

        String jsonData = JSON.toJSONString(body);
        Log.d(TAG,"jsonData:"+jsonData);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost201(url,CONTENT_TYPE_JSON,jsonData);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();

            if(null==ret){
                errMsg = "平台返回信息为空";
                return -1;
            }

            //  {"Code":"201","Msg":"Created","respTime":"2022-12-27T14:50:40.329981+08:00",
            //  "respParamSet":"eyJjbm8iOiI0MzEiLCJjcG9zbm8iOiIzMDAwMSIsImNwb3N0eXBlIjoiMCIsImNwYXl3YXkiOiIyIiwiY3VzZXJjb2RlIjoiOTEwMDEiLCJjYXJkbm8iOjQ5NDMsImNkYXRlIjoiMjAyMi0xMi0yN1QxNDo1MDo0MC40NjErMDg6MDAiLCJjbW9uZXkiOjAuMDIsImNyZW1haW4iOjAuMCwiY25vdGUiOiJxciIsInR5cGVpZCI6MTAwLCJsb2NhbHRpbWUiOm51bGwsIlN5c3RlbUlEIjpudWxsLCJwZXJzb25pbmZvIjpudWxsfQ==",
            //  "Mac":"12c9a02109a49ebb0e90ba645d9fe966"}
            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                errMsg = "后台返回信息格式出错";
                return -1;
            }

            String respParamSet = (String) responseBody.get("respParamSet");
            if(null==respParamSet){
                errMsg = "后台返回信息respParamSet格式出错";
                return -1;
            }

            String responseParam = Base64Util.Decode2String(respParamSet);
            if(null==respParamSet){
                Log.d(TAG,"responseParam is null");
                errMsg = "后台返回信息Base64解码出错";
                return -1;
            }

            //  {"cno":"433","cposno":"30001","cpostype":"0","cpayway":"2","cusercode":"91001","cardno":4943,
            //  "cdate":"2022-12-27T14:55:42.851+08:00","cmoney":0.02,"cremain":0.0,
            //  "cnote":"qr","typeid":100,"localtime":null,"SystemID":null,"personinfo":null}
            Log.d(TAG,String.format("对api返回的respParamSet进行JSON转换,内容:{%s}", responseParam));
            HashMap respParam = (HashMap) fromJsonString(responseParam, HashMap.class);

//            errCode = (int)Double.parseDouble(String.valueOf(respParam.get("errcode")));
//            errMsg = String.valueOf(respParam.get("errmsg"));
//            if (errCode != 0) return -1;
//
//            HashMap retMap = (HashMap) fromJsonString(String.valueOf(respParam.get("Result")), HashMap.class);
//            String str = JSON.toJSONString(retMap);
//            Log.d(TAG,"JSON:"+str);

            if(errCode==201) return 0;
            return -1;
        } catch (Exception e) {
            errMsg = e.getMessage();
            Log.d(TAG,errMsg);
        }
        return -1;
    }

    /**
     * <p>Json字符串转为对象</p>
     */
    public Object fromJsonString(String jsonString, Class<?> clz) {
        jsonString = jsonString.replaceAll(" ","").replaceAll("=,","=null,");

        Gson gson = new Gson();

        return gson.fromJson(jsonString, clz);
    }

    /**
     * <p>Json字符串转为对象</p>
     */
    public String toJsonString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public String getCposno() {
        return cposno;
    }

    public void setCposno(String cposno) {
        this.cposno = cposno;
    }

    public String getCusercode() {
        return cusercode;
    }

    public void setCusercode(String cusercode) {
        this.cusercode = cusercode;
    }

    private String hashMapEncode(Map<String,Object> requestMap){
        String str ="";
        if(requestMap.size()>0){
            for(Map.Entry<String,Object> entry:requestMap.entrySet()){
                String str0 = String.format("%s=%s",entry.getKey(), URLEncoder.encode(entry.getValue().toString()));
                if(str.length()>0)
                    str = str +"&" + str0;
                else
                    str = str + str0;
            }
        }
        Log.d(TAG,"hashMapEncode:"+str);
        return str;
    }

    // "cardno":520,"gsno":"de750674","gname":"童惠勇    ","gsex":"","gdeptname":"","deptId":"","StatusId":3,"gno":"66666757","gremain":0.00
    public static class PersonInfo{
        int cardno;
        String gsno;
        String gname;
        String gsex;
        String gdeptname;
        int deptId;
        int StatusId;
        String gno;
        double gremain;

        public int getCardno() {
            return cardno;
        }

        public void setCardno(int cardno) {
            this.cardno = cardno;
        }

        public String getGsno() {
            return gsno;
        }

        public void setGsno(String gsno) {
            this.gsno = gsno;
        }

        public String getGname() {
            return gname;
        }

        public void setGname(String gname) {
            this.gname = gname;
        }

        public String getGsex() {
            return gsex;
        }

        public void setGsex(String gsex) {
            this.gsex = gsex;
        }

        public String getGdeptname() {
            return gdeptname;
        }

        public void setGdeptname(String gdeptname) {
            this.gdeptname = gdeptname;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public int getStatusId() {
            return StatusId;
        }

        public void setStatusId(int statusId) {
            StatusId = statusId;
        }

        public String getGno() {
            return gno;
        }

        public void setGno(String gno) {
            this.gno = gno;
        }

        public double getGremain() {
            return gremain;
        }

        public void setGremain(double gremain) {
            this.gremain = gremain;
        }

        @Override
        public String toString() {
            return "PersonInfo{" +
                    "cardno=" + cardno +
                    ", gsno='" + gsno + '\'' +
                    ", gname='" + gname + '\'' +
                    ", gsex='" + gsex + '\'' +
                    ", gdeptname='" + gdeptname + '\'' +
                    ", deptId=" + deptId +
                    ", StatusId=" + StatusId +
                    ", gno='" + gno + '\'' +
                    ", gremain=" + gremain +
                    '}';
        }
    }

    public static class WebapiResponse{
        int error;
        String errmsg;
        PersonInfo Result;

        public int getError() {
            return error;
        }

        public void setError(int error) {
            this.error = error;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public PersonInfo getResult() {
            return Result;
        }

        public void setResult(PersonInfo result) {
            Result = result;
        }

        @Override
        public String toString() {
            return "WebapiResponse{" +
                    "error=" + error +
                    ", errmsg='" + errmsg + '\'' +
                    ", Result=" + Result +
                    '}';
        }
    }

    public static class WebapiResult{
        PayRecord Result;
        int errcode;
        String errmsg;

        public PayRecord getResult() {
            return Result;
        }

        public void setResult(PayRecord result) {
            Result = result;
        }

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        @Override
        public String toString() {
            return "WebapiResult{" +
                    "Result=" + Result +
                    ", errcode=" + errcode +
                    ", errmsg='" + errmsg + '\'' +
                    '}';
        }
    }

    public static class PayRecord{
        String cno;
        String cposno;
        int cpostype;
        int cpayway;
        String cusercode;
        int cardno;
        String cdate;
        float cmoney;
        String cnote;
        int typeid;
        String localtime;

        int systemid;
        int qrtype;
        String userid;

        public String getCno() {
            return cno;
        }

        public void setCno(String cno) {
            this.cno = cno;
        }

        public String getCposno() {
            return cposno;
        }

        public void setCposno(String cposno) {
            this.cposno = cposno;
        }

        public int getCpostype() {
            return cpostype;
        }

        public void setCpostype(int cpostype) {
            this.cpostype = cpostype;
        }

        public int getCpayway() {
            return cpayway;
        }

        public void setCpayway(int cpayway) {
            this.cpayway = cpayway;
        }

        public String getCusercode() {
            return cusercode;
        }

        public void setCusercode(String cusercode) {
            this.cusercode = cusercode;
        }

        public int getCardno() {
            return cardno;
        }

        public void setCardno(int cardno) {
            this.cardno = cardno;
        }

        public String getCdate() {
            return cdate;
        }

        public void setCdate(String cdate) {
            this.cdate = cdate;
        }

        public float getCmoney() {
            return cmoney;
        }

        public void setCmoney(float cmoney) {
            this.cmoney = cmoney;
        }

        public String getCnote() {
            return cnote;
        }

        public void setCnote(String cnote) {
            this.cnote = cnote;
        }

        public int getTypeid() {
            return typeid;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public String getLocaltime() {
            return localtime;
        }

        public void setLocaltime(String localtime) {
            this.localtime = localtime;
        }

        public int getSystemid() {
            return systemid;
        }

        public void setSystemid(int systemid) {
            this.systemid = systemid;
        }

        public int getQrtype() {
            return qrtype;
        }

        public void setQrtype(int qrtype) {
            this.qrtype = qrtype;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        @Override
        public String toString() {
            return "PayRecord{" +
                    "cno='" + cno + '\'' +
                    ", cposno='" + cposno + '\'' +
                    ", cpostype=" + cpostype +
                    ", cpayway=" + cpayway +
                    ", cusercode='" + cusercode + '\'' +
                    ", cardno=" + cardno +
                    ", cdate='" + cdate + '\'' +
                    ", cmoney=" + cmoney +
                    ", cnote='" + cnote + '\'' +
                    ", typeid=" + typeid +
                    ", localtime='" + localtime + '\'' +
                    ", systemid=" + systemid +
                    ", qrtype=" + qrtype +
                    ", userid=" + userid +
                    '}';
        }
    }

    /***
     * 获取黑名单
     * @return
     */
    public List<QrBlackListEntity> getQrBlackList(){
        Log.d(TAG,"getQrBlackList");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + String.format("/api/Personinfo/BarCodeblacklist?accessToken=%s",accessToken);
        Log.d(TAG,"URL:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpGet(url);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            Log.d(TAG,"返回："+ret);

            List<QrBlackListEntity> list = JSON.parseArray(ret, QrBlackListEntity.class);
            return list;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 获取操作员
     * @return
     */
    public List<UserInfoEntity> getUserInfoList(){
        Log.d(TAG,"getUserInfoList");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + String.format("/api/userinfo?accessToken=%s",accessToken);
        Log.d(TAG,"URL:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpGet(url);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            Log.d(TAG,"返回："+ret);
            List<UserInfoEntity> list = JSON.parseArray(ret, UserInfoEntity.class);
            return list;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 获取IC卡白名单信息
     * @return
     */
    public List<PersonInfoEntity> getPersonInfoList(){
        Log.d(TAG,"getPersonInfoList");

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return null;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + String.format("/api/Personinfo/whitelist?accessToken=%s",accessToken);
        Log.d(TAG,"URL:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpGet(url);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            Log.d(TAG,"返回："+ret);
            List<PersonInfoEntity> list = JSON.parseArray(ret, PersonInfoEntity.class);
            return list;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return null;
    }

    /***
     * 批量上送离线IC卡交易
     * @param record
     * @return
     */
    public int pushOfflineCardPaymentRecords(PaymentRecordEntity record){
        Log.d(TAG,"pushOfflineCardPaymentRecords "+record.toString());

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return -1;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/payinfoes/list?accessToken=" + accessToken;
        Log.d(TAG,"url:"+url);

        PaymentApplyEntity pae = new PaymentApplyEntity(record);
        List<PaymentApplyEntity> records = new ArrayList<>();
        records.add(pae);

        String jsonData = JSON.toJSONString(records);
        Log.d(TAG,"jsonData:"+jsonData);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost(url,CONTENT_TYPE_JSON,jsonData);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return -1;
            }

            WebapiResult wr = JSON.parseObject(ret,WebapiResult.class);
            Log.d(TAG,"wr:"+wr.toString());

            if(null==wr){
                errMsg = "后台返回信息格式出错";
                return -1;
            }

            //  {"errcode":0,"errmsg":"ok"}
            errCode = wr.getErrcode();
            errMsg = wr.getErrmsg();
            Log.d(TAG,"errMsg:"+errMsg);

            if (errCode == 0) return errCode;

            //  重复的返回
            //  {"Result":{"cno":"47031425","cposno":"20001","cpostype":null,"cpayway":null,"cusercode":"90001","cardno":18985,"cdate":"2023-10-05T19:34:19.542+08:00",
            //  "cmoney":1.2,"cremain":0.0,"cnote":null,"typeid":100,"localtime":null,"SystemID":0,"personinfo":null},"errcode":409,"errmsg":"数据冲突"}
            //  需要比较一下cardno,cdate,cmoney是否一样，如果一样就认为成功
            if(errCode==409){
                Log.d(TAG,"errCode:409");

                // cardno
                if(wr.getResult().getCardno()!=record.getCardno()){
                    Log.d(TAG,"卡号不一样，是主键重复");
                    return  errCode;
                }

                // "cmoney":1.2
                if(Math.abs(wr.getResult().getCmoney()-record.getAmount())>0.001){
                    Log.d(TAG,"金额不一样，是主键重复");
                    return errCode;
                }

                Log.d(TAG,"记录重新传输，算成功");

                return 0;
            }

            return errCode;

        } catch (Exception e) {
            e.printStackTrace();
            errMsg = e.getMessage();
        }

        return -1;
    }

    /***
     * 批量上送二维码交易
     * @param record
     * @return
     */
    public int pushOfflineQRPaymentRecords(PaymentRecordEntity record){
        Log.d(TAG,"pushOfflineQRPaymentRecords "+record.toString());

        String accessToken = accessToken();
        if(null==accessToken){
            errMsg = "网络不通，请检查网络连接！";
            return -1;
        }

        String serverPort = String.format("http://%s:%d",serverIP,portNo);
        String url = serverPort + "/api/qr/payinfoes/listv2?accessToken=" + accessToken;
        Log.d(TAG,"url:"+url);

        PaymentApplyEntity pae = new PaymentApplyEntity(record);
        List<PaymentApplyEntity> records = new ArrayList<>();
        records.add(pae);

        Date cdate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String reqTime = format.format(cdate);

        String jsonData = JSON.toJSONString(records);
        Log.d(TAG,"jsonData:"+jsonData);

        String reqParamsSet = Base64Util.EncodeString(jsonData);

        String data = secret + clientId + reqParamsSet + cdate.getTime();
        String mac = DigestUtils.md5Hex(data);

        Log.d(TAG,String.format("离线消费（二维码）记录，mac前字符串:{%s}，mac后字符串:{%s}", data, mac));

        Map body = new HashMap();
        body.put("clientID", clientId);
        body.put("reqTime", reqTime);
        body.put("reqParamsSet", reqParamsSet);
        body.put("mac", mac);

        jsonData = JSON.toJSONString(body);
        Log.d(TAG,"jsonData:"+jsonData);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpPost(url,CONTENT_TYPE_JSON,jsonData);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return -1;
            }

            //  {"Code":"200","Msg":"OK","respTime":"2023-10-05T19:53:37.0126618+08:00",
            //  "respParamSet":"eyJlcnJjb2RlIjowLCJlcnJtc2ciOiJvayJ9","Mac":"94701a3c363a92d620b4166220b0bbe1"}
            HashMap responseBody = (HashMap) fromJsonString(ret, HashMap.class);
            if(null==responseBody){
                errMsg = "后台返回信息格式出错";
                return -1;
            }

            String respParamSet = (String) responseBody.get("respParamSet");
            if(null==respParamSet){
                errMsg = "后台返回信息respParamSet格式出错";
                return -1;
            }

            String responseParam = Base64Util.Decode2String(respParamSet);
            if(null==respParamSet){
                Log.d(TAG,"responseParam is null");
                errMsg = "后台返回信息Base64解码出错";
                return -1;
            }

            //  {{"errcode":0,"errmsg":"ok"}}
            WebapiResult wr = JSON.parseObject(responseParam,WebapiResult.class);
            Log.d(TAG,"wr:"+wr.toString());

            if(null==wr){
                errMsg = "后台返回信息格式出错";
                return -1;
            }

            //  需要填充内容
            errCode = wr.getErrcode();
            Log.d(TAG,"errcode:"+errCode);

            errMsg = wr.getErrmsg();
            Log.d(TAG,"errmsg:"+errMsg);

            if (errCode == 0) return errCode;

            //  重复的返回
            //  {{"errcode":403,"errmsg":"设备交易流水已存在 caused by {\"cno\":\"47031428\",\"cposno\":\"20001\",\"cusercode\":\"90001\",
            //  \"cdate\":\"2023-10-05T20:03:41.369+08:00\",\"cmoney\":1.0,\"SystemId\":1,\"QrType\":1,\"UserId\":\"737589\"}"}}
            if (errCode == 409){
                if(!wr.getResult().getUserid().equalsIgnoreCase(record.getUserId())) return errCode;

                Log.d(TAG,"记录重新传输，算成功");

                return 0;
            }

            return errCode;

        } catch (Exception e) {
            errMsg = e.getMessage();
        }

        return -1;
    }


    /***
     * 获取菜品明细
     * @param posno
     * @return
     */
    public Result getDishType(String posno){
        Log.d(TAG,"getDishType "+posno);
        if(StringUtils.isEmpty(posno)) return null;

        String serverPort = String.format("http://%s:%d",menuServerIP,menuPortNo);
        String url = serverPort + String.format("/dishes/findByPosno?posno=%s",posno);
        Log.d(TAG,"URL:"+url);

        try {
            HttpUtil httpUtil = new HttpUtil();
            String ret = httpUtil.httpGet(url);
            Log.d(TAG,"ret:"+ret);

            errCode = httpUtil.getResponseCode();
            if(null==ret){
                errMsg = "平台返回信息为空";
                return null;
            }

            Log.d(TAG,"返回："+ret);
            Result result = JSON.parseObject(ret,Result.class);

            Log.d(TAG,"请求结果：" + result.getMessage());
            Log.d(TAG,"code:"+result.getCode());

            //  code必须是：40000
            if(40000!=result.getCode()) return null;

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            errMsg = e.getMessage();
        }
        return null;
    }

    /***
     * 上送支付明细
     * @param paymentTotal
     * @return
     */
    public int uploadPaymentItems(PaymentTotal paymentTotal){
        Log.d(TAG,"uploadPaymentItems ");

        // http://121.36.16.185:6062/transRecords/upload
        String serverPort = String.format("http://%s:%d",menuServerIP,menuPortNo);
        String url = serverPort + "/transRecords/upload";
        Log.d(TAG,"URL:"+url);

        String jsondata = JSON.toJSONString(paymentTotal);
        Log.d(TAG,"JSON:"+jsondata);

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpPostJson(url, jsondata, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DetLog.writeLog(TAG,"上传消费明细失败："+e.getMessage());

                paymentTotal.setUploadFlag(PaymentTotal.UNUPLOAD);
                DBManager.getInstance().getPaymentTotalDao().save(paymentTotal);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strjson = response.body().string();
                Log.d(TAG, "onSuccess:" + strjson);
                try {
                    Result result = JSON.parseObject(strjson, Result.class);
                    Log.d(TAG,"Result:"+result.toString());
                    if(result.getCode()>0) {
                        paymentTotal.setUploadFlag(PaymentTotal.UPLOADED);
                        DetLog.writeLog(TAG, "上传明细成功：" + JSON.toJSONString(paymentTotal));
                    }else{
                        paymentTotal.setUploadFlag(PaymentTotal.UNUPLOAD);
                        DetLog.writeLog(TAG,"上传消费明细失败：");
                    }
                    DBManager.getInstance().getPaymentTotalDao().save(paymentTotal);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return 0;
    }

    private void sleep(int ms){
        try{
            Thread.sleep(ms);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***
     * 开班报文
     * @param shiftEntity
     * @return
     */
    public int uploadShiftOn(PaymentShiftEntity shiftEntity){

        String serverPort = String.format("http://%s:%d",menuServerIP,menuPortNo);
        String url = serverPort + "/posshift/shifton?posno="+getCposno();
        Log.d(TAG,"URL:"+url);

        String jsondata = JSON.toJSONString(shiftEntity);
        DetLog.writeLog(TAG,"开班JSON:"+jsondata);

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpPostJson(url, jsondata, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DetLog.writeLog(TAG,"上传开班记录失败："+e.getMessage());
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strjson = response.body().string();
                Log.d(TAG, "onSuccess:" + strjson);
                try {
                    Result result = JSON.parseObject(strjson, Result.class);
                    Log.d(TAG,"Result:"+result.toString());
                    if(result.getCode()>=0){
                        DetLog.writeLog(TAG,"上传开班记录成功："+strjson);
                    }else{
                        DetLog.writeLog(TAG,"上传开班记录失败："+strjson);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return 0;
    }


    /***
     * 上传结班记录
     * @param shiftEntity
     * @return
     */
    public int uploadShiftOff(PaymentShiftEntity shiftEntity){
        Log.d(TAG,"uploadShiftOff");

        String serverPort = String.format("http://%s:%d",menuServerIP,menuPortNo);
        String url = serverPort + "/posshift/shiftoff?posno="+getCposno();
        Log.d(TAG,"URL:"+url);

        shiftEntity.setShiftNo(DateStringUtils.getYYMMDDHHMMss(shiftEntity.getShiftOnTime())+shiftEntity.getShiftOnAuditNo());

        String jsondata = JSON.toJSONString(shiftEntity);
        DetLog.writeLog(TAG,"结班JSON:"+jsondata);

        AsyncHttpCilentUtil asyncHttpCilentUtil = new AsyncHttpCilentUtil();
        asyncHttpCilentUtil.httpPostJson(url, jsondata, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DetLog.writeLog(TAG,"上传结班记录失败："+e.getMessage());
                shiftEntity.setStatus(PaymentShiftEntity.SHIFT_STATUS_OFF);
                DBManager.getInstance().getPaymentShiftEntityDao().save(shiftEntity);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strjson = response.body().string();
                Log.d(TAG, "onSuccess:" + strjson);
                try {
                    Result result = JSON.parseObject(strjson, Result.class);
                    Log.d(TAG,"Result:"+result.toString());
                    if(result.getCode()>=0){
                        DetLog.writeLog(TAG,"上传结班记录成功："+strjson);
                        shiftEntity.setStatus(PaymentShiftEntity.SHIFT_STATUS_OFF_UPLOADED);
                    }else{
                        DetLog.writeLog(TAG,"上传结班记录失败："+strjson);
                        shiftEntity.setStatus(PaymentShiftEntity.SHIFT_STATUS_OFF);
                    }
                    DBManager.getInstance().getPaymentShiftEntityDao().save(shiftEntity);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return 0;
    }
}
