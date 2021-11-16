package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl

@ApiModel(value = "總額外點數清單 DTO")
public class AdditionalConditionListDto {
    @ApiModelProperty(value="代碼", example="000001", required=true)
    private long id;
    @ApiModelProperty(value="是否啟用(1|0)", example="1", required=true, position=1)
    private int active;
    @ApiModelProperty(value="年度", example="110", required=true, position=2)
    private int syear;
    @ApiModelProperty(value="生效日(timestamp)", example="1625932800000", required=true, position=3)
    private String start_date;
    @ApiModelProperty(value="失效日(timestamp)", example="1627488000000", required=true, position=4)
    private String end_date;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getActive() {
        return active;
    }
    public void setActive(int active) {
        this.active = active;
    }
    public int getSyear() {
        return syear;
    }
    public void setSyear(int syear) {
        this.syear = syear;
    }
    public String getStart_date() {
        return start_date;
    }
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
    public String getEnd_date() {
        return end_date;
    }
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

}



