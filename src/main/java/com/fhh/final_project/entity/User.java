package com.fhh.final_project.entity;

/*
    Copyright (c) Igniculus Fu,2019,All Rights Reserved.
    抽象用户类型User。
    所有登录系统的人员均基于此类。
    向下细分为学生（Student），辅导员（Instructor）,教师（Teacher）。
 */
public class User {
    private String username=null,userno, privilege,userlastlogin,sex=null,pro=null;
    int age=0;
    //性别，专业，年龄
    public User(String userno, String privilege, String userlastlogin) {
        this.userno = userno;
        this.privilege = privilege;
        this.userlastlogin = userlastlogin;
        System.out.println("pre construction:"+userlastlogin);
    }
    public User(String userno, String username, String privilege, String userlastlogin, String sex, String pro, int age) {
        this.sex = sex;
        this.pro = pro;
        this.userno = userno;
        this.username = username;
        this.privilege = privilege;
        this.age = age;
        System.out.println("post construction:"+userlastlogin);
    }
    public String getUsername() {
        return username;
    }
    public String getUserno() {
        return userno;
    }
    public String getPrivilege(){return privilege;}
    public String getUserlastlogin() {
        return userlastlogin;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getSex() {
        return sex;
    }

    public String getPro() {
        return pro;
    }
    public void setUserLastLogin(String userlastlogin){
        this.userlastlogin = userlastlogin;
    }
    public int getAge() {
        return age;
    }
}
