package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value="pt支付條件設定搜尋 DTO", description = "pt支付條件設定搜尋 DTO")
public class PaymentTermsSearchDto {
    @ApiModelProperty(value="資料總筆數", example="76", required=true, position=1)
    private long total;
    
    @ApiModelProperty(value="資料", required=true, position=2)
    private java.util.List<PaymentTermsSearchData> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public java.util.List<PaymentTermsSearchData> getData() {
        return data;
    }

    public void setData(java.util.List<PaymentTermsSearchData> data) {
        this.data = data;
    }
    
}

class PaymentTermsSearchData extends PaymentTermsPl {
    @ApiModelProperty(value="代碼", example="000001", required=true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}


