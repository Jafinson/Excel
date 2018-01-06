package com.jafin.excel.bean;

import com.jafin.excel.annotation.AColumn;

/**
 * Created by 何锦发 on 2018/1/5.
 */

public class Student {
    @AColumn(name = "年龄")
    private int age;
    @AColumn(name = "姓名")
    private String name;
    @AColumn(name = "地址")
    private String address;
    @AColumn(name = "分数")
    private double score;
    @AColumn(name = "学校")
    private String school;
    @AColumn(name = "可视")
    private boolean show;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
