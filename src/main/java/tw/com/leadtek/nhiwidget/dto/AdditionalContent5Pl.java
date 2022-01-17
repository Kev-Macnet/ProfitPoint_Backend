package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;

public class AdditionalContent5Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    @Positive(message = "The 'enable' is required.")
    private int enable;
    @ApiModelProperty(value="資料清單", required=true, position=2)
    @NotNull(message = "The 'data' is required.")
    private java.util.List<AdditionalContent5ListPl> data;
    
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public java.util.List<AdditionalContent5ListPl> getData() {
        return data;
    }
    public void setData(java.util.List<AdditionalContent5ListPl> data) {
        this.data = data;
    }
    
}


