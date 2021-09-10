package com.fhh.final_project.service;

import com.fhh.final_project.entity.Administration;
import com.fhh.final_project.entity.User;
import com.fhh.final_project.mapper.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private LoginMapper loginMapper;
    public User checkLogin(String userno,String userpassword) {
        User user = loginMapper.checkLogin(userno, userpassword);
        if(user!=null){
            //BUG：最后登录时间错误.
            String lastLoginTime = user.getUserlastlogin();
            //根据查询结果，调用不同的表查询，并添加不同的操作到主界面。
            //类型：学生（Student），辅导员（Instructor）,教师（Teacher），教务（Administration）。
            switch (user.getPrivilege().charAt(0)){
                case 's':{
                    user = loginMapper.getStudent(userno);
                    break;
                }
                case 'i':{
                    user = loginMapper.getInstructor(userno);
                    break;
                }
                case 't':{
                    user = loginMapper.getTeacher(userno);
                    break;
                }
                case 'a':{
                    //教务没有详细信息。
                    user = new Administration(user.getUserno(),user.getPrivilege(), user.getUserlastlogin());
                    break;
                }
            }
            user.setUserLastLogin(lastLoginTime);
            //刷新登录时间。
            loginMapper.flushLoginTime(userno);
        }
        return user;
    }
    public int updatePassword(String userno,String origpass,String newpass){
        return loginMapper.updatePassword(userno, origpass, newpass);
    }
}
