package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt治療處置費設定參數")
public class PtTreatmentFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="治療處置費", required=true)
    private String category;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="不可與此支付標準代碼清單", required=false)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="需與以下任一支付標準代碼並存清單", required=false)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="單一就醫紀錄上，須包含以下任一ICD診斷碼", example="0", required=false)
    private int include_icd_no;
    @ApiModelProperty(value="ICD診斷碼清單", required=false)
    private java.util.List<String> lst_icd_no;
    
    @ApiModelProperty(value="限定特定科別應用", example="0", required=false)
    private int lim_division;
    @ApiModelProperty(value="科別清單", position=25, required=false)
    private java.util.List<String> lst_division;

    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=", example="20",required=false)
    private int max_inpatient;
    @ApiModelProperty(value="單一就醫紀錄上，每日限定應用小於等於n次", example="1", required=false)
    private int max_daily;
    
    @ApiModelProperty(value="每 days 天 <= times 次", example="0", required=false)
    private int every_nday;
    @ApiModelProperty(value="每 days 天", example="3", required=false)
    private int every_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false)
    private int every_nday_times;
    
    @ApiModelProperty(value="同患者限定每 days 天 <= times 次", example="0", required=false)
    private int patient_nday;
    @ApiModelProperty(value="每 days 天", example="3", required=false)
    private int patient_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false)
    private int patient_nday_times;

    @ApiModelProperty(value="每組病歷號碼，每院限申報次數", example="20", required=false)
    private int max_patient;
    @ApiModelProperty(value="每月申報數量，不可超過門診就診人次之百分之x", example="5", required=false)
    private int max_month_percentage;
    @ApiModelProperty(value="患者限定年紀小於等於 n 方可進行申報", example="0", required=false)
    private int max_age;
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getExclude_nhi_no() {
        return exclude_nhi_no;
    }
    public void setExclude_nhi_no(int exclude_nhi_no) {
        this.exclude_nhi_no = exclude_nhi_no;
    }
    public java.util.List<String> getLst_nhi_no() {
        return lst_nhi_no;
    }
    public void setLst_nhi_no(java.util.List<String> lst_nhi_no) {
        this.lst_nhi_no = lst_nhi_no;
    }
    public int getCoexist_nhi_no() {
        return coexist_nhi_no;
    }
    public void setCoexist_nhi_no(int coexist_nhi_no) {
        this.coexist_nhi_no = coexist_nhi_no;
    }
    public java.util.List<String> getLst_co_nhi_no() {
        return lst_co_nhi_no;
    }
    public void setLst_co_nhi_no(java.util.List<String> lst_co_nhi_no) {
        this.lst_co_nhi_no = lst_co_nhi_no;
    }
    public int getInclude_icd_no() {
        return include_icd_no;
    }
    public void setInclude_icd_no(int include_icd_no) {
        this.include_icd_no = include_icd_no;
    }
    public java.util.List<String> getLst_icd_no() {
        return lst_icd_no;
    }
    public void setLst_icd_no(java.util.List<String> lst_icd_no) {
        this.lst_icd_no = lst_icd_no;
    }
    public int getLim_division() {
        return lim_division;
    }
    public void setLim_division(int lim_division) {
        this.lim_division = lim_division;
    }
    public java.util.List<String> getLst_division() {
        return lst_division;
    }
    public void setLst_division(java.util.List<String> lst_division) {
        this.lst_division = lst_division;
    }
    public int getMax_inpatient() {
        return max_inpatient;
    }
    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
    }
    public int getMax_daily() {
        return max_daily;
    }
    public void setMax_daily(int max_daily) {
        this.max_daily = max_daily;
    }
    public int getEvery_nday() {
        return every_nday;
    }
    public void setEvery_nday(int every_nday) {
        this.every_nday = every_nday;
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
    public int getPatient_nday() {
        return patient_nday;
    }
    public void setPatient_nday(int patient_nday) {
        this.patient_nday = patient_nday;
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
    public int getMax_patient() {
        return max_patient;
    }
    public void setMax_patient(int max_patient) {
        this.max_patient = max_patient;
    }
    public int getMax_month_percentage() {
        return max_month_percentage;
    }
    public void setMax_month_percentage(int max_month_percentage) {
        this.max_month_percentage = max_month_percentage;
    }
    public int getMax_age() {
        return max_age;
    }
    public void setMax_age(int max_age) {
        this.max_age = max_age;
    }

}



