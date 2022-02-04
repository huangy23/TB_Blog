package com.treebee.blog.entity;

public class CategoryStat {
    private String name;
    private int value;

    public String getKey() {
        return name;
    }

    public void setKey(String key) {
        this.name = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
