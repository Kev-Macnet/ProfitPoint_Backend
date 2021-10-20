package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;

public class PlanMoreTimesPl {
//    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
//    private int enable;
    @ApiModelProperty(value="ICD診斷碼", example="icd-0001", required=true, position=2)
    private String icd_no;
    @ApiModelProperty(value="就醫次數大於 n 次", example="10", required=true, position=3)
    private int times;
    
    public String getIcd_no() {
        return icd_no.replaceAll("\'", "\'\'");
    }
    public void setIcd_no(String icd_no) {
        this.icd_no = icd_no;
    }
    public int getTimes() {
        return times;
    }
    public void setTimes(int times) {
        this.times = times;
    }

}





