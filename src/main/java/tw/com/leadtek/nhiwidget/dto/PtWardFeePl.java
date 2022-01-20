package tw.com.leadtek.nhiwidget.dto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt病房費設定參數")
public class PtWardFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="病房費", required=true, position=21)
    @NotEmpty()
    private String category;

    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=22)
    private int min_stay_enable;
    @ApiModelProperty(value="入住時間滿n小時，方可申報此支付標準代碼", example="48",required=false, position=23)
    private int min_stay;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=24)
    private int max_stay_enable;
    @ApiModelProperty(value="入住時間超過n小時，不可申報此支付標準代碼", example="168", required=false, position=25)
    private int max_stay;

    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報(開關)", example="0", required=false, position=26)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=27)
    private java.util.List<String> lst_nhi_no;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getMin_stay_enable() {
        return min_stay_enable;
    }
    public void setMin_stay_enable(int min_stay_enable) {
        this.min_stay_enable = min_stay_enable;
    }
    public int getMin_stay() {
        return min_stay;
    }
    public void setMin_stay(int min_stay) {
        this.min_stay = min_stay;
    }
    public int getMax_stay_enable() {
        return max_stay_enable;
    }
    public void setMax_stay_enable(int max_stay_enable) {
        this.max_stay_enable = max_stay_enable;
    }
    public int getMax_stay() {
        return max_stay;
    }
    public void setMax_stay(int max_stay) {
        this.max_stay = max_stay;
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

}



