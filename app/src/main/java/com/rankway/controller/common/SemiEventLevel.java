package com.rankway.controller.common;

public class SemiEventLevel {
    private int level;

    public SemiEventLevel(){
        level = 0;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "SemiEventLevel{" +
                "level=" + level +
                '}';
    }
}
