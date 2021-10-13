package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class AdditionalContent2Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    private int enable;
    @ApiModelProperty(value="資料清單", required=true, position=2)
    private java.util.List<AdditionalContent2ListPl> data;
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public java.util.List<AdditionalContent2ListPl> getData() {
        return data;
    }
    public void setData(java.util.List<AdditionalContent2ListPl> data) {
        this.data = data;
    }
}

