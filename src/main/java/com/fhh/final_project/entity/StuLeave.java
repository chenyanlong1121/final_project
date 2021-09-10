package com.fhh.final_project.entity;

public class StuLeave {
    public static final String[] TYPES={"事假","公假","病假","补假"};
    public String leaveuuid,leavestuno,leavebegintime,leaveendtime,leavereason,leavename;
    public int leavetype,leavestate,teacherreadstate;
    public StuLeave(String leaveuuid, String leavestuno, String leavebegintime, String leaveendtime, int leavestate, String leavereason,int leavetype,String leavename,int teacherreadstate){
        this(leaveuuid, leavestuno, leavebegintime, leaveendtime, leavestate, leavereason, leavetype);
        this.teacherreadstate = teacherreadstate;
        this.leavename = leavename;
    }
    //构建完全的学生-请假对应关系
    public StuLeave(String leaveuuid, String leavestuno, String leavebegintime, String leaveendtime, int leavestate, String leavereason,int leavetype) {
        this.leaveuuid = leaveuuid;
        this.leavestuno = leavestuno;
        this.leavebegintime = leavebegintime;
        this.leaveendtime = leaveendtime;
        this.leavestate = leavestate;
        this.leavereason = leavereason;
        this.leavetype = leavetype;
    }
    //或者，在查询时返回标题的大致信息，此时只需初始化部分数据。

    public StuLeave(String leaveuuid, String leavebegintime,String leaveendtime, String leavereason,int leavestate) {
        this.leaveuuid = leaveuuid;
        this.leavebegintime = leavebegintime;
        this.leavestate = leavestate;
        this.leavereason = leavereason;
    }
    //或者，在非学生实体查询时，将姓名放置到数据中。
    public StuLeave(String leaveuuid, String leavebegintime,String leaveendtime,String leavereason, int leavestate,String leavename) {
        this(leaveuuid, leavebegintime,leaveendtime,leavereason,leavestate);
        this.leavename = leavename;
    }
    //由于Thymeleaf不支持按位运算，这里直接封装按位运算接口。
    public int checkstate() {
        //0：提交，1：辅导员通过，2：不通过，3：通过，4：归档，5：删除
        if((this.leavestate&128)==128)
            return 4;
        if((this.leavestate&1)==1)
            return 5;
        if((this.leavestate&8)==8)
            return 2;
        if((this.leavestate&6)==6)
            return 3;
        if(this.leavestate==0)
            return 0;
        return 1;
    }
    //详细的检查接口。
    public String checkprogressstate(int field) {
        String ret="";
        //0：提交，1：辅导员通过，2：不通过，3：通过，4：归档，5：删除
        switch(field){
            case 0:{
                ret = (leavestate&1)==1?"error":"active";
                break;
            }
            case 1:{
                if((leavestate&2)==0)
                    ret="";
                else
                    if((leavestate&4)==0&&(leavestate&8)==8)//出错，且教务处未审阅
                        ret="error";
                    else
                        ret="active";
                break;
            }
            case 2:{
                if((leavestate&4)==0)
                    ret="";
                else
                    if((leavestate&8)==8)
                        ret="error";
                    else
                        ret="active";
                break;
            }
            case 3:{break;}

        }
        return ret;
    }
    public boolean didInspectorRead(){
        return (leavestate&2)==2;
    }
    public boolean didTeacherRead(){
        if((leavestate&255)!=6)
            return true;
        return teacherreadstate!=0;
    }
    public boolean didAdministrationRead(){
        //1.未被拒绝。
        //2.辅导员已阅读。
        //3.自己没有阅读。
        return (leavestate&14)!=2;
    }
    public String getReason() {
        return TYPES[leavetype];
    }
}
