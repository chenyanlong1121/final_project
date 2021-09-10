package com.fhh.final_project.mapper;

import com.fhh.final_project.entity.Leave;
import com.fhh.final_project.entity.StuLeave;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface LeaveMapper {
    int addLeave(/*UUID，学生No，起始时间，截至时间，状态（0），备注*/
                @Param("uuid") String uuid,
                @Param("leavestuno") String leavestuno,
                @Param("leavebegintime") String leavebegintime,
                @Param("leaveendtime") String leaveendtime,
                @Param("leavestate") byte leavestate,
                @Param("leavereason") String leavereason,
                @Param("leavetype") int leavetype
    );
    int insertLeaves(@Param("leaves") List<Leave> leaves);
    List<StuLeave> getLeaveTitleByName(@Param("stuno") String stuno,
                                       @Param("pageno") int pageno,
                                       @Param("filter") int filter,
                                       @Param("result") int result
    );
    List<StuLeave> getLeaveTitleByInstructorNo(@Param("instructorno") String instructorno,
                                               @Param("pageno") int pageno,
                                               @Param("filter") int filter,
                                               @Param("result") int result
    );
    //管理者：我全都要！
    List<StuLeave> getLeaveTitle(@Param("pageno") int pageno,
                                 @Param("filter") int filter,
                                 @Param("result") int result
    );
    List<StuLeave> getLeaveTitleByTeacherNo(@Param("teacherno") String instructorno,
                                            @Param("pageno") int pageno,
                                            @Param("filter") int filter,
                                            @Param("result") int result
    );
    List<StuLeave> getWaitingLeaveTitleByTeacherNo(@Param("teacherno") String instructorno,
                                            @Param("pageno") int pageno,
                                            @Param("filter") int filter,
                                            @Param("result") int result
    );
    StuLeave getFullLeaveByUuid(@Param("leaveuuid") String leaveuuid);
    List<Leave> getLeaveCoursesByUuid(@Param("leaveuuid") String leaveuuid);
    int triggerDeleteMarkerByUuid(@Param("leaveuuid") String leaveuuid);
    int getTotalCountByCondition(@Param("source") String source,
                                 @Param("whereclause") String whereclause);
    int triggerArchiveMarkerByUuid(@Param("leaveuuid") String leaveuuid);
    int setTeacherReadStateByTeacherNoAndUuid(
                                    @Param("leaveuuid") String leaveuuid,
                                    @Param("teacherno") String teacherno);
    int setResultByUuidAndCal(@Param("leaveuuid") String leaveuuid,@Param("cal") int cal);
    List<StuLeave> getLeavesByGenericConditions(@Param("source") String source,
                                                @Param("whereclause") String whereClause ,
                                                @Param("page") int page);
    List<String> getWaitingTeacherNameByUuid(@Param("leaveuuid") String leaveuuid);
    int deleteLeavePerm(@Param("leaveuuid") String leaveuuid);
    int clearDelete(@Param("stuno") String stuno);
    int getSimilarLeaveCountByLeaveTypeAndStuno(@Param("leavetype") int leavetype,
                                                @Param("leavestuno") String leavestuno);
    List<HashMap<Integer,Integer>> getMostLeaveType();
}
