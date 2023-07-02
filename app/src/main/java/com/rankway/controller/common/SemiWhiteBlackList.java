package com.rankway.controller.common;

/***
 * 赛米平台黑白名单应答
 */
public class SemiWhiteBlackList {

    //"id": 3,
    //"sn": "F60A8112233",
    //"note": "辛宏伟测试iData",
    //"createByName": "ADMIN管理员",
    //"type": 2,
    //"status": 1,
    //"allowDetNum": 12,
    //"proposer": "15609171667",
    //"reqTime": 1619575625363,
    //"detonateTimeStart": 1619366400000,
    //"detonateTimeEnd": 1619798400000,
    //"createTime": 1619575621726,
    //"createBy": 2,
    //"level": 1,
    //"lastUpdateTime": null,
    //"lastUpdateBy": null,
    //"removeTime": null
    private int id;
    private String sn;
    private int allowDetNum;
    private long detonateTimeStart;
    private long detonateTimeEnd;
    private int level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getAllowDetNum() {
        return allowDetNum;
    }

    public void setAllowDetNum(int allowDetNum) {
        this.allowDetNum = allowDetNum;
    }

    public long getDetonateTimeStart() {
        return detonateTimeStart;
    }

    public void setDetonateTimeStart(long detonateTimeStart) {
        this.detonateTimeStart = detonateTimeStart;
    }

    public long getDetonateTimeEnd() {
        return detonateTimeEnd;
    }

    public void setDetonateTimeEnd(long detonateTimeEnd) {
        this.detonateTimeEnd = detonateTimeEnd;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "semiWhiteBlackList{" +
                "id=" + id +
                ", sn='" + sn + '\'' +
                ", allowDetNum=" + allowDetNum +
                ", detonateTimeStart=" + detonateTimeStart +
                ", detonateTimeEnd=" + detonateTimeEnd +
                ", level=" + level +
                '}';
    }
}
