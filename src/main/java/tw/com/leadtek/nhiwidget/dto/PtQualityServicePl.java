package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt品質支付服務設定參數")
public class PtQualityServicePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="品質支付服務", required=true)
    private String category;
    
//    interval_nday smallint default 0 comment '同患者每次申報間隔大於等於n day',
//    coexist_nhi_no smallint default 0 comment '限定同患者執行過任一支付標準代碼',
//    min_coexist smallint default 0 comment '大於等於n次,方可申報',
//    every_nday smallint comment '每 days 天 <= times 次',
//    every_nday_days smallint,
//    every_nday_times smallint,

    @ApiModelProperty(value="同患者每次申報間隔大於等於 n day", example="10",required=false)
    private int interval_nday;
    

    @ApiModelProperty(value="限定同患者執行過任一支付標準代碼", example="0", required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="coexist_nhi_no 大於等於n次方可申報", example="5", required=false)
    private int min_coexist;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false)
    private java.util.List<String> lst_co_nhi_no;
     
    @ApiModelProperty(value="每 days 天 <= times 次", example="0", required=false)
    private int every_nday;
    @ApiModelProperty(value="每 days 天", example="3", required=false)
    private int every_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false)
    private int every_nday_times;
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
    public int getEvery_nday() {
        return every_nday;
    }
    public void setEvery_nday(int every_nday) {
        this.every_nday = every_nday;
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



