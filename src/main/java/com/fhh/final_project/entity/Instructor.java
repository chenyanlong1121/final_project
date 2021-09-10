package com.fhh.final_project.entity;

public class Instructor extends User {
    //添加了“入职年份”，“年级”,"联系方式(tel)"。
    private String insInyear,insGrade,insTel;
    public Instructor(String userno, String username, String sex, String pro, int age,String insInyear,String insGrade,String insTel) {
        super(userno, username, "instructor", null, sex, pro, age);
        this.insGrade = insGrade;
        this.insInyear = insInyear;
        this.insTel = insTel;
    }

    public String getInsInyear() {
        return insInyear;
    }

    public String getInsGrade() {
        return insGrade;
    }

    public String getInsTel() {
        return insTel;
    }
}
