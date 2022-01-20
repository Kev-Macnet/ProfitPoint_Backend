package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt住院診察費設定參數")
public class PtInpatientFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="住院診察費", required=true, position=21)
    @NotEmpty()
    private String category;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=22)
    private int max_inpatient_enable;
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=", example="5", required=false, position=23)
    private int max_inpatient;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=24)
    private int max_emergency_enable;
    @ApiModelProperty(value="單一急診就醫紀錄應用數量<=", example="3", required=false, position=25)
    private int max_emergency;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=26)
    private int max_patient_no_enable;
    @ApiModelProperty(value="每組病歷號碼，每院限申報", example="10", required=false, position=27)
    private int max_patient_no;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報(開關)", example="0", required=false, position=28)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=29)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="參與計畫之病患，不得申報(開關)", example="0", required=false, position=30)
    private int not_allow_plan_enable;
    @ApiModelProperty(value="not_allow_plan 清單", required=false, position=31)
    private java.util.List<String> lst_allow_plan;
    
    @ApiModelProperty(value="需與以下任一支付標準代碼並存(開關)", example="0", required=false, position=32)
    private int coexist_nhi_no_enable;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false, position=33)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="單一住院就醫紀錄門診診察費或住院診察費支付標準代碼，不可並存(開關)", example="0", required=false, position=34)
    private int no_coexist_enable;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public int getMax_emergency_enable() {
        return max_emergency_enable;
    }

    public void setMax_emergency_enable(int max_emergency_enable) {
        this.max_emergency_enable = max_emergency_enable;
    }

    public int getMax_emergency() {
        return max_emergency;
    }

    public void setMax_emergency(int max_emergency) {
        this.max_emergency = max_emergency;
    }

    public int getMax_patient_no_enable() {
        return max_patient_no_enable;
    }

    public void setMax_patient_no_enable(int max_patient_no_enable) {
        this.max_patient_no_enable = max_patient_no_enable;
    }

    public int getMax_patient_no() {
        return max_patient_no;
    }

    public void setMax_patient_no(int max_patient_no) {
        this.max_patient_no = max_patient_no;
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

    public int getNot_allow_plan_enable() {
        return not_allow_plan_enable;
    }

    public void setNot_allow_plan_enable(int not_allow_plan_enable) {
        this.not_allow_plan_enable = not_allow_plan_enable;
    }

    public java.util.List<String> getLst_allow_plan() {
        return lst_allow_plan;
    }

    public void setLst_allow_plan(java.util.List<String> lst_allow_plan) {
        this.lst_allow_plan = lst_allow_plan;
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

    public int getNo_coexist_enable() {
        return no_coexist_enable;
    }

    public void setNo_coexist_enable(int no_coexist_enable) {
        this.no_coexist_enable = no_coexist_enable;
    }

}



