package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt石膏繃帶費設定參數") 
public class PtPlasterBandageFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="石膏繃帶費", required=true)
    private String category;

    public String getCategory() {
        return category.replaceAll("\'", "\'\'");
    }

    public void setCategory(String category) {
        this.category = category;
    }


}



