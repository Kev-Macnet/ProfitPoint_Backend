package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;

public class AdditionalContent1Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    @Min(value = 0) @Max(value = 1)
    private int enable;
    @ApiModelProperty(value="案件分類", required=true, position=2)
    @NotNull()
    private java.util.List<String> category;
    
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public java.util.List<String> getCategory() {
        return category;
    }
    public void setCategory(java.util.List<String> category) {
        this.category = category;
    }
}


