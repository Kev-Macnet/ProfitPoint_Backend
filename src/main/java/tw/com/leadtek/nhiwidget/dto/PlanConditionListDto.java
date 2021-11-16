package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "計畫可收案病例清單 DTO")
public class PlanConditionListDto {
    @ApiModelProperty(value="代碼", example="000001", required=true, position=1)
    private long id;
    @ApiModelProperty(value="計畫名稱", example="星光計畫", required=true, position=2)
    private String name;
    @ApiModelProperty(value="就醫科別", example="心臟科", required=true, position=3)
    private String division;
    @ApiModelProperty(value="是否啟用(1|0)", example="1", required=true, position=4)
    private int active;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDivision() {
        return division;
    }
    public void setDivision(String division) {
        this.division = division;
    }
    public int getActive() {
        return active;
    }
    public void setActive(int active) {
        this.active = active;
    }

}








