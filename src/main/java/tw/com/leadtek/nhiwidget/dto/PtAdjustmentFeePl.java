package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt調劑費設定參數")
public class PtAdjustmentFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="調劑費", required=true)
    private String category;

    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false)
    private java.util.List<String> lst_co_nhi_no;
    
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
    public int getCoexist_nhi_no() {
        return coexist_nhi_no;
    }
    public void setCoexist_nhi_no(int coexist_nhi_no) {
        this.coexist_nhi_no = coexist_nhi_no;
    }
    public java.util.List<String> getLst_co_nhi_no() {
        return lst_co_nhi_no;
    }
    public void setLst_co_nhi_no(java.util.List<String> lst_co_nhi_no) {
        this.lst_co_nhi_no = lst_co_nhi_no;
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



