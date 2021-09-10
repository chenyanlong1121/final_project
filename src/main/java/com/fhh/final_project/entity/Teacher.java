package com.fhh.final_project.entity;

public class Teacher extends User {
    //添加了“入职年份”，"联系方式(tel)"。
    private String teaInyear,teaTel;
    //select teano,teaname,teasex,teapro,teaage,teainyear,teatel,now()
    public Teacher(String userno, String username, String userlastlogin, String sex, String pro, int age,String teaInyear,String teaTel) {
        super(userno, username, "teacher", userlastlogin, sex, pro, age);
        this.teaInyear = teaInyear;
        this.teaTel = teaTel;
    }
    public String getTeaInyear() {
        return teaInyear;
    }

    public String getTeaTel() {
        return teaTel;
    }
}
