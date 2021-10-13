package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;

public class AdditionalContent5ListPl {
    @ApiModelProperty(value="主診斷碼", example="icd001", required=true, position=2)
    private String icd_no;
    @ApiModelProperty(value="支付標準代碼", example="nhi001", required=true, position=3)
    private String nhi_no;
    @ApiModelProperty(value="醫令類別清單", required=true, position=4)
    private java.util.List<String> cpoe;
    
    public String getIcd_no() {
        return icd_no;
    }
    public void setIcd_no(String icd_no) {
        this.icd_no = icd_no;
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


