package com.fhh.final_project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fhh.final_project.annotation.Android;
import com.fhh.final_project.entity.Instructor;
import com.fhh.final_project.entity.StuLeave;
import com.fhh.final_project.entity.Student;
import com.fhh.final_project.entity.User;
import com.fhh.final_project.mapper.LeaveMapper;
import com.fhh.final_project.service.LeaveService;
import com.fhh.final_project.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/android",produces = "text/plain;charset=utf-8")
public class AndroidController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private LeaveMapper leaveMapper;
    @Autowired
    private LeaveService leaveService;
    @Android
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String checkLogin(HttpServletRequest request) throws IOException {
        Map<?,?> data = (Map<?,?>) JSON.parse(request.getReader().readLine());
        User result = loginService.checkLogin(data.get("userno").toString(),data.get("password").toString());
        if(result!=null){
            switch (result.getPrivilege().charAt(0)){
                case 's':{result.setPrivilege("同学");break;}
                //辅导员喜欢被别人称为老师，make them happy.
                case 'i':{result.setPrivilege("老师");break;}
                default:{result=null;}
            }
        }
        request.getSession().setAttribute("user",result);
        return JSON.toJSONString(result);
    }
    @RequestMapping(value = "getnormalleave",method = RequestMethod.POST)
    public String getNormalLeave(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<StuLeave> leaves = new ArrayList<>();
        if(user instanceof Student) {
            //安卓：分页？？？
            for (int i = 0; i <= (leaveService.getNormalLeaveCountByStuNo(user.getUserno())/20);i++){
                List<StuLeave> temp = leaveService.getNormalLeaveTitleByStuNo(user.getUserno(),i*20);
                leaves.addAll(temp);
            }
        }
        if(user instanceof Instructor) {
            for (int i = 0; i <= (leaveService.getNormalLeaveCountByInstructorNo(user.getUserno())/20);i++){
                List<StuLeave> temp = leaveService.getNormalLeaveTitleByInstructorNo(user.getUserno(),i*20);
                leaves.addAll(temp);
            }
        }
        return JSON.toJSONString(leaves, SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteMapNullValue);
    }
    @RequestMapping(value = "getarchiveleave",method = RequestMethod.POST)
    public String getArchiveLeave(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<StuLeave> leaves = new ArrayList<>();
        if(user instanceof Student) {
            //安卓：分页？？？
            for (int i = 0; i <= (leaveService.getArchiveLeaveCountByStuNo(user.getUserno())/20);i++){
                List<StuLeave> temp = leaveService.getArchiveLeaveTitleByStuNo(user.getUserno(),i*20);
                leaves.addAll(temp);
            }
        }
        if(user instanceof Instructor) {
            for (int i = 0; i <= (leaveService.getArchiveLeaveCountByInstructorNo(user.getUserno())/20);i++){
                List<StuLeave> temp = leaveService.getArchiveLeaveTitleByInstructorNo(user.getUserno(),i*20);
                leaves.addAll(temp);
            }
        }
        return JSON.toJSONString(leaves, SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteMapNullValue);
    }
    @RequestMapping(value = "getwaitingleave",method = RequestMethod.POST)
    public String getUnreadLeave(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<StuLeave> leaves = new ArrayList<>();
        for (int i = 0; i <= (leaveService.getWaitingCountByInstructorNo(user.getUserno())/20);i++){
            List<StuLeave> temp = leaveService.getWaitingLeaveTitleByInstructorNo(user.getUserno(),i*20);
            leaves.addAll(temp);
        }
        return JSON.toJSONString(leaves, SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteMapNullValue);
    }
    @RequestMapping(value = "setresult",method = RequestMethod.POST)
    public String setResult(HttpServletRequest request) throws Exception{
        JSONObject jsonObject = JSON.parseObject(request.getReader().readLine());
        String uuid=jsonObject.get("uuid").toString();
        int cal = Boolean.parseBoolean(jsonObject.get("result").toString())?2:10;
        return Integer.toString(leaveService.setResult(uuid,cal));
    }
    @RequestMapping(value = "newleave",method = RequestMethod.POST)
    //"uuid","startdate","enddate","leavereason"
    public String newLeave(HttpServletRequest request) throws Exception{
        User user = (User) request.getSession().getAttribute("user");
        JSONObject jsonObject = JSON.parseObject(request.getReader().readLine());
        String uuid=jsonObject.get("uuid").toString();
        String startdate=jsonObject.get("startdate").toString();
        String enddate=jsonObject.get("enddate").toString();
        String leavereason=jsonObject.get("leavereason").toString();
        /*
        *   @Param("uuid") String uuid,
                @Param("leavestuno") String leavestuno,
                @Param("leavebegintime") String leavebegintime,
                @Param("leaveendtime") String leaveendtime,
                @Param("leavestate") byte leavestate,
                @Param("leavereason") String leavereason,
                @Param("leavetype") int leavetype
        * */
        return Integer.toString(leaveMapper.addLeave(uuid,user.getUserno(),
                                                         startdate,enddate,
                (byte) 0,leavereason,0));
    }
}
