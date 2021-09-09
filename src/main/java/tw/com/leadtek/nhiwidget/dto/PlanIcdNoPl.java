package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class PlanIcdNoPl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    private int enable;
    @ApiModelProperty(value="ICD診斷碼", example="icd-0001", required=true, position=2)
    private String icd_no;
    
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public String getIcd_no() {
        return icd_no;
    }
    public void setIcd_no(String icd_no) {
        this.icd_no = icd_no;
    }
}







