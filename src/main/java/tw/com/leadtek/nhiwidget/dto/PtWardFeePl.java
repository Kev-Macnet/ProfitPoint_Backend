package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt病房費設定參數")
public class PtWardFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="病房費", required=true)
    private String category;
    
//    min_stay smallint comment '入住時間滿n小時，方可申報此支付標準代碼',
//    max_stay smallint comment '入住時間超過n小時，不可申報此支付標準代碼',
//    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報',

    @ApiModelProperty(value="入住時間滿n小時，方可申報此支付標準代碼", example="24",required=false)
    private int min_stay;
    
    @ApiModelProperty(value="入住時間超過n小時，不可申報此支付標準代碼", example="168", required=false)
    private int max_stay;

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

    public int getMin_stay() {
        return min_stay;
    }

    public void setMin_stay(int min_stay) {
        this.min_stay = min_stay;
    }

    public int getMax_stay() {
        return max_stay;
    }

    public void setMax_stay(int max_stay) {
        this.max_stay = max_stay;
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



