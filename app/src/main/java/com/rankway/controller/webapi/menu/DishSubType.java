package com.rankway.controller.webapi.menu;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class DishSubType {
    String dishSubTypeCode;
    String dishSubTypeName;
    int status;
    List<Dish> dishs;

    public String getDishSubTypeCode() {
        return dishSubTypeCode;
    }

    public void setDishSubTypeCode(String dishSubTypeCode) {
        this.dishSubTypeCode = dishSubTypeCode;
    }

    public String getDishSubTypeName() {
        return dishSubTypeName;
    }

    public void setDishSubTypeName(String dishSubTypeName) {
        this.dishSubTypeName = dishSubTypeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Dish> getDishs() {
        return dishs;
    }

    public void setDishs(List<Dish> dishs) {
        this.dishs = dishs;
    }

    @Override
    public String toString() {
        return "DishSubType{" +
                "dishSubTypeCode='" + dishSubTypeCode + '\'' +
                ", dishSubTypeName='" + dishSubTypeName + '\'' +
                ", status=" + status +
                ", dishs=" + dishs +
                '}';
    }
}
