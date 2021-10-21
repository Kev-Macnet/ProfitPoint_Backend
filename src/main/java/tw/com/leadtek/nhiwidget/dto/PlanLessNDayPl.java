package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;

public class PlanLessNDayPl {
//    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
//    private int enable;
    @ApiModelProperty(value="ICD診斷碼", example="icd-0001", required=true, position=2)
    private String icd_no;
    @ApiModelProperty(value="就醫天數少於 n day", example="20", required=true, position=3)
    private int nday;

    public String getIcd_no() {
        return icd_no;
    }
    public void setIcd_no(String icd_no) {
        this.icd_no = icd_no;
    }
    public int getNday() {
        return nday;
    }
    public void setNday(int nday) {
        this.nday = nday;
    }
    
    
}

