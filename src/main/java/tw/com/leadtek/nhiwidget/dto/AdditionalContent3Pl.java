package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;

public class AdditionalContent3Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    @Positive(message = "The 'enable' is required.")
    private int enable;
    @ApiModelProperty(value="支付代碼項目", required=true, position=2)
    @NotNull(message = "The 'nhi_no' is required.")
    private java.util.List<String> nhi_no;
    
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public java.util.List<String> getNhi_no() {
        return nhi_no;
    }
    public void setNhi_no(java.util.List<String> nhi_no) {
        this.nhi_no = nhi_no;
    }

}


