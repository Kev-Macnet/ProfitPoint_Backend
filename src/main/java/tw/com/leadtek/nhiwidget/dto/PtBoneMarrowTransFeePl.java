package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt輸血及骨髓移植費設定參數")
public class PtBoneMarrowTransFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="輸血及骨髓移植費", required=true)
    private String category;

    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", required=false)
    private int coexist_nhi_no_enable;
    @ApiModelProperty(value="coexist_nhi_no 清單", required=false)
    private java.util.List<String> lst_co_nhi_no;
    
    @ApiModelProperty(value="參與計畫之病患，不得申報", example="0", required=false)
    private int not_allow_plan_enable;
    @ApiModelProperty(value="計畫 list", required=false)
    private java.util.List<String> lst_allow_plan;
    
    @ApiModelProperty(value="限定特定科別應用", position=9, example="0", required=false)
    private int lim_division_enable;
    @ApiModelProperty(value="lim_division 清單", position=11, required=false)
    private java.util.List<String> lst_division;
    
    public String getCategory() {
        return category.replaceAll("\'", "\'\'");
    }
    public void setCategory(String category) {
        this.category = category;
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
    public int getNot_allow_plan_enable() {
        return not_allow_plan_enable;
    }
    public void setNot_allow_plan_enable(int not_allow_plan_enable) {
        this.not_allow_plan_enable = not_allow_plan_enable;
    }
    public java.util.List<String> getLst_allow_plan() {
        return lst_allow_plan;
    }
    public void setLst_allow_plan(java.util.List<String> lst_allow_plan) {
        this.lst_allow_plan = lst_allow_plan;
    }
    public int getLim_division_enable() {
        return lim_division_enable;
    }
    public void setLim_division_enable(int lim_division_enable) {
        this.lim_division_enable = lim_division_enable;
    }
    public java.util.List<String> getLst_division() {
        return lst_division;
    }
    public void setLst_division(java.util.List<String> lst_division) {
        this.lst_division = lst_division;
    }
    
}



