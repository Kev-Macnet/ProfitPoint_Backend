package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt管灌飲食費設定參數")
public class PtTubeFeedingFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="管灌飲食費", required=true)
    private String category;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false)
    private java.util.List<String> lst_nhi_no;

    public String getCategory() {
        return category;
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

}



