package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt放射線診療費設定參數")
public class PtRadiationFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="放射線診療費", required=true)
    private String category;
    
//    notify_nhi_no smallint default 0 comment '與此支付標準代碼並存時，需提示有無特別原由-list',
//    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報-list',
//    coexist_nhi_no smallint default 0 comment '需與以下任一支付標準代碼並存-list',
//    max_inpatient smallint comment '單一住院就醫紀錄應用數量<=n次',
    
    @ApiModelProperty(value="與此支付標準代碼並存時，需提示有無特別原由", example="0", required=false)
    private int notify_nhi_no;
    @ApiModelProperty(value="notify_nhi_no 清單", required=false)
    private java.util.List<String> lst_ntf_nhi_no;

    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="exclude_nhi_no 清單", required=false)
    private java.util.List<String> lst_nhi_no;
    
    @ApiModelProperty(value="單一住院就醫紀錄應用數量<=n次", example="5",required=false)
    private int max_inpatient;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNotify_nhi_no() {
        return notify_nhi_no;
    }

    public void setNotify_nhi_no(int notify_nhi_no) {
        this.notify_nhi_no = notify_nhi_no;
    }

    public java.util.List<String> getLst_ntf_nhi_no() {
        return lst_ntf_nhi_no;
    }

    public void setLst_ntf_nhi_no(java.util.List<String> lst_ntf_nhi_no) {
        this.lst_ntf_nhi_no = lst_ntf_nhi_no;
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

    public int getMax_inpatient() {
        return max_inpatient;
    }

    public void setMax_inpatient(int max_inpatient) {
        this.max_inpatient = max_inpatient;
    }

}



