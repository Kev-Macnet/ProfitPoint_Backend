package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt石膏繃帶費設定 DTO")
public class PtPlasterBandageFeeDto extends PtPlasterBandageFeePl {
    @ApiModelProperty(value="代碼", example="000001", required=true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}


