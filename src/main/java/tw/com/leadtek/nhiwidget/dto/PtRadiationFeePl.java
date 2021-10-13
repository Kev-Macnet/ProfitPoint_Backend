package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt放射線診療費設定參數")
public class PtRadiationFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="放射線診療費", required=true, position=21)
    private String category;
    
    @ApiModelProperty(value="與此支付標準代碼並存時，需提示有無特別原由", example="0", required=false, position=22)
    private int notify_nhi_no_enable;
    @ApiModelProperty(value="notify_nhi_no 清單", required=false, position=23)
    private java.util.List<String> lst_ntf_nhi_no;

    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", required=false, position=24)
    private int coexist_nhi_no_enable;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false, position=25)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false, position=26)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=27)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=28)
    private int max_inpatient_enable;
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=n次", example="5",required=false, position=29)
    private int max_inpatient;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getNotify_nhi_no_enable() {
        return notify_nhi_no_enable;
    }
    public void setNotify_nhi_no_enable(int notify_nhi_no_enable) {
        this.notify_nhi_no_enable = notify_nhi_no_enable;
    }
    public java.util.List<String> getLst_ntf_nhi_no() {
        return lst_ntf_nhi_no;
    }
    public void setLst_ntf_nhi_no(java.util.List<String> lst_ntf_nhi_no) {
        this.lst_ntf_nhi_no = lst_ntf_nhi_no;
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

}



