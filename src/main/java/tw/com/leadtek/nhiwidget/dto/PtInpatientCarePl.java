package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt住院安寧療護設定參數")
public class PtInpatientCarePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="住院安寧療護", required=true)
    private String category;

    public String getCategory() {
        return category.replaceAll("\'", "\'\'");
    }

    public void setCategory(String category) {
        this.category = category;
    }


}



