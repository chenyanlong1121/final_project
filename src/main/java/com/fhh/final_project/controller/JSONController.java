package com.fhh.final_project.controller;

import com.alibaba.fastjson.JSON;
import com.fhh.final_project.entity.*;
import com.fhh.final_project.service.CourseService;
import com.fhh.final_project.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/",produces = "text/plain;charset=utf-8")
public class JSONController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private LeaveService leaveService;
    @RequestMapping(value="getcourse",method = RequestMethod.POST)
    public String getCourse(HttpServletRequest request) {
        int beginyear = Integer.parseInt(request.getParameter("beginyear"));
        int beginmonth = Integer.parseInt(request.getParameter("beginmonth"));
        int begindate = Integer.parseInt(request.getParameter("begindate"));
        int beginhour = Integer.parseInt(request.getParameter("beginhour"));
        int beginminute = Integer.parseInt(request.getParameter("beginminute"));
        int endyear = Integer.parseInt(request.getParameter("endyear"));
        int endmonth = Integer.parseInt(request.getParameter("endmonth"));
        int enddate = Integer.parseInt(request.getParameter("enddate"));
        int endhour = Integer.parseInt(request.getParameter("endhour"));
        int endminute = Integer.parseInt(request.getParameter("endminute"));
        String stuno = request.getParameter("stuno");
        Calendar calendar = Calendar.getInstance();
        calendar.set(beginyear,beginmonth-1,begindate,beginhour,beginminute);
        Date begintime = calendar.getTime();
        calendar.set(endyear,endmonth-1,enddate,endhour,endminute);
        Date endtime = calendar.getTime();
        Course[] results = courseService.getCourses(stuno,begintime,endtime);
        return JSON.toJSONString(results);
    }
    @RequestMapping(value="searchleave",method = RequestMethod.POST)
    public String searchLeave(HttpServletRequest request){
        String method=request.getParameter("method");
        String value=request.getParameter("value");
        String page=request.getParameter("page");
        User user = (User)request.getSession().getAttribute("user");
        //?????????????????????????????????SQL????????????
        String whereClause="",source="stu_leaves as sl";
        //leaveuuid,stuno,term,begindate,enddate
        switch (method.charAt(0)){
            case 'l':{ //leaveuuid
                    //????????????????????????UUID?????????
                whereClause+="sl.leaveuuid='"+value+"'";
                break;
            }
            case 's':{
                //stuno
                whereClause="sl.leavestuno='"+value+"'";
                    //???????????????????????????????????????
                if(user instanceof Student)
                    whereClause+= " and 1=0";
                break;
            }
            case 't':{ //term
                //?????????????????????????????????????????????
                whereClause="sl.leavebegintime like '"+value.substring(0,4)+"%'";
                break;
            }
            case 'b':{ //begindate
                //???????????????????????????????????????????????????
                whereClause="sl.leavebegintime like '"+value+"%'";
                break;
            }
            case 'e':{ //enddate
                //???????????????????????????????????????????????????
                whereClause="sl.leaveendtime like '"+value+"%'";
                break;
            }

        }
        //????????????????????????
        if(user instanceof Student){
            whereClause += " and sl.leavestuno='"+user.getUserno()+"'";
        }
        //????????????????????????????????????????????????
        if(user instanceof Instructor) {
            source += ",student as s";
            whereClause += " and sl.leavestuno = s.stuno and s.stuinstructor='"+user.getUserno()+"'";
        }
        //????????????????????????????????????????????????
        if(user instanceof Teacher) {
            source += ",courses as c,leaves as l";
            whereClause +=" and sl.leaveuuid= l.leaveuuid and l.courseid = c.courseid and c.courseteacherno = '"+user.getUserno()+"'";
        }
        //?????????????????????????????????

        //???????????????
        List<StuLeave> leaves = leaveService.getLeavesByGenericConditions(source,whereClause,(Integer.parseInt(page)-1)*12);
        String leaveString = JSON.toJSONString(leaves);
        //???????????????
        int leavecount = (leaveService.getLeaveCountByGenericConditions(source,whereClause) - 1) / 12 + 1;
        leaveString = String.format("{\"pagecount\":\"%d\",\"leaves\":%s}",leavecount,leaveString);
        return leaveString;
    }
}
