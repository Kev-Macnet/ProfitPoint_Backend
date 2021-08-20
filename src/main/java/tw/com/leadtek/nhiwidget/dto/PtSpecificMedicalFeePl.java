package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt特定診療檢查費設定參數")
public class PtSpecificMedicalFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="特定診療檢查費", required=true)
    private String category;
    
//    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報-list',
//    interval_nday smallint default 0 comment '同患者每次申報間隔大於等於n day',
//    max_times smallint default 0 comment '一年內限定申報次數',

    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="同患者每次申報間隔大於等於n day", example="0", required=false)
    private int interval_nday;
    
    @ApiModelProperty(value="同患者一年內限定申報次數", example="0", required=false)
    private int max_times;

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

    public int getInterval_nday() {
        return interval_nday;
    }

    public void setInterval_nday(int interval_nday) {
        this.interval_nday = interval_nday;
    }

    public int getMax_times() {
        return max_times;
    }

    public void setMax_times(int max_times) {
        this.max_times = max_times;
    }

}



