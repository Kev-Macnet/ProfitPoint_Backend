package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class ptNhiNoTimes {
    @ApiModelProperty(value="支付代碼", example="nhi-0001", required=false, position=1)
    private String nhi_no;
    @ApiModelProperty(value=">= 次數", example="3", required=false, position=2)
    private int times;
    
    public String getNhi_no() {
        return nhi_no;
    }
    public void setNhi_no(String nhi_no) {
        this.nhi_no = nhi_no;
    }
    public int getTimes() {
        return times;
    }
    public void setTimes(int times) {
        this.times = times;
    }

}







