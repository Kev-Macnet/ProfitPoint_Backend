package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;




public class AdditionalContent2Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    private int enable;
    @ApiModelProperty(value="支付標準代碼", example="nhi001", required=true, position=2)
    private String nhi_no;
    @ApiModelProperty(value="醫令類別清單", required=true, position=3)
    private java.util.List<String> cpoe;
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
    public java.util.List<String> getCpoe() {
        return cpoe;
    }
    public void setCpoe(java.util.List<String> cpoe) {
        this.cpoe = cpoe;
    }
}

