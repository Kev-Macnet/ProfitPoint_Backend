package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class PlanSearchPl {
    @ApiModelProperty(value="名稱搜尋關鍵字", position=1, example="星光", required=true)
    private String searchName;
    @ApiModelProperty(value="每頁筆數", position=2, example="10", required=true)
    private int pageSize;
    @ApiModelProperty(value="頁數(由0起算)", position=3, example="2", required=true)
    private int pageIndex;

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

}







