package com.rankway.controller.webapi.menu;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/06
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class DishType {
    String dishTypeCode;
    String dishTypeName;
    int status;
    List<DishSubType> dishSubTypes;

    public String getDishTypeCode() {
        return dishTypeCode;
    }

    public void setDishTypeCode(String dishTypeCode) {
        this.dishTypeCode = dishTypeCode;
    }

    public String getDishTypeName() {
        return dishTypeName;
    }

    public void setDishTypeName(String dishTypeName) {
        this.dishTypeName = dishTypeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DishSubType> getDishSubTypes() {
        return dishSubTypes;
    }

    public void setDishSubTypes(List<DishSubType> dishSubTypes) {
        this.dishSubTypes = dishSubTypes;
    }

    @Override
    public String toString() {
        return "DishType{" +
                "dishTypeCode='" + dishTypeCode + '\'' +
                ", dishTypeName='" + dishTypeName + '\'' +
                ", status=" + status +
                ", dishSubTypes=" + dishSubTypes +
                '}';
    }
}
