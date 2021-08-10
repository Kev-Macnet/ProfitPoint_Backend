package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt復健治療費設定參數")
public class PtRehabilitationFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="復健治療費", required=true)
    private String category;
    
//    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報-list',
//    patient_nday smallint comment '同患者限定每 days 天 <= times 次',
//    patient_nday_days smallint,
//    patient_nday_times smallint,
//    include_icd_no smallint default 0 comment '單一就醫紀錄上，須包含以下任一ICD診斷碼-list',
    
//    coexist_nhi_no smallint default 0 comment '限定同患者執行過任一支付標準代碼',
//    min_coexist smallint default 0 comment '大於等於n次,方可申報',
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

    @ApiModelProperty(value="單一就醫紀錄上，須包含以下任一ICD診斷碼", example="0", required=false)
    private int include_icd_no;
    @ApiModelProperty(value="include_icd_no 清單", required=false)
    private java.util.List<String> lst_icd_no;
    
    @ApiModelProperty(value="限定同患者執行過任一支付標準代碼", example="0", required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="coexist_nhi_no 大於等於n次方可申報", example="5", required=false)
    private int min_coexist;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="限定特定科別應用", example="0", required=false)
    private int lim_division;
    @ApiModelProperty(value="lim_division 清單", position=25, required=false)
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
    public int getCoexist_nhi_no() {
        return coexist_nhi_no;
    }
    public void setCoexist_nhi_no(int coexist_nhi_no) {
        this.coexist_nhi_no = coexist_nhi_no;
    }
    public int getMin_coexist() {
        return min_coexist;
    }
    public void setMin_coexist(int min_coexist) {
        this.min_coexist = min_coexist;
    }
    public java.util.List<String> getLst_co_nhi_no() {
        return lst_co_nhi_no;
    }
    public void setLst_co_nhi_no(java.util.List<String> lst_co_nhi_no) {
        this.lst_co_nhi_no = lst_co_nhi_no;
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



