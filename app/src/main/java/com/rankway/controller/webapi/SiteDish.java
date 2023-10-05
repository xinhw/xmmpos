package com.rankway.controller.webapi;

import com.rankway.controller.persistence.entity.DishTypeEntity;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class SiteDish {
    //"siteId": "10000001",
    //"siteDesc": "文新后台管理",
    //"siteVersion": "10000001_20230824212611",
    //"dishTypes":
    String siteId;
    String siteDesc;
    String siteVersion;
    List<DishTypeEntity> dishTypes;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteDesc() {
        return siteDesc;
    }

    public void setSiteDesc(String siteDesc) {
        this.siteDesc = siteDesc;
    }

    public String getSiteVersion() {
        return siteVersion;
    }

    public void setSiteVersion(String siteVersion) {
        this.siteVersion = siteVersion;
    }

    public List<DishTypeEntity> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<DishTypeEntity> dishTypeEntities) {
        this.dishTypes = dishTypeEntities;
    }

    @Override
    public String toString() {
        return "SiteDish{" +
                "siteId='" + siteId + '\'' +
                ", siteDesc='" + siteDesc + '\'' +
                ", siteVersion='" + siteVersion + '\'' +
                ", dishTypes=" + dishTypes +
                '}';
    }
}
