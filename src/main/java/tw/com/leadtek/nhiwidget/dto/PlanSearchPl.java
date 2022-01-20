package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;


public class PlanSearchPl {
    @ApiModelProperty(value="名稱搜尋關鍵字", position=1, example="星光", required=true)
    @NotEmpty()
    private String searchName;
    @ApiModelProperty(value="每頁筆數", position=2, example="10", required=true)
    @Positive()
    private int pageSize;
    @ApiModelProperty(value="頁數(由0起算)", position=3, example="2", required=true)
    @Min(value = 0) @Max(value = 9999)
    private int pageIndex;
    @ApiModelProperty(value="排序欄位(''/DIVISION/PLAN_NAME)", position=4, example="DIVISION", required=false)
    private String sort_field;
    @ApiModelProperty(value="排序方向(ASC/DESC)", position=5, example="ASC", required=false)
    private String sort_direction;

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
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







