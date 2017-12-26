package com.jafin.excel.test;

import com.jafin.excel.annotation.AColumn;

/**
 * Created by 何锦发 on 2017/5/26.
 */
public class Student {
    @AColumn(name = "名字")
    private String name;
    @AColumn(name = "编码")
    private String code;
    @AColumn(name = "年龄")
    private int age;
    @AColumn(name = "分数")
    private float score;
    @AColumn(name = "学校")
    private String school;
    @AColumn(name = "地址")
    private String address;
    @AColumn(name = "电话")
    private String phone;
    @AColumn(name = "生日")
    private String birthday;
    @AColumn(name = "数量")
    private double qty;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
