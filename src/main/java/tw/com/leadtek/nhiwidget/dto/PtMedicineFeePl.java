package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt藥費設定參數")
public class PtMedicineFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="藥費", required=true)
    private String category;

    @ApiModelProperty(value="每件給藥日數不得超過n日", example="3",required=false)
    private int max_nday;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMax_nday() {
        return max_nday;
    }

    public void setMax_nday(int max_nday) {
        this.max_nday = max_nday;
    }


}



