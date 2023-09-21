package com.rankway.controller.pushmessage;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.rankway.controller.activity.BaseActivity;
import com.rankway.controller.activity.project.comment.AppSpSaveConstant;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.common.SemiEventLevel;
import com.rankway.controller.common.SemiParamSetting;
import com.rankway.controller.common.WhiteBlackListMode;
import com.rankway.controller.hardware.util.DetLog;
import com.rankway.controller.persistence.DBManager;
import com.rankway.controller.persistence.entity.MessageDetail;

import java.util.List;

public class ETEKMessageProcess {
    private final String TAG="ETEKMessageProcess";
    private BaseActivity activity;
    public ETEKMessageProcess(BaseActivity activity){
        this.activity = activity;
    }

    public int Process(String title,String strMessage){
        int ret = 0;

        Log.d(TAG,"移动推送:"+strMessage);
        PushMessageRequest req = JSON.parseObject(strMessage,PushMessageRequest.class);
        if(null==req){
            DetLog.writeLog(TAG,"无法处理的消息："+strMessage);
            return 0;
        }

        PushMessageResponse resp = new PushMessageResponse(req);
        switch (req.getMessageType()){
            case 10000:     //  4.3.1	通知
                //  缓存信息
                MessageDetail md = new MessageDetail(req);
                storeNotification(md);

                activity.showNotification(title,req.getContent(),req.getFrom());
                resp.setResponse("收到");

                ret = 1;
                break;

            case 10001:     //  4.3.2	白名单
                WhiteBlackListMode.getInstance().storeWhiteBlackList(WhiteBlackListMode.TYPE_WHITE_LIST,req.getContent());
                resp.setResponse("成功");

                MessageDetail md0 = new MessageDetail();
                md0.setTitle("模式设置");
                md0.setContent("设备模式设置成功-W");
                md0.setFrom("运营平台");

                storeNotification(md0);
                activity.showNotification(md0.getTitle(),md0.getContent(),md0.getFrom());

                break;

            case 10002:     //  4.3.3	黑名单
                WhiteBlackListMode.getInstance().storeWhiteBlackList(WhiteBlackListMode.TYPE_BLACK_LIST,req.getContent());
                resp.setResponse("成功");

                MessageDetail md1 = new MessageDetail();
                md1.setTitle("模式设置");
                md1.setContent("设备模式设置成功-B");
                md1.setFrom("运营平台");

                storeNotification(md1);
                activity.showNotification(md1.getTitle(),md1.getContent(),md1.getFrom());
                break;

            case 10003:     //  4.3.4	上报状态
                String str ="";
                activity.resetUploadNum();
                activity.UploadHandsetInfo(str);
                resp.setResponse("成功");
                break;

            case 10004:      //  4.3.5	上传日志
                Log.d(TAG,"上传日志");
                activity.uploadLog();
                resp.setResponse("收到命令，开始上传");
                break;

            case 10005:     //  4.3.6	修改编号
            case 10008:     //  4.3.9	控制器命令
                List<PushMessageRequest> cmdlist = activity.getDataList(AppSpSaveConstant.PUSH_MESSAGE_CACHE_LIST,
                        PushMessageRequest.class);
                cmdlist.add(req);
                activity.setDataList(AppSpSaveConstant.PUSH_MESSAGE_CACHE_LIST,
                        cmdlist);
                resp.setResponse("缓存");
                break;

            case 10006:     //  4.3.7	设置软件升级地址
                UpdateAppURL obj = JSON.parseObject(req.getContent(),UpdateAppURL.class);
                if(null==obj){
                    String str1 = "设置软件升级地址无效："+req.getContent();
                    DetLog.writeLog(TAG,str1);
                    resp.setResponse(str1);
                    break;
                }

                MessageDetail md2 = new MessageDetail();
                md2.setFrom("运营平台");

                switch (obj.getDeviceType()){
                    case 0:
                        md2.setTitle("APP更新");
                        md2.setContent("收到APP更新消息，请到【系统设置】——【关于】，点击【检查更新】下载并升级！");
                        break;
                }
                resp.setResponse("设置成功！");

                storeNotification(md2);
                activity.showNotification(md2.getTitle(),md2.getContent(),md2.getFrom());

                break;

            case 10007:     //  4.3.8	上传指定Android文件
                FilePath fpobj = JSON.parseObject(req.getContent(),FilePath.class);
                if(null==fpobj){
                    String str1 = "上传指定Android文件无效："+req.getContent();
                    DetLog.writeLog(TAG,str1);
                    resp.setResponse(str1);
                }else{
                    activity.updateFile(fpobj.getPath());
                    resp.setResponse("收到命令，开始上传！");
                }
                break;

            case 10009:     //  4.3.10	设置上传事件级别
                Log.d(TAG,"设置事件级别："+req.getContent());
                SemiEventLevel level = JSON.parseObject(req.getContent(),SemiEventLevel.class);
                if(null==level){
                    Log.d(TAG,"无效的信息");
                    break;
                }

                //  保存事件级别
                SpManager.getIntance().saveSpInt(AppSpSaveConstant.UPLOAD_EVENT_LEVEL,level.getLevel());
                resp.setResponse(String.format("Yes,sir，上传事件级别已经设置为：%d",level.getLevel()));

                break;

            case 10010:     //  4.3.11	设置本地参数
                Log.d(TAG,"设置本地参数："+req.getContent());
                List<SemiParamSetting> lstParam = null;
                try {
                    lstParam = JSON.parseArray(req.getContent(), SemiParamSetting.class);
                    if(lstParam.size()==0) {
                        resp.setResponse("没有参数需要设置！");
                        break;
                    }

                    for(SemiParamSetting p: lstParam){
                        DetLog.writeLog(TAG,String.format("设置参数%s为%s",p.getName(),p.getValue()));
                        //  保存事件级别
                        switch (p.getType()){
                            case 1:     //  整型
                                int nval = Integer.parseInt(p.getValue());
                                SpManager.getIntance().saveSpInt(p.getName(),nval);
                                break;
                            default:     //  字符串
                                SpManager.getIntance().saveSpString(p.getName(),p.getValue());
                                break;
                        }
                    }

                    resp.setResponse("参数都已保存！");
                }catch (Exception e){
                    e.printStackTrace();
                    resp.setResponse(e.getMessage());
                }
                break;
            default:
                resp.setResponse("未知信息类型："+req.getMessageType());
                break;
        }

        String strjson = JSON.toJSONString(resp);

        Log.d(TAG,"推送信息应答："+strjson);
        activity.httpPostPushMessageResponse(strjson);
        return ret;
    }

    private void storeNotification(MessageDetail md){
        Log.d(TAG,"storeNotification "+md.toString());
        DBManager.getInstance().getMessageDetailDao().save(md);
    }

    public static class UpdateAppURL{
        private int deviceType;
        private String url;

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "UpdateAppURL{" +
                    "deviceType=" + deviceType +
                    ", url='" + url + '\'' +
                    '}';
        }

        public UpdateAppURL(){

        }
    }

    public static class FilePath{
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return "FilePath{" +
                    "path='" + path + '\'' +
                    '}';
        }

        public FilePath(){

        }
    }

    public static class HandsetChangeSNO{
        private String fromSNO;
        private String toSNO;

        public String getFromSNO() {
            return fromSNO;
        }

        public void setFromSNO(String fromSNO) {
            this.fromSNO = fromSNO;
        }

        public String getToSNO() {
            return toSNO;
        }

        public void setToSNO(String toSNO) {
            this.toSNO = toSNO;
        }

        @Override
        public String toString() {
            return "HandsetChangeSNO{" +
                    "fromSNO='" + fromSNO + '\'' +
                    ", toSNO='" + toSNO + '\'' +
                    '}';
        }

        public HandsetChangeSNO(){

        }
    }

    public static class HandsetCmd{
        private String cmd;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public String toString() {
            return "HandsetCmd{" +
                    "cmd='" + cmd + '\'' +
                    '}';
        }

        public HandsetCmd(){

        }
    }

    public static class HandsetResponse{
        private String resp;

        public String getResp() {
            return resp;
        }

        public void setResp(String resp) {
            this.resp = resp;
        }

        @Override
        public String toString() {
            return "HandsetResponse{" +
                    "resp='" + resp + '\'' +
                    '}';
        }

        public HandsetResponse(){

        }
    }
}
