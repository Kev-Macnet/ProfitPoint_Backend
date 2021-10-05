package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value="總額外點數條件參數 DTO", description = "總額外點數條件參數 DTO")
public class AdditionalConditionDto extends AdditionalConditionPl {
    @ApiModelProperty(value="代碼", example="000001", required=true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}


