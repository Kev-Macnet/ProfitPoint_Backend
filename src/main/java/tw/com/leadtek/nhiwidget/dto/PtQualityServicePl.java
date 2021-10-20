package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt品質支付服務設定參數")
public class PtQualityServicePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="品質支付服務", required=true, position=21)
    private String category;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=22)
    private int interval_nday_enable;
    @ApiModelProperty(value="同患者每次申報間隔大於等於 n day", example="10",required=false, position=23)
    private int interval_nday;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=24)
    private int min_coexist_enable;
    @ApiModelProperty(value="coexist_nhi_no 大於等於n次方可申報", example="5", required=false, position=25)
    private int min_coexist;
    
    @ApiModelProperty(value="限定同患者執行過任一支付標準代碼", example="0", required=false, position=26)
    private int coexist_nhi_no_enable;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false, position=27)
    private java.util.List<String> lst_co_nhi_no;
     
    @ApiModelProperty(value="每 days 天 <= times 次", example="0", required=false, position=28)
    private int every_nday_enable;
    @ApiModelProperty(value="每 days 天", example="3", required=false, position=29)
    private int every_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false, position=30)
    private int every_nday_times;
    
    public String getCategory() {
        return category.replaceAll("\'", "\'\'");
    }
    public void setCategory(String category) {
        this.category = category;
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
    public int getMin_coexist_enable() {
        return min_coexist_enable;
    }
    public void setMin_coexist_enable(int min_coexist_enable) {
        this.min_coexist_enable = min_coexist_enable;
    }
    public int getMin_coexist() {
        return min_coexist;
    }
    public void setMin_coexist(int min_coexist) {
        this.min_coexist = min_coexist;
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
    public int getEvery_nday_enable() {
        return every_nday_enable;
    }
    public void setEvery_nday_enable(int every_nday_enable) {
        this.every_nday_enable = every_nday_enable;
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

}



