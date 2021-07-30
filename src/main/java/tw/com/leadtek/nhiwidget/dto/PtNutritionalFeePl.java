package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "營養照護費設定參數")
public class PtNutritionalFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="營養照護費", required=true)
    private String category;

    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=", example="20",required=false)
    private int max_inpatient;
    @ApiModelProperty(value="單一就醫紀錄上，每日限定應用小於等於n次", example="1", required=false)
    private int max_daily;
    
    @ApiModelProperty(value="每 days 天 <= times 次", example="0", required=false)
    private int every_nday;
    @ApiModelProperty(value="每 days 天", example="3", required=false)
    private int every_nday_days;
    @ApiModelProperty(value="<= times 次", example="10", required=false)
    private int every_nday_times;
    
    @ApiModelProperty(value="超過 days 日後，超出天數部份限定應用 <= times 次", example="0", required=false)
    private int over_nday;
    @ApiModelProperty(value="超過 days 日後", example="3", required=false)
    private int over_nday_days;
    @ApiModelProperty(value="超出天數部份限定 <= times 次", example="10", required=false)
    private int over_nday_times;

    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="不可與此支付標準代碼清單", required=false)
    private java.util.List<String> lst_nhi_no;
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getMax_inpatient() {
        return max_inpatient;
    }
    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
    }
    public int getMax_daily() {
        return max_daily;
    }
    public void setMax_daily(int max_daily) {
        this.max_daily = max_daily;
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
    public int getOver_nday() {
        return over_nday;
    }
    public void setOver_nday(int over_nday) {
        this.over_nday = over_nday;
    }
    public int getOver_nday_days() {
        return over_nday_days;
    }
    public void setOver_nday_days(int over_nday_days) {
        this.over_nday_days = over_nday_days;
    }
    public int getOver_nday_times() {
        return over_nday_times;
    }
    public void setOver_nday_times(int over_nday_times) {
        this.over_nday_times = over_nday_times;
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



