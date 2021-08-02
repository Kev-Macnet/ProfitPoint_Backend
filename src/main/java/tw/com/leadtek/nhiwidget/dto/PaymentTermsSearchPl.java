package tw.com.leadtek.nhiwidget.dto;

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

}



