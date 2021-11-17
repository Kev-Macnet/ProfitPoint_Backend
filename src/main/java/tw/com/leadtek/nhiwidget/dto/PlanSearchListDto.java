package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "計畫可收案病例清單 DTO")
public class PlanSearchListDto {
    @ApiModelProperty(value="資料總筆數", example="76", required=true, position=1)
    private long total;
    
    @ApiModelProperty(value="資料", required=true, position=2)
    private java.util.List<PlanSearchListData> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public java.util.List<PlanSearchListData> getData() {
        return data;
    }

    public void setData(java.util.List<PlanSearchListData> data) {
        this.data = data;
    }

}

class PlanSearchListData {
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








