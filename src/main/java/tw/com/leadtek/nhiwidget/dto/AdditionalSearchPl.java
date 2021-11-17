package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class AdditionalSearchPl {
    @ApiModelProperty(value="生效日", position=1, example="2021/07/11 (1625932800000)", required=true)
    private String start_date;
    @ApiModelProperty(value="失效日", position=2, example="2021/07/29 (1627488000000)", required=true)
    private String end_date;
    @ApiModelProperty(value="每頁筆數", position=3, example="10", required=true)
    private int pageSize;
    @ApiModelProperty(value="頁數(由0起算)", position=4, example="2", required=true)
    private int pageIndex;


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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

}







