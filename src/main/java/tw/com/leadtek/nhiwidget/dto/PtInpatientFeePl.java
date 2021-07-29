package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "住院診察費設定參數")
public class PtInpatientFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="住院診察費", required=true)
    private String category;
    
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=", example="5", required=false)
    private int max_inpatient;
    
    @ApiModelProperty(value="單一急診就醫紀錄應用數量<=", example="3", required=false)
    private int max_emergency;
    
    @ApiModelProperty(value="每組病歷號碼，每院限申報", example="10", required=false)
    private int max_patient_no;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="支付代碼 list", required=false)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="參與計畫之病患，不得申報", example="0", required=false)
    private int not_allow_plan;
    @ApiModelProperty(value="計畫 list", required=false)
    private java.util.List<String> lst_allow_plan;
    
    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="計畫 list", required=false)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="單一住院就醫紀錄門診診察費或住院診察費支付標準代碼，不可並存", example="0", required=false)
    private int no_coexist;


//    @Override
    public String getCategory() {
        return category;
    }
//    @Override
    public void setCategory(String category) {
        this.category = category;
    }
    public int getMax_inpatient() {
        return max_inpatient;
    }
    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
    }
    public int getMax_emergency() {
        return max_emergency;
    }
    public void setMax_emergency(int max_emergency) {
        this.max_emergency = max_emergency;
    }
    public int getMax_patient_no() {
        return max_patient_no;
    }
    public void setMax_patient_no(int max_patient_no) {
        this.max_patient_no = max_patient_no;
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
    public int getNot_allow_plan() {
        return not_allow_plan;
    }
    public void setNot_allow_plan(int not_allow_plan) {
        this.not_allow_plan = not_allow_plan;
    }
    public java.util.List<String> getLst_allow_plan() {
        return lst_allow_plan;
    }
    public void setLst_allow_plan(java.util.List<String> lst_allow_plan) {
        this.lst_allow_plan = lst_allow_plan;
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
    public int getNo_coexist() {
        return no_coexist;
    }
    public void setNo_coexist(int no_coexist) {
        this.no_coexist = no_coexist;
    }

    /*
    max_inpatient smallint comment '單一住院就醫紀錄應用數量<=',
    max_emergency smallint comment '單一急診就醫紀錄應用數量<=',
    max_patient_no smallint comment '每組病歷號碼，每院限申報',
    exclude_nhi_no smallint comment '不可與此支付標準代碼並存單一就醫紀錄一併申報',
  
    not_allow_plan smallint comment '參與計畫之病患，不得申報',
    coexist_nhi_no smallint comment '需與以下任一支付標準代碼並存',
  
    no_coexist smallint comment '單一住院就醫紀錄門診診察費或住院診察費支付標準代碼，不可並存',
    */

}



