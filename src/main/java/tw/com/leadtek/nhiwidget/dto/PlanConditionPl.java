package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl

@ApiModel(value = "計畫可收案病例條件參數")
public class PlanConditionPl {
    @ApiModelProperty(value="計畫名稱", example="星光計畫", required=true, position=2)
    private String name;
    @ApiModelProperty(value="就醫科別", example="心臟科", required=true, position=3)
    private String division;
    @ApiModelProperty(value="是否啟用(0.未啟動/1.使用中/2.鎖定),更新API不支援此參數", example="1", required=true, position=4)
    private int active;
    
    @ApiModelProperty(value="開關--本院ICD碼(1|0)", example="1", required=false, position=5)
    private int icd_no_enable;
    @ApiModelProperty(value="本院ICD碼列表", required=true, position=5)
    private java.util.List<String> icd_no;
    
    @ApiModelProperty(value="開關--就醫天數少於 n day(1|0)", example="1", required=false, position=6)
    private int less_nday_enable;
    @ApiModelProperty(value="就醫天數少於 n day列表", required=true, position=6)
    private java.util.List<PlanLessNDayPl> less_nday;
    
    @ApiModelProperty(value="開關--就醫次數大於 n 次(1|0)", example="1", required=false, position=7)
    private int more_times_enable;
    @ApiModelProperty(value="就醫次數大於 n 次列表", required=true, position=7)
    private java.util.List<PlanMoreTimesPl> more_times;
    
    @ApiModelProperty(value="開關--曾申報過ICD診斷碼(1|0)", example="1", required=true, position=8)
    private int exp_icd_no_enable;
    @ApiModelProperty(value="曾申報過ICD診斷碼", example="icd-001", required=true, position=9)
    private String exp_icd_no;
    
    @ApiModelProperty(value="開關--不曾申報過ICD診斷碼(1|0)", example="1", required=true, position=10)
    private int no_exp_icd_no_enable;
    @ApiModelProperty(value="不曾申報過ICD診斷碼", example="icd-002", required=true, position=11)
    private String no_exp_icd_no;
    
    @ApiModelProperty(value="開關--排除就醫申報中含有精神科慢性病房住院照護類費用(1|0)", example="1", required=true, position=12)
    private int exclude_psychiatric_enable;
    
    @ApiModelProperty(value="開關--領藥(1|0)", example="1", required=true, position=13)
    private int medicine_times_enable;
    @ApiModelProperty(value="領藥次數>=", example="10", required=true, position=14)
    private int medicine_times;
    @ApiModelProperty(value="領藥科別", example="骨科", required=true, position=15)
    private String medicine_times_division;
    
    @ApiModelProperty(value="開關--同院計畫n天結案(1|0)", example="1", required=true, position=16)
    private int exclude_plan_nday_enable;
    @ApiModelProperty(value="同院計畫n天結案", example="10", required=true, position=17)
    private int exclude_plan_nday;
    
    @ApiModelProperty(value="開關--排除同院所參與者(1|0)", example="1", required=true, position=18)
    private int exclude_join_enable;
    @ApiModelProperty(value="排除同院所參與者", example="G101***505", required=true, position=19)
    private String exclude_join;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDivision() {
        return division;
    }
    public void setDivision(String division) {
        this.division = division;
    }
    
    public int getExp_icd_no_enable() {
        return exp_icd_no_enable;
    }
    public void setExp_icd_no_enable(int exp_icd_no_enable) {
        this.exp_icd_no_enable = exp_icd_no_enable;
    }
    public String getExp_icd_no() {
        return exp_icd_no;
    }
    public void setExp_icd_no(String exp_icd_no) {
        this.exp_icd_no = exp_icd_no;
    }
    public int getNo_exp_icd_no_enable() {
        return no_exp_icd_no_enable;
    }
    public void setNo_exp_icd_no_enable(int no_exp_icd_no_enable) {
        this.no_exp_icd_no_enable = no_exp_icd_no_enable;
    }
    public String getNo_exp_icd_no() {
        return no_exp_icd_no;
    }
    public void setNo_exp_icd_no(String no_exp_icd_no) {
        this.no_exp_icd_no = no_exp_icd_no;
    }
    public int getExclude_psychiatric_enable() {
        return exclude_psychiatric_enable;
    }
    public void setExclude_psychiatric_enable(int exclude_psychiatric_enable) {
        this.exclude_psychiatric_enable = exclude_psychiatric_enable;
    }
    public int getMedicine_times_enable() {
        return medicine_times_enable;
    }
    public void setMedicine_times_enable(int medicine_times_enable) {
        this.medicine_times_enable = medicine_times_enable;
    }
    public int getMedicine_times() {
        return medicine_times;
    }
    public void setMedicine_times(int medicine_times) {
        this.medicine_times = medicine_times;
    }
    public String getMedicine_times_division() {
        return medicine_times_division;
    }
    public void setMedicine_times_division(String medicine_times_division) {
        this.medicine_times_division = medicine_times_division;
    }
    public int getExclude_plan_nday_enable() {
        return exclude_plan_nday_enable;
    }
    public void setExclude_plan_nday_enable(int exclude_plan_nday_enable) {
        this.exclude_plan_nday_enable = exclude_plan_nday_enable;
    }
    public int getExclude_plan_nday() {
        return exclude_plan_nday;
    }
    public void setExclude_plan_nday(int exclude_plan_nday) {
        this.exclude_plan_nday = exclude_plan_nday;
    }
    public int getExclude_join_enable() {
        return exclude_join_enable;
    }
    public void setExclude_join_enable(int exclude_join_enable) {
        this.exclude_join_enable = exclude_join_enable;
    }
    public String getExclude_join() {
        return exclude_join;
    }
    public void setExclude_join(String exclude_join) {
        this.exclude_join = exclude_join;
    }
    
    public java.util.List<PlanLessNDayPl> getLess_nday() {
        return less_nday;
    }
    public void setLess_nday(java.util.List<PlanLessNDayPl> less_nday) {
        this.less_nday = less_nday;
    }
    public java.util.List<PlanMoreTimesPl> getMore_times() {
        return more_times;
    }
    public void setMore_times(java.util.List<PlanMoreTimesPl> more_times) {
        this.more_times = more_times;
    }
    public int getIcd_no_enable() {
        return icd_no_enable;
    }
    public void setIcd_no_enable(int icd_no_enable) {
        this.icd_no_enable = icd_no_enable;
    }
    public java.util.List<String> getIcd_no() {
        return icd_no;
    }
    public void setIcd_no(java.util.List<String> icd_no) {
        this.icd_no = icd_no;
    }
    public int getLess_nday_enable() {
        return less_nday_enable;
    }
    public void setLess_nday_enable(int less_nday_enable) {
        this.less_nday_enable = less_nday_enable;
    }
    public int getMore_times_enable() {
        return more_times_enable;
    }
    public void setMore_times_enable(int more_times_enable) {
        this.more_times_enable = more_times_enable;
    }
    
    public int getActive() {
        return active;
    }
    
    public void setActive(int active) {
        this.active = active;
    }
}
