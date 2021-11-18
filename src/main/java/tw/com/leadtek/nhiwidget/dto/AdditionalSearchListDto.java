package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl

@ApiModel(value = "總額外點數清單 DTO")
public class AdditionalSearchListDto {
    @ApiModelProperty(value="資料總筆數", example="76", required=true, position=1)
    private long total;
    
    @ApiModelProperty(value="總頁數", example="5", required=true, position=2)
    private long pages;
    
    @ApiModelProperty(value="資料", required=true, position=3)
    private java.util.List< AdditionalSearchListData> data;


    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public java.util.List<AdditionalSearchListData> getData() {
        return data;
    }

    public void setData(java.util.List<AdditionalSearchListData> data) {
        this.data = data;
    }

}
class AdditionalSearchListData {
    @ApiModelProperty(value="代碼", example="000001", required=true, position=1)
    private long id;
    @ApiModelProperty(value="是否啟用(1|0)", example="1", required=true, position=2)
    private int active;
    @ApiModelProperty(value="年度", example="110", required=true, position=3)
    private int syear;
    @ApiModelProperty(value="生效日(timestamp)", example="1625932800000", required=true, position=4)
    private String start_date;
    @ApiModelProperty(value="失效日(timestamp)", example="1627488000000", required=true, position=5)
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



