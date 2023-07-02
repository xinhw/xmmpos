package com.rankway.controller.common;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.rankway.controller.activity.project.manager.SpManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/***
 * 赛米 黑白名单管理
 */
public class WhiteBlackListMode {

    public static final int TYPE_WHITE_LIST = 0;    //  白名单
    public static final int TYPE_BLACK_LIST = 1;    //  黑名单

    private final String KEY_WHITE_LIST = "WHITELIST";
    private final String KEY_BLACK_LIST ="BLACKLIST";

    private final String TAG="WhiteBlackListMode";

    private WhiteBlackListMode(){}

    public static WhiteBlackListMode getInstance(){
        WhiteBlackListMode sIntance = SingletonHoler.sIntance;
        return sIntance;
    }

    private static class SingletonHoler{
        private static final WhiteBlackListMode sIntance = new WhiteBlackListMode();
    }


    /***
     *
     * @param ntype
     * @param strlist
     */
    public void storeWhiteBlackList(int ntype, String strlist){
        if(TYPE_WHITE_LIST==ntype){
            Log.d(TAG,"白名单:"+strlist);
            List<SemiWhiteBlackList> list =  JSON.parseArray(strlist, SemiWhiteBlackList.class);
            if(null==list){
                return;
            }
            if(list.size()==0){
                return;
            }

            SpManager.getIntance().saveSpString(KEY_WHITE_LIST,strlist);
        }else{
            Log.d(TAG,"黑名单:"+strlist);
            SpManager.getIntance().saveSpString(KEY_BLACK_LIST,strlist);
        }
        return;
    }

    /***
     * 判断是否在黑名单内
     * @return
     */
    public boolean isInBlackList(StringBuilder blinfo){
        String str = SpManager.getIntance().getSpString(KEY_BLACK_LIST);
        Log.d(TAG,"缓存黑名单："+str);
        if(TextUtils.isEmpty(str)){
            return false;
        }

        List<SemiWhiteBlackList> list =  JSON.parseArray(str, SemiWhiteBlackList.class);
        if(null==list){
            return false;
        }
        if(list.size()==0){
            return false;
        }

        long ltm = new Date().getTime();
        for(SemiWhiteBlackList bl:list){
            if((ltm>bl.getDetonateTimeStart())&&(ltm<bl.getDetonateTimeEnd())){
                Log.d(TAG,"符合黑名单："+bl.toString());
                Log.d(TAG,String.format("黑名单：%d  in [%d,%d]!",ltm,bl.getDetonateTimeStart(),bl.getDetonateTimeEnd()));

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
                String msg = String.format("%s—%s 间不能使用!",
                        sdf.format(bl.getDetonateTimeStart()),
                        sdf.format(bl.getDetonateTimeEnd()));
                blinfo.setLength(0);
                blinfo.append(msg);
                return  true;
            }
        }

        SpManager.getIntance().saveSpString(KEY_BLACK_LIST,"");
        return false;
    }

    /**
     * 判断是否在白名单内
     * @return
     */
    public boolean isInWhiteList(){
        String str = SpManager.getIntance().getSpString(KEY_WHITE_LIST);
        Log.d(TAG,"缓存白名单："+str);
        if(TextUtils.isEmpty(str)){
            return false;
        }

        List<SemiWhiteBlackList> list =  JSON.parseArray(str, SemiWhiteBlackList.class);
        if(null==list){
            return false;
        }
        if(list.size()==0){
            return false;
        }

        long ltm = new Date().getTime();
        for(SemiWhiteBlackList bl:list){
            if((ltm>bl.getDetonateTimeStart())&&(ltm<bl.getDetonateTimeEnd())){
                Log.d(TAG,"符合白名单："+bl.toString());
                int n = bl.getAllowDetNum();
                if(n>0){
                    return true;
                }
            }
        }

        SpManager.getIntance().saveSpString(KEY_WHITE_LIST,"");
        return false;
    }

    /***
     * 刷新白名单
     */
    public void refreshWhiteList(){
        Log.d(TAG,"刷新白名单：");

        String str = SpManager.getIntance().getSpString(KEY_WHITE_LIST);

        Log.d(TAG,"刷新前："+str);
        if(TextUtils.isEmpty(str)){
            return;
        }

        List<SemiWhiteBlackList> list =  JSON.parseArray(str, SemiWhiteBlackList.class);
        if(null==list){
            return;
        }
        if(list.size()==0){
            return;
        }

        long ltm = new Date().getTime();
        for(SemiWhiteBlackList bl:list){
            if((ltm>bl.getDetonateTimeStart())&&(ltm<bl.getDetonateTimeEnd())){

                int n = bl.getAllowDetNum();
                bl.setAllowDetNum(n-1);
            }
        }

        str= JSON.toJSONString(list);
        SpManager.getIntance().saveSpString(KEY_WHITE_LIST,str);

        Log.d(TAG,"刷新后："+str);
        return;
    }
}
