package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl


public class AdditionalContent3Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    private int enable;
    @ApiModelProperty(value="支付代碼項目", example="nhi001", required=true, position=2)
    private String nhi_no;
    
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public String getNhi_no() {
        return nhi_no;
    }
    public void setNhi_no(String nhi_no) {
        this.nhi_no = nhi_no;
    }

}


