package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt精神醫療治療費設定參數")
public class PtPsychiatricFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="精神醫療治療費", required=true, position=21)
    private String category;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false, position=22)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=23)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="同患者限定每 days 天 <= times 次", example="0", required=false, position=24)
    private int patient_nday_enable;
    @ApiModelProperty(value="每 days 天", example="3", required=false, position=25)
    private int patient_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false, position=26)
    private int patient_nday_times;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=27)
    private int max_inpatient_enable;
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=n次", example="16", required=false, position=28)
    private int max_inpatient;

    @ApiModelProperty(value="限定特定科別應用", example="0", required=false, position=29)
    private int lim_division_enable;
    @ApiModelProperty(value="lim_division 清單", required=false, position=30)
    private java.util.List<String> lst_division;
    
    public String getCategory() {
        return category.replaceAll("\'", "\'\'");
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

}



