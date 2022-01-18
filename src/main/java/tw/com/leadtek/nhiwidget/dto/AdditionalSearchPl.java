package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;


public class AdditionalSearchPl {
    @ApiModelProperty(value="生效日", position=1, example="2021/07/11", required=true)
    @NotEmpty()
    private String start_date;
    @ApiModelProperty(value="失效日", position=2, example="2021/12/31", required=true)
    @NotEmpty()
    private String end_date;
    @ApiModelProperty(value="每頁筆數", position=3, example="10", required=true)
    @Positive()
    private int pageSize;
    @ApiModelProperty(value="頁數(由0起算)", position=4, example="2", required=true)
    @Positive()
    private int pageIndex;
    
    @ApiModelProperty(value="排序欄位(''/START_DATE/END_DATE)", position=5, example="START_DATE", required=false)
    private String sort_field;
    @ApiModelProperty(value="排序方向(ASC/DESC)", position=6, example="ASC", required=false)
    private String sort_direction;

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

    public String getSort_field() {
        return sort_field;
    }

    public void setSort_field(String sort_field) {
        this.sort_field = sort_field;
    }

    public String getSort_direction() {
        return sort_direction;
    }

    public void setSort_direction(String sort_direction) {
        this.sort_direction = sort_direction;
    }

    

}







