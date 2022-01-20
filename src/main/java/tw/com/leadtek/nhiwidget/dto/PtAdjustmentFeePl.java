package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt調劑費設定參數")
public class PtAdjustmentFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="調劑費", required=true, position=21)
    @NotEmpty()
    private String category;

    @ApiModelProperty(value="需與以下任一支付標準代碼並存(開關)", example="0", required=false, position=22)
    private int coexist_nhi_no_enable;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false, position=23)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報(開關)", example="0", required=false, position=24)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false, position=25)
    private java.util.List<String> lst_nhi_no;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    
    public java.util.List<String> getLst_co_nhi_no() {
        return lst_co_nhi_no;
    }
    public void setLst_co_nhi_no(java.util.List<String> lst_co_nhi_no) {
        this.lst_co_nhi_no = lst_co_nhi_no;
    }
    
    public java.util.List<String> getLst_nhi_no() {
        return lst_nhi_no;
    }
    public void setLst_nhi_no(java.util.List<String> lst_nhi_no) {
        this.lst_nhi_no = lst_nhi_no;
    }
    public int getCoexist_nhi_no_enable() {
        return coexist_nhi_no_enable;
    }
    public void setCoexist_nhi_no_enable(int coexist_nhi_no_enable) {
        this.coexist_nhi_no_enable = coexist_nhi_no_enable;
    }
    public int getExclude_nhi_no_enable() {
        return exclude_nhi_no_enable;
    }
    public void setExclude_nhi_no_enable(int exclude_nhi_no_enable) {
        this.exclude_nhi_no_enable = exclude_nhi_no_enable;
    }

}



