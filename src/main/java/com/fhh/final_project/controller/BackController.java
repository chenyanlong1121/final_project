package com.fhh.final_project.controller;

import com.fhh.final_project.entity.User;
import com.fhh.final_project.service.LeaveService;
import com.fhh.final_project.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping(value="/",produces = "text/plain;charset=utf-8")
public class BackController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private LeaveService leaveService;
    @RequestMapping(value="updatepassword",method = RequestMethod.POST)
    public String updatePassword(HttpServletRequest request)  {
        System.out.println(request.getParameter("userno"));
        System.out.println(request.getParameter("origpass"));
        System.out.println(request.getParameter("newpass"));
        int result = loginService.updatePassword(
                        request.getParameter("userno"),
                        request.getParameter("origpass"),
                        request.getParameter("newpass"));
        if(result==1)
            return "OK";
        else
            //由于用户ID必定正确，因此肯定是原密码出了问题。
            return "ERROR";
    }

    @RequestMapping(value="insertleave",method = RequestMethod.POST)
    public String insertLeave(HttpServletRequest request) {
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
        String leaveReason = request.getParameter("leavereason");
        int leavetype=Integer.parseInt(request.getParameter("leavetype"));
        //拼接时间对象
        Calendar calendar = Calendar.getInstance();
        calendar.set(beginyear,beginmonth-1,begindate,beginhour,beginminute);
        Date begintime = calendar.getTime();
        calendar.set(endyear,endmonth-1,enddate,endhour,endminute);
        Date endtime = calendar.getTime();
        int result = leaveService.insertLeave(stuno, begintime,endtime,leaveReason,leavetype);
        return Integer.toString(result);
    }
    @RequestMapping(value = "triggerdelete",method=RequestMethod.POST)
    public String triggerdelete(HttpServletRequest request) {
        return Integer.toString(leaveService.triggerDeleteMarkerByUuid(request.getParameter("leaveuuid")));
    }
    @RequestMapping(value = "triggerarchive",method=RequestMethod.POST)
    public String triggerarchive(HttpServletRequest request) {
        return Integer.toString(leaveService.triggerArchiveMarkerByUuid(request.getParameter("leaveuuid")));
    }
    @RequestMapping(value = "setresult",method = RequestMethod.POST)
    public String setResult(HttpServletRequest request) {
        /*
         * 这里复用了辅导员与教务处设置审定结果的代码。
         * 其中，需要执行的任务有：
         * 1.设置访问记录(无论同意/拒绝，标志位2/4).
         * 2.如果结果为“拒绝”，则直接将拒绝标志位置1(标志位8).
         */
        int privilege = Integer.parseInt(request.getParameter("privilege"));
        String leaveuuid = request.getParameter("leaveuuid");
        int result = Integer.parseInt(request.getParameter("result"));
        //拼接运算算子。
        //神奇的位运算大法好#(滑稽)
        int cal=(result^1)<<3|privilege;
        return Integer.toString(leaveService.setResult(leaveuuid,cal));
    }
    @RequestMapping(value = "triggerdelete_perm",method = RequestMethod.POST)
    public String triggerDeletePerm(HttpServletRequest request){
        String leaveuuid = request.getParameter("leaveuuid");
        return Integer.toString(leaveService.deleteLeavePerm(leaveuuid));
    }
    @RequestMapping(value = "cleardelete",method = RequestMethod.POST)
    public String clearDelete(HttpServletRequest request){
        String userno = ((User)request.getSession().getAttribute("user")).getUserno();
        return Integer.toString(leaveService.clearDelete(userno));
    }
}
