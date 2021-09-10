package com.fhh.final_project.entity;

public class Course {
    private String no,name,teacherno,classroom,term;
    private int start,length,weekday;
    public Course(String no, String name, String teacherno, String classroom, int start, int length, int weekday,String term) {
        super();
        this.no = no;
        this.name = name;
        this.teacherno = teacherno;
        this.classroom = classroom;
        this.start = start;
        this.length = length;
        this.weekday = weekday;
        this.term = term;
    }
    public String getNo() {
        return no;
    }
    public String getName() {
        return name;
    }
    public String getTeacherno() {
        return teacherno;
    }
    public String getClassroom() {
        return classroom;
    }
    public int getStart() {
        return start;
    }
    public int getLength() {
        return length;
    }
    public int getWeekday() {
        return weekday;
    }
    public String getTerm() {
        return term;
    }
    @Override
    public String toString() {
        return String.format("%s|%s|%s@%s,%d~%d,星期%d", no,name,teacherno,classroom,start,start+length,weekday);
    }
}



