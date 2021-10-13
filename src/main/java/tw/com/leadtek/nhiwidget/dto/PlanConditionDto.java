package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "計畫可收案病例條件參數 DTO")
public class PlanConditionDto extends PlanConditionPl {
    @ApiModelProperty(value="代碼", example="000001", required=true, position=1)
    private long id;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}








