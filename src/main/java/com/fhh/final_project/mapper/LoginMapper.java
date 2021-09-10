package com.fhh.final_project.mapper;

import com.fhh.final_project.entity.Instructor;
import com.fhh.final_project.entity.Student;
import com.fhh.final_project.entity.Teacher;
import com.fhh.final_project.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginMapper {
    User checkLogin(@Param("userno") String userNo,
                    @Param("userpassword") String userPassword
    );
    Student getStudent(@Param("userno") String userNo);
    Instructor getInstructor(@Param("userno") String userNo);
    Teacher getTeacher(@Param("userno") String userNo);
    void flushLoginTime(@Param("userno") String userNo);
    int updatePassword(@Param("userno") String userNo,
                       @Param("origpass") String origPass,
                       @Param("newpass") String newPass
    );
}
