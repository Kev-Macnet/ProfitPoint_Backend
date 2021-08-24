package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt麻醉費設定參數")
public class PtAnesthesiaFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="麻醉費", required=true)
    private String category;
    
//    include_drg_no smallint default 0 comment '單一就醫紀錄上，須包含以下任一DRG代碼-list',
//    coexist_nhi_no smallint default 0 comment '需與以下任一支付標準代碼並存-list',
//    over_times smallint default 0 comment '超過 n 次時，首次需>first_n分鐘，後續每次執行需間隔>next_n分鐘',
//    over_times_n smallint,
//    over_times_first_n smallint,
//    over_times_next_n smallint,
//    lim_division smallint default 0 comment '限定特定科別應用-list',
    
    @ApiModelProperty(value="單一就醫紀錄上，須包含以下任一DRG代碼", example="0", position=20, required=false)
    private int include_drg_no;
    @ApiModelProperty(value="include_drg_no 清單", position=21, required=false)
    private java.util.List<String> lst_drg_no;
    
    @ApiModelProperty(value="需與以下任一支付標準代碼並存", example="0", position=22,required=false)
    private int coexist_nhi_no;
    @ApiModelProperty(value="coexist_nhi_no 清單", position=23, required=false)
    private java.util.List<String> lst_co_nhi_no;

    @ApiModelProperty(value="限定特定科別應用", example="0", position=24, required=false)
    private int lim_division;
    @ApiModelProperty(value="lim_division 清單", position=25, required=false)
    private java.util.List<String> lst_division;

    @ApiModelProperty(value="超過 n 次時，首次需>first_n分鐘，後續每次執行需間隔>next_n分鐘", example="0", position=26, required=false)
    private int over_times;
    @ApiModelProperty(value="超過 n 次", example="2", position=27, required=false)
    private int over_times_n;
    @ApiModelProperty(value="首次需>first_n分鐘", example="50", position=28, required=false)
    private int over_times_first_n;
    @ApiModelProperty(value="後續每次執行需間隔>next_n分鐘", example="20", position=29, required=false)
    private int over_times_next_n;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getInclude_drg_no() {
        return include_drg_no;
    }
    public void setInclude_drg_no(int include_drg_no) {
        this.include_drg_no = include_drg_no;
    }
    public java.util.List<String> getLst_drg_no() {
        return lst_drg_no;
    }
    public void setLst_drg_no(java.util.List<String> lst_drg_no) {
        this.lst_drg_no = lst_drg_no;
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
    public int getLim_division() {
        return lim_division;
    }
    public void setLim_division(int lim_division) {
        this.lim_division = lim_division;
    }
    public java.util.List<String> getLst_division() {
        return lst_division;
    }
    public void setLst_division(java.util.List<String> lst_division) {
        this.lst_division = lst_division;
    }
    public int getOver_times() {
        return over_times;
    }
    public void setOver_times(int over_times) {
        this.over_times = over_times;
    }
    public int getOver_times_n() {
        return over_times_n;
    }
    public void setOver_times_n(int over_times_n) {
        this.over_times_n = over_times_n;
    }
    public int getOver_times_first_n() {
        return over_times_first_n;
    }
    public void setOver_times_first_n(int over_times_first_n) {
        this.over_times_first_n = over_times_first_n;
    }
    public int getOver_times_next_n() {
        return over_times_next_n;
    }
    public void setOver_times_next_n(int over_times_next_n) {
        this.over_times_next_n = over_times_next_n;
    }

}
