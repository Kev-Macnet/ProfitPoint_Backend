package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


//參數說明: https://iter01.com/378925.html
@ApiModel(value = "pt門診診察費設定參數")
public class PtOutpatientFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="門診診察費", required=true)
    private String category;

    @ApiModelProperty(value="不含牙醫(0|1)", position=21, example="1", required=false)
    private int no_dentisit;
    
    @ApiModelProperty(value="不含中醫(0|1)", position=22, example="1", required=false)
    private int no_chi_medicine;

    @ApiModelProperty(value="不得同時申報藥事服務費", position=23, example="0", required=false)
    private int no_service_charge;
    
    @ApiModelProperty(value="限定離島區域申報", position=24, example="0", required=false)
    private int lim_out_islands;
    @ApiModelProperty(value="限定假日加計使用", position=25, example="0", required=false)
    private int lim_holiday;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=26)
    private int lim_max_enable;
    @ApiModelProperty(value="限定單一醫師、護理人員、藥師執行此醫令單月上限", position=27, example="0", required=false)
    private int lim_max;
    
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=false, position=28)
    private int lim_age_enable;
    @ApiModelProperty(value="限定幾歲病患(年齡)開立", position=29, example="65", required=false)
    private int lim_age;
    @ApiModelProperty(value="(1<未滿, 2>=大於等於 3<=小於等於)", position=30, example="2", required=false)
    private int lim_age_type;
    
    @ApiModelProperty(value="限定特定科別應用(開關)", position=31, example="0", required=false)
    private int lim_division_enable;
    @ApiModelProperty(value="lim_division 清單", position=32, required=false)
    private java.util.List<String> lst_division;
    
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報(開關)", position=33, example="0", required=false)
    private int exclude_nhi_no_enable;
    @ApiModelProperty(value="exclude_nhi_no 清單", position=34, required=false)
    private java.util.List<String> lst_nhi_no;
    public String getCategory() {
        return category.replaceAll("\'", "\'\'");
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getNo_dentisit() {
        return no_dentisit;
    }
    public void setNo_dentisit(int no_dentisit) {
        this.no_dentisit = no_dentisit;
    }
    public int getNo_chi_medicine() {
        return no_chi_medicine;
    }
    public void setNo_chi_medicine(int no_chi_medicine) {
        this.no_chi_medicine = no_chi_medicine;
    }
    public int getNo_service_charge() {
        return no_service_charge;
    }
    public void setNo_service_charge(int no_service_charge) {
        this.no_service_charge = no_service_charge;
    }
    public int getLim_out_islands() {
        return lim_out_islands;
    }
    public void setLim_out_islands(int lim_out_islands) {
        this.lim_out_islands = lim_out_islands;
    }
    public int getLim_holiday() {
        return lim_holiday;
    }
    public void setLim_holiday(int lim_holiday) {
        this.lim_holiday = lim_holiday;
    }
    public int getLim_max_enable() {
        return lim_max_enable;
    }
    public void setLim_max_enable(int lim_max_enable) {
        this.lim_max_enable = lim_max_enable;
    }
    public int getLim_max() {
        return lim_max;
    }
    public void setLim_max(int lim_max) {
        this.lim_max = lim_max;
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
    public int getLim_age_type() {
        return lim_age_type;
    }
    public void setLim_age_type(int lim_age_type) {
        this.lim_age_type = lim_age_type;
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



