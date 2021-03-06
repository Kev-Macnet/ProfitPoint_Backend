package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt治療處置費設定參數")
public class PtTreatmentFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="治療處置費", required=true, position=21)
    @NotEmpty()
    private String category;

    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報(開關)", example="0", required=false, position=22)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=23)
    private java.util.List<String> lst_nhi_no;

    @ApiModelProperty(value="需與以下任一支付標準代碼並存(開關)", example="0", required=false, position=24)
    private int coexist_nhi_no_enable;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false, position=25)
    private java.util.List<String> lst_co_nhi_no;

    @ApiModelProperty(value="單一就醫紀錄上，須包含以下任一ICD診斷碼", example="0", required=false, position=26)
    private int include_icd_no_enable;
    @ApiModelProperty(value="ICD診斷碼清單", required=false, position=27)
    private java.util.List<String> lst_icd_no;

    @ApiModelProperty(value="限定特定科別應用", example="0", required=false, position=28)
    private int lim_division_enable;
    @ApiModelProperty(value="lim_division 清單", required=false, position=29)
    private java.util.List<String> lst_division;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=30)
    private int max_inpatient_enable;
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=", example="20",required=false, position=31)
    private int max_inpatient;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=32)
    private int max_daily_enable;
    @ApiModelProperty(value="單一就醫紀錄上，每日限定應用小於等於n次", example="1", required=false, position=33)
    private int max_daily;
    
    @ApiModelProperty(value="每 days 天 <= times 次", example="0", required=false, position=34)
    private int every_nday_enable;
    @ApiModelProperty(value="每 days 天", example="3", required=false, position=35)
    private int every_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false, position=36)
    private int every_nday_times;

    @ApiModelProperty(value="同患者限定每 days 天 <= times 次", example="0", required=false, position=37)
    private int patient_nday_enable;
    @ApiModelProperty(value="每 days 天", example="3", required=false, position=38)
    private int patient_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false, position=39)
    private int patient_nday_times;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=40)
    private int max_patient_enable;
    @ApiModelProperty(value="每組病歷號碼，每院限申報次數", example="20", required=false, position=41)
    private int max_patient;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=42)
    private int max_month_enable;
    @ApiModelProperty(value="每月申報數量，不可超過門診就診人次之百分之x", example="5", required=false, position=43)
    private int max_month_percentage;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=44)
    private int max_age_enable;
    @ApiModelProperty(value="患者限定年紀小於等於 n 方可進行申報", example="0", required=false, position=45)
    private int max_age;
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getExclude_nhi_no_enable() {
        return exclude_nhi_no_enable;
    }
    public void setExclude_nhi_no_enable(int exclude_nhi_no_enable) {
        this.exclude_nhi_no_enable = exclude_nhi_no_enable;
    }
    public java.util.List<String> getLst_nhi_no() {
        return lst_nhi_no;
    }
    public void setLst_nhi_no(java.util.List<String> lst_nhi_no) {
        this.lst_nhi_no = lst_nhi_no;
    }
    public int getCoexist_nhi_no_enable() {
        return coexist_nhi_no_enable;
    }
    public void setCoexist_nhi_no_enable(int coexist_nhi_no_enable) {
        this.coexist_nhi_no_enable = coexist_nhi_no_enable;
    }
    public java.util.List<String> getLst_co_nhi_no() {
        return lst_co_nhi_no;
    }
    public void setLst_co_nhi_no(java.util.List<String> lst_co_nhi_no) {
        this.lst_co_nhi_no = lst_co_nhi_no;
    }
    public int getInclude_icd_no_enable() {
        return include_icd_no_enable;
    }
    public void setInclude_icd_no_enable(int include_icd_no_enable) {
        this.include_icd_no_enable = include_icd_no_enable;
    }
    public java.util.List<String> getLst_icd_no() {
        return lst_icd_no;
    }
    public void setLst_icd_no(java.util.List<String> lst_icd_no) {
        this.lst_icd_no = lst_icd_no;
    }
    public int getLim_division_enable() {
        return lim_division_enable;
    }
    public void setLim_division_enable(int lim_division_enable) {
        this.lim_division_enable = lim_division_enable;
    }
    public java.util.List<String> getLst_division() {
        return lst_division;
    }
    public void setLst_division(java.util.List<String> lst_division) {
        this.lst_division = lst_division;
    }
    public int getMax_inpatient_enable() {
        return max_inpatient_enable;
    }
    public void setMax_inpatient_enable(int max_inpatient_enable) {
        this.max_inpatient_enable = max_inpatient_enable;
    }
    public int getMax_inpatient() {
        return max_inpatient;
    }
    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
    }
    public int getMax_daily_enable() {
        return max_daily_enable;
    }
    public void setMax_daily_enable(int max_daily_enable) {
        this.max_daily_enable = max_daily_enable;
    }
    public int getMax_daily() {
        return max_daily;
    }
    public void setMax_daily(int max_daily) {
        this.max_daily = max_daily;
    }
    public int getEvery_nday_enable() {
        return every_nday_enable;
    }
    public void setEvery_nday_enable(int every_nday_enable) {
        this.every_nday_enable = every_nday_enable;
    }
    public int getEvery_nday_days() {
        return every_nday_days;
    }
    public void setEvery_nday_days(int every_nday_days) {
        this.every_nday_days = every_nday_days;
    }
    public int getEvery_nday_times() {
        return every_nday_times;
    }
    public void setEvery_nday_times(int every_nday_times) {
        this.every_nday_times = every_nday_times;
    }
    public int getPatient_nday_enable() {
        return patient_nday_enable;
    }
    public void setPatient_nday_enable(int patient_nday_enable) {
        this.patient_nday_enable = patient_nday_enable;
    }
    public int getPatient_nday_days() {
        return patient_nday_days;
    }
    public void setPatient_nday_days(int patient_nday_days) {
        this.patient_nday_days = patient_nday_days;
    }
    public int getPatient_nday_times() {
        return patient_nday_times;
    }
    public void setPatient_nday_times(int patient_nday_times) {
        this.patient_nday_times = patient_nday_times;
    }
    public int getMax_patient_enable() {
        return max_patient_enable;
    }
    public void setMax_patient_enable(int max_patient_enable) {
        this.max_patient_enable = max_patient_enable;
    }
    public int getMax_patient() {
        return max_patient;
    }
    public void setMax_patient(int max_patient) {
        this.max_patient = max_patient;
    }
    public int getMax_month_enable() {
        return max_month_enable;
    }
    public void setMax_month_enable(int max_month_enable) {
        this.max_month_enable = max_month_enable;
    }
    public int getMax_month_percentage() {
        return max_month_percentage;
    }
    public void setMax_month_percentage(int max_month_percentage) {
        this.max_month_percentage = max_month_percentage;
    }
    public int getMax_age_enable() {
        return max_age_enable;
    }
    public void setMax_age_enable(int max_age_enable) {
        this.max_age_enable = max_age_enable;
    }
    public int getMax_age() {
        return max_age;
    }
    public void setMax_age(int max_age) {
        this.max_age = max_age;
    }

}



