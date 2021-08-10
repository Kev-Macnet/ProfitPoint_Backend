package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt精神醫療治療費設定參數")
public class PtPsychiatricFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="精神醫療治療費", required=true)
    private String category;
    
//    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報-list',
//    patient_nday smallint comment '同患者限定每 days 天 <= times 次',
//    patient_nday_days smallint,
//    patient_nday_times smallint,
//    max_inpatient smallint comment '單一住院就醫紀錄應用數量<=n次',
//    lim_division smallint default 0 comment '限定特定科別應用-list',
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="同患者限定每 days 天 <= times 次", example="0", required=false)
    private int patient_nday;
    @ApiModelProperty(value="每 days 天", example="3", required=false)
    private int patient_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false)
    private int patient_nday_times;

    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=n次", example="16", required=false)
    private int max_inpatient;

    @ApiModelProperty(value="限定特定科別應用", position=9, example="0", required=false)
    private int lim_division;
    @ApiModelProperty(value="lim_division 清單", position=11, required=false)
    private java.util.List<String> lst_division;
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
    public int getMax_inpatient() {
        return max_inpatient;
    }
    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
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


}



