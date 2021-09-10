package com.fhh.final_project.mapper;

import com.fhh.final_project.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseMapper {
    List<Course> getCourses(@Param("stuno") String stuNo,
                            @Param("begintimestr") String begintimestr,
                            @Param("endtimestr") String endtimestr);
}
