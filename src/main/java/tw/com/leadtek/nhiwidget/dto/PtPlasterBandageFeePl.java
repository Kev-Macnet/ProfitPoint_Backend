package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt石膏繃帶費設定參數") 
public class PtPlasterBandageFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="石膏繃帶費", required=true)
    @NotEmpty()
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}



