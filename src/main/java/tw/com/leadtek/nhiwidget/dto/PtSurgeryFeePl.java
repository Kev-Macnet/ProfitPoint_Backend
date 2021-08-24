package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt手術費設定參數")
public class PtSurgeryFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="手術費", required=true)
    private String category;
    
    @ApiModelProperty(value="限定幾歲病患(年齡)開立", example="65", position=20, required=false)
    private int lim_age;

    @ApiModelProperty(value="限定特定科別應用", example="0", position=21, required=false)
    private int lim_division;
    @ApiModelProperty(value="lim_division 清單", position=24, required=false)
    private java.util.List<String> lst_division;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", example="0", position=23, required=false)
    private int exclude_nhi_no;
    @ApiModelProperty(value="exclude_nhi_no 清單", position=25, required=false)
    private java.util.List<String> lst_nhi_no;

//    @Override
    public String getCategory() {
        return category;
    }
//    @Override
    public void setCategory(String category) {
        this.category = category;
    }
    public int getLim_age() {
        return lim_age;
    }
    public void setLim_age(int lim_age) {
        this.lim_age = lim_age;
    }
    public int getLim_division() {
        return lim_division;
    }
    public void setLim_division(int lim_division) {
        this.lim_division = lim_division;
    }
    public int getExclude_nhi_no() {
        return exclude_nhi_no;
    }
    public void setExclude_nhi_no(int exclude_nhi_no) {
        this.exclude_nhi_no = exclude_nhi_no;
    }
    public java.util.List<String> getLst_division() {
        return lst_division;
    }
    public void setLst_division(java.util.List<String> lst_division) {
        this.lst_division = lst_division;
   }
    public java.util.List<String> getLst_nhi_no() {
        return lst_nhi_no;
    }
    public void setLst_nhi_no(java.util.List<String> lst_nhi_no) {
        this.lst_nhi_no = lst_nhi_no;
    }

}



