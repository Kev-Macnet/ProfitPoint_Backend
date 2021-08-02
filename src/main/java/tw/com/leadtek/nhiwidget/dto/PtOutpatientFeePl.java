package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


//參數說明: https://iter01.com/378925.html
@ApiModel(value = "pt門診診察費設定參數")
public class PtOutpatientFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="門診診察費", required=true)
    private String category;

    @ApiModelProperty(value="不含牙醫(0|1)", position=1, example="1", required=true)
    private int no_dentisit;
    
    @ApiModelProperty(value="不含中醫(0|1)", position=2, example="1", required=true)
    private int no_chi_medicine;

    @ApiModelProperty(value="不得同時申報藥事服務費", position=3, example="0", required=false)
    private int no_service_charge;
    
    @ApiModelProperty(value="限定離島區域申報", position=4, example="0", required=false)
    private int lim_out_islands;
    @ApiModelProperty(value="限定假日加計使用", position=5, example="0", required=false)
    private int lim_holiday;
    @ApiModelProperty(value="限定單一醫師、護理人員、藥師執行此醫令單月上限", position=6, example="0", required=false)
    private int lim_max;
    
    @ApiModelProperty(value="限定幾歲病患(年齡)開立", position=7, example="65", required=false)
    private int lim_age;
    @ApiModelProperty(value="(1<未滿, 2>=大於等於 3<=小於等於)", position=8, example="2", required=false)
    private int lim_age_type;
    
    @ApiModelProperty(value="限定特定科別應用", position=9, example="0", required=false)
    private int lim_division;
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", position=10, example="0", required=false)
    private int exclude_nhi_no;
    
    @ApiModelProperty(value="限定特定科別應用清單", position=11, required=false)
    private java.util.List<String> lst_division;
    @ApiModelProperty(value="不可與此支付標準代碼並存單一就醫紀錄一併申報", position=12, required=false)
    private java.util.List<String> lst_nhi_no;
    
    public String getCategory() {
        return category;
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
    public int getLim_max() {
        return lim_max;
    }
    public void setLim_max(int lim_max) {
        this.lim_max = lim_max;
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

    /*
    no_dentisit smallint default 1 comment '不含牙醫',
    no_chi_medicine smallint default 1 comment '不含中醫',
    no_service_charge smallint comment '不得同時申報藥事服務費',
    lim_out_islands smallint comment '限定離島區域申報',
    lim_holiday smallint comment '限定假日加計使用',
    lim_max smallint default 0 comment '限定單一醫師、護理人員、藥師執行此醫令單月上限',
    lim_age smallint default 0 comment '限定幾歲病患(年齡)開立',
    lim_age_type smallint comment '(1<未滿, 2>=大於等於 3<=小於等於)',
    lim_division smallint default 0 comment '限定特定科別應用',
    exclude_nhi_no smallint default 0 comment '不可與此支付標準代碼並存單一就醫紀錄一併申報',
    */

}



