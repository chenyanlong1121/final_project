package com.fhh.final_project.entity;

public class Leave {
    //UUID，课程ID，日期
    public String uuid,courseid,coursedate,coursename;
    public int readstate;
    public Leave(String coursename,String uuid,String courseid,String begindate,int readstate) {
        this(uuid,courseid,begindate,readstate);
        this.coursename = coursename;
    }
    public Leave(String uuid,String courseid,String begindate,int readstate) {
        this.uuid = uuid;
        this.courseid = courseid;
        this.coursedate = begindate;
        this.readstate = readstate;
    }
    @Override
    public String toString(){
        return String.format("(%s,%s,%s,%d)",uuid,courseid,coursedate,readstate);
    }
}
