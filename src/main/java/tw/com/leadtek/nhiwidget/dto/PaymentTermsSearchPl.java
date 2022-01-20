package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


//參數說明: https://iter01.com/378925.html
@ApiModel(value="pt支付條件設定搜尋", description = "pt支付條件設定搜尋")
public class PaymentTermsSearchPl {

    @ApiModelProperty(value="院內碼", position=1, example="abc001", required=false)
    private String feeNo;
    @ApiModelProperty(value="支付標準代碼", position=2, example="def001", required=false)
    private String nhiNo;
    @ApiModelProperty(value="分類", position=3, example="門診診察費", required=false)
    private String category;
    @ApiModelProperty(value="生效日", position=4, example="2021/07/11 (1625932800000)", required=false)
    private String start_date;
    @ApiModelProperty(value="失效日", position=5, example="2021/07/29 (1627488000000)", required=false)
    private String end_date;
    @ApiModelProperty(value="每頁筆數", position=6, example="10", required=true)
    @Positive()
    private int pageSize;
    @ApiModelProperty(value="頁數(由0起算)", position=7, example="2", required=true)
    @Min(value = 0) @Max(value = 9999)
    private int pageIndex;
    @ApiModelProperty(value="排序欄位(''/FEE_NO/NHI_NO/CATEGORY/START_DATE/END_DATE, fee_no=院內碼, nhi_no=支付標準代碼, category=分類, start_date=生效日, end_date=失效日)", position=7, example="START_DATE", required=false)
    private String sort_field;
    @ApiModelProperty(value="排序方向(ASC/DESC)", position=8, example="ASC", required=false)
    private String sort_direction;
    
    // fee_no=院內碼, nhi_no=支付標準代碼, category=分類, start_date=生效日, end_date=失效日,
    public String getFeeNo() {
        return feeNo;
    }

    public void setFeeNo(String feeNo) {
        this.feeNo = feeNo;
    }

    public String getNhiNo() {
        return nhiNo;
    }

    public void setNhiNo(String nhiNo) {
        this.nhiNo = nhiNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getSort_field() {
        return sort_field;
    }

    public void setSort_field(String sort_field) {
        this.sort_field = sort_field;
    }

    public String getSort_direction() {
        return sort_direction;
    }

    public void setSort_direction(String sort_direction) {
        this.sort_direction = sort_direction;
    }
    
    

}



