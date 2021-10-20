package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt特定診療檢查費設定參數")
public class PtSpecificMedicalFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="特定診療檢查費", required=true, position=21)
    private String category;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false, position=22)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=23)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=24)
    private int interval_nday_enable;
    @ApiModelProperty(value="同患者每次申報間隔大於等於n day", example="0", required=false, position=25)
    private int interval_nday;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=26)
    private int max_times_enable;
    @ApiModelProperty(value="同患者一年內限定申報次數", example="0", required=false, position=27)
    private int max_times;

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

    public int getInterval_nday_enable() {
        return interval_nday_enable;
    }

    public void setInterval_nday_enable(int interval_nday_enable) {
        this.interval_nday_enable = interval_nday_enable;
    }

    public int getInterval_nday() {
        return interval_nday;
    }

    public void setInterval_nday(int interval_nday) {
        this.interval_nday = interval_nday;
    }

    public int getMax_times_enable() {
        return max_times_enable;
    }

    public void setMax_times_enable(int max_times_enable) {
        this.max_times_enable = max_times_enable;
    }

    public int getMax_times() {
        return max_times;
    }

    public void setMax_times(int max_times) {
        this.max_times = max_times;
    }

}



