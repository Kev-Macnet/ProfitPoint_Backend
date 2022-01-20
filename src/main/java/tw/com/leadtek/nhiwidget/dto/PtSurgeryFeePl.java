package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt手術費設定參數")
public class PtSurgeryFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="手術費", required=true, position=21)
    @NotEmpty()
    private String category;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=22)
    private int lim_age_enable;
    @ApiModelProperty(value="限定幾歲病患(年齡)開立", example="65", required=false, position=23)
    private int lim_age;

    @ApiModelProperty(value="限定特定科別應用(開關)", example="0", required=false, position=24)
    private int lim_division_enable;
    @ApiModelProperty(value="lim_division 清單", required=false, position=25)
    private java.util.List<String> lst_division;
    
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
    public int getLim_age_enable() {
        return lim_age_enable;
    }
    public void setLim_age_enable(int lim_age_enable) {
        this.lim_age_enable = lim_age_enable;
    }
    public int getLim_age() {
        return lim_age;
    }
    public void setLim_age(int lim_age) {
        this.lim_age = lim_age;
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



