package com.fhh.final_project.service;

import com.fhh.final_project.entity.Course;
import com.fhh.final_project.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {
    @Autowired
    private CourseMapper courseMapper;
    private static Timetable[] timetables= {
            new Timetable(8,30),
            new Timetable(9,20),
            new Timetable(10,10),
            new Timetable(11,0),
            new Timetable(11,50),
            new Timetable(13,30),
            new Timetable(14,20),
            new Timetable(15,10),
            new Timetable(16, 0),
            new Timetable(18,20),
            new Timetable(19,10),
            new Timetable(20, 0),
            new Timetable(20,45)//最后一节下课时间
    };
    public Course[] getCourses(String stuno, Date begin, Date end) {
        //通过计算，确定是不是本学期的课程。
        String begintermstr,endtermstr;
        Calendar a = Calendar.getInstance();
        a.set(Calendar. DAY_OF_WEEK, Calendar.MONDAY);
        a.setTime(end);
        endtermstr = (a.get(Calendar.MONTH)<6?
                            String.format("(%d-%d-2%%",a.get(Calendar.YEAR)-1,a.get(Calendar.YEAR)):
                            String.format("(%d-%d-1%%",a.get(Calendar.YEAR),a.get(Calendar.YEAR)+1));
        int length = a.get(Calendar.DAY_OF_YEAR);
        a.setTime(begin);
        length = length - a.get(Calendar.DAY_OF_YEAR);
        begintermstr = (a.get(Calendar.MONTH)<6?
                String.format("(%d-%d-2%%",a.get(Calendar.YEAR)-1,a.get(Calendar.YEAR)):
                String.format("(%d-%d-1%%",a.get(Calendar.YEAR),a.get(Calendar.YEAR)+1));
        List<Course> source = courseMapper.getCourses(stuno,begintermstr,endtermstr);
        Set<Course> courses = new HashSet<>();
        //将传入的时间转换为课程节数。
        int[] begintime = getCourseCountByDate(begin);
        int[] endtime = getCourseCountByDate(end);

        if(length<0)
            length += 365;
        //begintime,length
        //如果为同一天，则直接判断。
        if(length==0) {
            for(Course i:source)
                if(i.getWeekday()==begintime[0]&&i.getStart()>=begintime[1]&&i.getStart()<=endtime[1])
                    courses.add(i);
        }
        //如果不是同一天，则按照顺序查询。
        else {
            //第一天
            for(Course i:source)
                if(i.getWeekday()==begintime[0]&&i.getStart()>=begintime[1])
                    courses.add(i);
            //除了最后一天的其他天
            for(int i=1;i<length;i++) {
                for(Course j:source)
                    if(j.getWeekday()==(i+begintime[0])%7)
                        courses.add(j);
            }
            //最后一天
            for(Course i:source)
                if(i.getWeekday()==endtime[0]&&i.getStart()<=endtime[1])
                    courses.add(i);
        }
        Course[] result = new Course[courses.size()];
        System.arraycopy(courses.toArray(),0,result,0,courses.size());
        return result;
    }
    //辅助操作方法：返回不大于给定时间的课程节数。
    //返回格式：{星期，节数}
    public int[] getCourseCountByDate(Date in) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar. DAY_OF_WEEK, Calendar.MONDAY);
        a.setTime(in);
        int hour = a.get(Calendar.HOUR_OF_DAY);
        int minute = a.get(Calendar.MINUTE);
        int i=0;
        while(i<13&&timetables[i].hour<hour)
            i++;
        while(i<13&&timetables[i].hour==hour&&timetables[i].minute<minute)
            i++;
        return new int[] {a.get(Calendar.DAY_OF_WEEK)-1,i};
    }
}
class Timetable {
    int hour,minute;
    Timetable(int hour,int minute) {
        this.hour = hour;
        this.minute = minute;
    }
}