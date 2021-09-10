package com.fhh.final_project.entity;
/*
 * Copyright (c)  Igniculus Fu,2019,All Rights Reserved.
 * 数据类型Student，继承自User。
 *
 * 添加了“班级”，“年级”，“对应辅导员”,"联系方式(tel)"。
 */
public class Student extends User {
    private String stuClass,stuGrade,stuInstructor,stuTel;
    public Student(String userno, String username,String sex,String pro,int age, String userlastlogin,String stuClass,String stuGrade,String stuInstructor,String stuTel) {
        super(userno, username,"student", userlastlogin,sex,pro,age);
        this.stuClass = stuClass;
        this.stuGrade = stuGrade;
        this.stuInstructor = stuInstructor;
        this.stuTel = stuTel;
    }

    public String getStuClass() {
        return stuClass;
    }

    public String getStuGrade() {
        return stuGrade;
    }

    public String getStuInstructor() {
        return stuInstructor;
    }

    public String getStuTel() {
        return stuTel;

    }
}
