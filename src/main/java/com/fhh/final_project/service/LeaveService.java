package com.fhh.final_project.service;
import com.fhh.final_project.entity.Course;
import com.fhh.final_project.entity.Leave;
import com.fhh.final_project.entity.StuLeave;
import com.fhh.final_project.mapper.LeaveMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LeaveService {
    /*
     * 新请假申请的方法:
     * 1.生成请假的编号（UUID,char(36)），用以区分不同的请假。
     * 2.将数据插入请假表中（UUID，学生No，起始时间，截至时间，状态（0），备注）。
     *   状态： X   X X X  X             X                   X               X
     *        归档 不使用  ！当前审核状态  教务处审核记录        辅导员审核记录    删除
     *   效仿Unix文件系统权限。
     *   若审核状态为1,视为拒绝审核。
     *   经过计算，一些基本的状态如下：
     *   1.提交：0.
     *   2.提交，辅导员审核通过：2.
     *   3.提交，辅导员审核通过，教务处审核通过：2+4=6.
     *   4.提交，辅导员审核通过，教务处审核通过，归档：2+4+128=134.
     *   5.提交，辅导员审核不通过，归档：2+8+128=138.
     *   6.提交，辅导员审核通过，教务处审核不通过：2+4+8=14.
     *   7.提交，删除：1.
     *
     * 3.将数据插入请假课程表中(UUID，课程ID，日期，课程起始节次)。
     */
    @Autowired
    private LeaveMapper leaveMapper;
    @Autowired
    private CourseService courseService;
    public int insertLeave(String leaveStuNo, Date begin, Date end,String leaveReason,int leavetype){
        String leaveUuid = UUID.randomUUID().toString();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String leaveBeginTime = sdf.format(begin);
        String leaveEndTime = sdf.format(end);
        byte leaveState=0;
        Course[] courses = courseService.getCourses(leaveStuNo,begin,end);
        List<Leave> leaves = new ArrayList<>();
        //UUID，课程ID，日期，课程起始节次
        //较为愚蠢的方法，O(n^2).
        Calendar a = Calendar.getInstance(),b = Calendar.getInstance();
        a.set(Calendar. DAY_OF_WEEK, Calendar.MONDAY);
        a.setTime(end);
        b.set(Calendar. DAY_OF_WEEK, Calendar.MONDAY);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean shouldAddLeap = a.get(Calendar.YEAR)-b.get(Calendar.YEAR)>0;
        for(b.setTime(begin);(a.get(Calendar.DAY_OF_YEAR)+(shouldAddLeap?365:0)-b.get(Calendar.DAY_OF_YEAR))>=0;b.add(Calendar.DAY_OF_YEAR,1)){
            for(Course i:courses)
                if(i.getWeekday()==b.get(Calendar.DAY_OF_WEEK)-1)
                    leaves.add(new Leave(leaveUuid,i.getNo(),sdf.format(b.getTime()),0));
        }
        //测试。
        for (Leave i:leaves)
            System.out.println(i);
        //return 0;
        leaveMapper.addLeave(leaveUuid,leaveStuNo,leaveBeginTime,leaveEndTime,leaveState,leaveReason,leavetype);
        return ((leaves.size()==0)?0:leaveMapper.insertLeaves(leaves));
    }
    public List<StuLeave> getNormalLeaveTitleByStuNo(String stuno,int pageno){
        return leaveMapper.getLeaveTitleByName(stuno,pageno,129,0);
    }
    public List<StuLeave> getDeleteLeaveTitleByStuNo(String stuno,int pageno){
        return leaveMapper.getLeaveTitleByName(stuno,pageno,129,1);
    }
    public List<StuLeave> getArchiveLeaveTitleByStuNo(String stuno,int pageno){
        return leaveMapper.getLeaveTitleByName(stuno,pageno,128,128);
    }
    public StuLeave getFullLeaveByUuid(String leaveuuid){
        return leaveMapper.getFullLeaveByUuid(leaveuuid);
    }
    public List<Leave> getLeaveCoursesByUuid(String leaveuuid) {
        return leaveMapper.getLeaveCoursesByUuid(leaveuuid);
    }
    public int triggerDeleteMarkerByUuid(String leaveuuid){
        return leaveMapper.triggerDeleteMarkerByUuid(leaveuuid);
    }
    public int triggerArchiveMarkerByUuid(String leaveuuid){
        return leaveMapper.triggerArchiveMarkerByUuid(leaveuuid);
    }
    //学生端，获取正常(无归档/删除)请假的总数量
    public int getNormalLeaveCountByStuNo(String stuno){
        String source="stu_leaves as sl";
        String whereClause="leavestate&129=0 and leavestuno='"+stuno+"'";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getDeleteLeaveCountByStuNo(String stuno){
        String source="stu_leaves as sl";
        String whereClause="leavestate&129=1 and leavestuno='"+stuno+"'";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getNormalLeaveCountByInstructorNo(String instructorno){
        String source="stu_leaves as sl,student as s";
        String whereClause="s.stuno = sl.leavestuno and sl.leavestate&129=0 and s.stuinstructor = '"+instructorno+"'";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getArchiveLeaveCountByInstructorNo(String instructorno){
        String source="stu_leaves as sl,student as s";
        String whereClause="s.stuno = sl.leavestuno and sl.leavestate&129=128 and s.stuinstructor = '"+instructorno+"'";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getNormalLeaveCountByTeacherNo(String teacherno){
        String source="stu_leaves as sl,leaves as l,courses as c,student as s";
        String whereClause=String.format(" l.leaveuuid = sl.leaveuuid and l.courseid=c.courseid and c.courseteacherno=%s and sl.leavestate&%d=%d and sl.leavestuno = s.stuno",teacherno,129,0);
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getWaitingLeaveCountByTeacherNo(String teacherno){
        String source="stu_leaves as sl,leaves as l,courses as c,student as s";
        String whereClause=String.format(" l.leaveuuid = sl.leaveuuid and l.courseid=c.courseid and c.courseteacherno=%s and sl.leavestate&%d=%d and sl.leavestuno = s.stuno and l.readstate=0",teacherno,142,6);
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getArchiveLeaveCountByTeacherNo(String teacherno){
        String source="stu_leaves as sl,leaves as l,courses as c,student as s";
        String whereClause=String.format(" l.leaveuuid = sl.leaveuuid and l.courseid=c.courseid and c.courseteacherno=%s and sl.leavestate&%d=%d and sl.leavestuno = s.stuno",teacherno,129,128);
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int getArchiveLeaveCountByStuNo(String stuno){
        String source="stu_leaves as sl";
        String whereClause="leavestate&128=128 and leavestuno='"+stuno+"'";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public List<StuLeave> getNormalLeaveTitleByInstructorNo(String instructorno,int pageno){
        return leaveMapper.getLeaveTitleByInstructorNo(instructorno,pageno,129,0);
    }
    public List<StuLeave> getArchiveLeaveTitle(int pageno){
        return leaveMapper.getLeaveTitle(pageno,129,128);
    }
    public int getArchiveLeaveCount(){
        String source="stu_leaves as sl";
        String whereClause="leavestate&129=128";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public List<StuLeave> getWaitingLeaveTitle(int pageno){
        return leaveMapper.getLeaveTitle(pageno,143,2);
    }
    public int getWaitingLeaveCount(){
        String source="stu_leaves as sl";
        String whereClause="leavestate&143=2";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public List<StuLeave> getNormalLeaveTitle(int pageno){
        return leaveMapper.getLeaveTitle(pageno,129,0);
    }
    public int getNormalLeaveCount(){
        String source="stu_leaves as sl";
        String whereClause="leavestate&129=0";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public List<StuLeave> getArchiveLeaveTitleByInstructorNo(String instructorno,int pageno){
        return leaveMapper.getLeaveTitleByInstructorNo(instructorno,pageno,129,128);
    }
    public List<StuLeave> getWaitingLeaveTitleByInstructorNo(String instructorno,int pageno){
        return leaveMapper.getLeaveTitleByInstructorNo(instructorno,pageno,131,0);
    }
    public List<StuLeave> getNormalLeaveTitleByTeacherNo(String teacherno, int pageno) {
        return leaveMapper.getLeaveTitleByTeacherNo(teacherno,pageno,129,0);
    }
    public List<StuLeave> getArchiveLeaveTitleByTeacherNo(String teacherno, int pageno) {
        return leaveMapper.getLeaveTitleByTeacherNo(teacherno,pageno,129,128);
    }
    public List<StuLeave> getWaitingLeaveTitleByTeacherNo(String teacherno, int pageno) {
        return leaveMapper.getWaitingLeaveTitleByTeacherNo(teacherno,pageno,142,6);
    }
    public int getWaitingCountByInstructorNo(String instructorno){
        /*
         * 辅导员需要处理的请假需求：
         * 1.未归档，未删除;
         * 2.自身未审核。
         * 0XXXXX00
         *    State&10000011==0
         * -->state&131==0
         */
        String source="stu_leaves as sl,student as s";
        String whereClause="s.stuno = sl.leavestuno and sl.leavestate&131=0 and s.stuinstructor = '"+instructorno+"'";
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int setResult(String leaveuuid,int cal){
        return leaveMapper.setResultByUuidAndCal(leaveuuid,cal);
    }
    public List<StuLeave> getLeavesByGenericConditions(String source,String whereClause ,int page){
        return leaveMapper.getLeavesByGenericConditions(source, whereClause, page);
    }
    public int getLeaveCountByGenericConditions(String source,String whereClause){
        return leaveMapper.getTotalCountByCondition(source,whereClause);
    }
    public int setTeacherReadStateByTeacherNoAndUuid(String leaveuuid,String teacherno){
        return leaveMapper.setTeacherReadStateByTeacherNoAndUuid(leaveuuid, teacherno);
    }
    public List<String> getWaitingTeacherNameByUuid(String leaveuuid){
        return leaveMapper.getWaitingTeacherNameByUuid(leaveuuid);
    }
    public int deleteLeavePerm(String leaveuuid){
        return leaveMapper.deleteLeavePerm(leaveuuid);
    }
    public int clearDelete(String stuno){
        return leaveMapper.clearDelete(stuno);
    }
    public int getMostLeaveType(){
        return leaveMapper.getMostLeaveType().get(0).get("0");
    }
    public int getSimilarLeaveCountByLeaveTypeAndStuno(int leavetype,String stuno){
        return  leaveMapper.getSimilarLeaveCountByLeaveTypeAndStuno(leavetype,stuno);
    }
}
