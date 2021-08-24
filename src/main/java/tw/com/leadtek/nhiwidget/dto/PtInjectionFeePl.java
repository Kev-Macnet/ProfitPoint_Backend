package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt注射設定參數")
public class PtInjectionFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="注射", required=true)
    private String category;
    
//    interval_nday smallint default 0 comment '同患者每次申報間隔大於等於n day',
//    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報-list',
//    max_inpatient smallint comment '單一住院就醫紀錄應用數量<=n次',

    @ApiModelProperty(value="同患者每次申報間隔大於等於n day", example="10",required=false)
    private int interval_nday;
    
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=n次", example="16", required=false)
    private int max_inpatient;

    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false)
    private java.util.List<String> lst_nhi_no;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getInterval_nday() {
        return interval_nday;
    }
    public void setInterval_nday(int interval_nday) {
        this.interval_nday = interval_nday;
    }
    public int getMax_inpatient() {
        return max_inpatient;
    }
    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
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

}



