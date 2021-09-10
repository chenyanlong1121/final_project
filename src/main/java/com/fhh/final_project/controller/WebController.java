package com.fhh.final_project.controller;

import com.fhh.final_project.entity.*;
import com.fhh.final_project.service.LeaveService;
import com.fhh.final_project.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/")
public class WebController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private LeaveService leaveService;
    @RequestMapping("/")
    public String index()  {
        return "index";
    }
    @RequestMapping(value="check_login",method = RequestMethod.POST)
    public String checkLogin(HttpServletRequest request, Model model) {
        String userno = request.getParameter("userno");
        String userpassword = request.getParameter("userpassword");
        User user = loginService.checkLogin(userno,userpassword);
        if(user!=null){
            request.getSession().setAttribute("user",user);
            //将可操作的功能数组放入Session。
            List<Link> actions = new ArrayList<>();
            if(user instanceof Student){
                actions.add(new Link("提交新请假申请","newleave"));
                actions.add(new Link("显示请假列表","listleave?status=normal"));
                actions.add(new Link("显示删除请假","listleave?status=delete"));
                actions.add(new Link("显示归档请假","listleave?status=archive"));
                actions.add(new Link("搜索请假","search"));
            }
            if(user instanceof Teacher){
                actions.add(new Link("显示未处理请假","listleave?status=waiting"));
                actions.add(new Link("显示请假列表","listleave?status=normal"));
                actions.add(new Link("显示归档请假","listleave?status=archive"));
                actions.add(new Link("搜索请假","search"));
            }
            if(user instanceof Instructor){
                actions.add(new Link("显示未处理请假","listleave?status=waiting"));
                actions.add(new Link("显示请假列表","listleave?status=normal"));
                actions.add(new Link("显示归档请假","listleave?status=archive"));
                actions.add(new Link("搜索请假","search"));

            }
            if(user instanceof Administration){
                actions.add(new Link("显示未处理请假","listleave?status=waiting"));
                actions.add(new Link("显示请假列表","listleave?status=normal"));
                actions.add(new Link("显示归档请假","listleave?status=archive"));
                actions.add(new Link("搜索请假","search"));
            }
            request.getSession().setAttribute("actions",actions);
            return "redirect:main";
        }
        return "redirect:/";
    }
    @RequestMapping(value="main")
    public String showMain(HttpServletRequest request){
        User user = (User)(request.getSession().getAttribute("user"));
        if(!(user instanceof Student)) {
            if(user instanceof Instructor)
                request.getSession().setAttribute("waitingcount", leaveService.getWaitingCountByInstructorNo(user.getUserno()));
            if(user instanceof Administration)
                request.getSession().setAttribute("waitingcount",leaveService.getWaitingLeaveCount());
            if(user instanceof Teacher)
                request.getSession().setAttribute("waitingcount",leaveService.getWaitingLeaveCountByTeacherNo(user.getUserno()));
        }
        return "framework";
    }
    @RequestMapping(value="logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:main";
    }
    @RequestMapping(value = "userinfo")
    public String getInfo() {
        return "stuinfo";
    }
    @RequestMapping(value = "search")
    public String search(){
        return "search";
    }
    @RequestMapping(value = "newleave")
    public String newLeave() {
        return "newleave";
    }
    @RequestMapping(value = "listleave")
    public String listLeave(HttpServletRequest request,Model model){
        //由于直接可以从session中读取No，因此不必再通过Request传入值。
        String page = request.getParameter("page");
        if(page==null)
            page="1";
        //1页20行
        String status=request.getParameter("status");
        if(status==null)
            status = (String)request.getSession().getAttribute("status");
        else
            request.getSession().setAttribute("status",status);
        int totalCount=0;
        if((request.getSession().getAttribute("user")) instanceof Student) {
            if (status.equals("normal")) { //正常状态
                model.addAttribute("leavetitles",
                        leaveService.getNormalLeaveTitleByStuNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getNormalLeaveCountByStuNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if (status.equals("delete")) { //删除状态
                model.addAttribute("leavetitles",
                        leaveService.getDeleteLeaveTitleByStuNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getDeleteLeaveCountByStuNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;

            }
            if (status.equals("archive")) { //归档状态
                model.addAttribute("leavetitles",
                        leaveService.getArchiveLeaveTitleByStuNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getArchiveLeaveCountByStuNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
        }
        if((request.getSession().getAttribute("user")) instanceof Instructor) {
            if (status.equals("normal")) { //正常状态
                model.addAttribute("leavetitles",
                        leaveService.getNormalLeaveTitleByInstructorNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getNormalLeaveCountByInstructorNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if (status.equals("archive")) { //归档状态
                model.addAttribute("leavetitles",
                        leaveService.getArchiveLeaveTitleByInstructorNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getArchiveLeaveCountByInstructorNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if(status.equals("waiting")) { //未读统计
                model.addAttribute("leavetitles",
                        leaveService.getWaitingLeaveTitleByInstructorNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getWaitingCountByInstructorNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
        }
        if((request.getSession().getAttribute("user")) instanceof Administration) {
            if (status.equals("normal")) { //正常状态
                model.addAttribute("leavetitles",
                        leaveService.getNormalLeaveTitle((Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getNormalLeaveCount();
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if (status.equals("archive")) { //归档状态
                model.addAttribute("leavetitles",
                        leaveService.getArchiveLeaveTitle((Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getArchiveLeaveCount();
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if(status.equals("waiting")) { //未读统计
                model.addAttribute("leavetitles",
                        leaveService.getWaitingLeaveTitle((Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getWaitingLeaveCount();
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
        }
        if((request.getSession().getAttribute("user")) instanceof Teacher) {
            if (status.equals("normal")) { //正常状态
                model.addAttribute("leavetitles",
                        leaveService.getNormalLeaveTitleByTeacherNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getNormalLeaveCountByTeacherNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if (status.equals("archive")) { //归档状态
                model.addAttribute("leavetitles",
                        leaveService.getArchiveLeaveTitleByTeacherNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getArchiveLeaveCountByTeacherNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
            if(status.equals("waiting")) { //未读统计
                model.addAttribute("leavetitles",
                        leaveService.getWaitingLeaveTitleByTeacherNo(
                                ((User) (request.getSession().getAttribute("user")))
                                        .getUserno(),
                                (Integer.parseInt(page) - 1) * 20));
                //获取总共的页数。
                totalCount = leaveService.getWaitingLeaveCountByTeacherNo(((User) (request.getSession().getAttribute("user")))
                        .getUserno());
                //计算总共的页数。
                totalCount = (totalCount - 1) / 20 + 1;
            }
        }
        model.addAttribute("delete",status.equals("delete")?"yes":"no");
        model.addAttribute("currentpage",page);
        model.addAttribute("totalpage",totalCount);
        return "listleave";
    }
    @RequestMapping(value = "showleave")
    public String showLeave(HttpServletRequest request,Model model) {
        //将请假信息放进去。
        String leaveuuid = request.getParameter("leaveuuid");
        StuLeave stuleave = leaveService.getFullLeaveByUuid(leaveuuid);
        model.addAttribute("stuleave",stuleave);
        //计算课程
        List<Leave> leavecourses = leaveService.getLeaveCoursesByUuid(leaveuuid);
        model.addAttribute("leavecourses",leavecourses);
        //由于Thymeleaf引擎过于羸弱，这里提前将所有课程的名称设置好
        StringBuilder sb = new StringBuilder();
        for(Leave i:leavecourses) {
            sb.append(i.coursename);
            sb.append("(");
            sb.append(i.coursedate);
            sb.append(")");
            sb.append(",");
        }
        if(sb.length()!=0) {
            sb.delete(sb.length()-1,sb.length()+1);
        }
        model.addAttribute("coursestring",sb.toString());
        User user = (User) request.getSession().getAttribute("user");
        //如果是老师，则将对应位置的课程设置为“已读”。
        if(user instanceof Teacher){
            leaveService.setTeacherReadStateByTeacherNoAndUuid(leaveuuid,user.getUserno());
        }
        //如果是辅导员，则将统一理由请假统计送出。
        if(user instanceof Instructor){
            model.addAttribute("similarcount",leaveService.getSimilarLeaveCountByLeaveTypeAndStuno(stuleave.leavetype,stuleave.leavestuno));
        }
        //检测还有哪些老师依旧没有读取。
        List<String> waitingTeachers = leaveService.getWaitingTeacherNameByUuid(leaveuuid);
        StringBuilder sb2 = new StringBuilder();
        for(String i:waitingTeachers)
            sb2.append(i+",");
        if(sb2.length()!=0) {
            sb2.delete(sb2.length()-1,sb2.length()+1);
        }
        model.addAttribute("unreadteacherstring",sb2.toString());
        return "showleave_stu";
    }

}
